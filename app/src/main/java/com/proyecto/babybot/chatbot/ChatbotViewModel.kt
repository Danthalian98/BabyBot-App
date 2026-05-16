package com.proyecto.babybot.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.babybot.BuildConfig
import com.proyecto.babybot.ModeracionUtil
import com.proyecto.babybot.data.local.dao.BabyDao
import com.proyecto.babybot.data.local.dao.ChatDao
import com.proyecto.babybot.data.local.dao.MealDao
import com.proyecto.babybot.data.local.dao.SleepDao
import com.proyecto.babybot.data.local.entity.ChatHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val babyDao: BabyDao,
    private val sleepDao: SleepDao,
    private val mealDao: MealDao,
    private val chatDao: ChatDao
) : ViewModel() {
    private val generativeModel = GenerativeModel(
        modelName = "models/gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )
    private var currentRequestId = 0
    private var chatSession = generativeModel.startChat()
    private var hasShownWelcome = false


    private val _state = MutableStateFlow(ChatbotState())
    val state: StateFlow<ChatbotState> = _state

    init {
        loadChatHistory()

        if (!hasShownWelcome) {
            showWelcomeMessage()
            hasShownWelcome = true
        }
    }

    fun onMessageChange(text: String) {
        _state.update { it.copy(currentMessage = text) }
    }

    private fun showWelcomeMessage() {

        if (_state.value.messages.isNotEmpty()) return

        val welcome = ChatMessage(
            text = """
            Hola 👋 Soy BabyBot, tu asistente de apoyo en el cuidado de tu bebé.

            Puedo ayudarte con:
                • sueño
                • alimentación
                • rutinas
                • señales de alerta
                • consejos generales pediátricos

            Recuerda que esta información es orientativa y no reemplaza a tu pediatra.
            """.trimIndent(),
            time = currentTime(),
            isUser = false
        )

        _state.update {
            it.copy(messages = listOf(welcome))
        }
    }

    fun sendMessage() {
        val userText = state.value.currentMessage
        detectProfileUpdate(userText)
        if (userText.isBlank() || state.value.isLoading) return

        // --- INICIO FILTRADO DE LENGUAJE ---
        if (!ModeracionUtil.esContenidoSeguro(userText)) {
            val warningMessage = ChatMessage(
                text = "Tu mensaje contiene lenguaje inapropiado. Por favor, mantengamos el respeto en la comunidad.",
                time = currentTime(),
                isUser = false
            )
            _state.update {
                it.copy(
                    messages = it.messages + warningMessage,
                    currentMessage = "" // Limpiamos el input
                )
            }
            return // Bloqueamos el envío a Gemini
        }
        // --- FIN FILTRADO DE LENGUAJE ---

        val userMessage = ChatMessage(text = userText, time = currentTime(), isUser = true)

        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                currentMessage = "",
                isLoading = true,
                error = null
            )
        }

        val requestId = ++currentRequestId

        viewModelScope.launch {
            try {
                val contextResult = repository.searchInKnowledge(userText)
                val finalContext = if (contextResult.contains("No hay registros")) "" else contextResult

                // 1. OBTENEMOS EL UID (Necesario para el DAO)
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                // 2. OBTENEMOS EL OBJETO BABY
                val baby = babyDao.getBaby(currentUid)
                val babyContext = getBabyLocalContext()

                val historyFromRoom =
                    chatDao.getLastMessages(currentUid, limit = 20)

                if (historyFromRoom.isNotEmpty()) {

                    chatSession = generativeModel.startChat(
                        history = historyFromRoom.reversed().map {
                            content(if (it.isUser) "user" else "model") {
                                text(it.message)
                            }
                        }
                    )
                }

                val prompt = """
                Eres BabyBot, un asistente experto en pediatría. Tienes acceso a una base de conocimiento local y a los datos del bebé.

                <contexto_conocimiento_especifico>
                $finalContext
                </contexto_conocimiento_especifico>
        
                <datos_del_bebe_usuario>
                $babyContext
                </datos_del_bebe_usuario>

                INSTRUCCIONES DE RESPUESTA:
                1. Si la respuesta está en <contexto_conocimiento_especifico>, úsala y cita la fuente.
                2. Si la información NO está en el contexto o es insuficiente, USA TU CONOCIMIENTO GENERAL de pediatría para responder de forma segura y profesional, pero si existe información relacionada en el contexto, incluye sus fuentes para reforzar la seguridad.
                3. Siempre personaliza la respuesta usando los <datos_del_bebe_usuario>.
                4. IMPORTANTE: Si el usuario pregunta algo que represente un riesgo vital, indica claramente que debe acudir a urgencias.
                5. Mantén siempre el aviso: "Esta es una guía informativa, consulta siempre a tu pediatra ${baby?.pediatra ?: "de confianza"}".
                
                MENSAJE ACTUAL DEL USUARIO:
                "$userText"
                """.trimIndent()

                val response = chatSession.sendMessage(prompt)

                if (requestId != currentRequestId) return@launch

                //FILTRO DE SALIDA
                var botReplyText = response.text ?: "Lo siento, no pude procesar la respuesta."
                if (!ModeracionUtil.esContenidoSeguro(botReplyText)) {
                    botReplyText = "La respuesta generada no cumple con las políticas de seguridad. Por favor, intenta de nuevo."
                }

                val botMessage = ChatMessage(
                    text = botReplyText,
                    time = currentTime(),
                    isUser = false
                )

                _state.update {
                    it.copy(messages = it.messages + botMessage)
                }

                chatDao.insertMessage(
                    ChatHistoryEntity(idUsuario = currentUid, message = userText, isUser = true)
                )
                chatDao.insertMessage(
                    ChatHistoryEntity(idUsuario = currentUid, message = botReplyText, isUser = false)
                )

            } catch (e: Exception) {
                val errorMsg = if (e.localizedMessage?.contains("429") == true) {
                    "Estamos recibiendo muchas preguntas. Por favor, intenta de nuevo en un minuto."
                } else {
                    "Error de conexión. Revisa tu internet."
                }

                val errorReply = ChatMessage(text = errorMsg, time = currentTime(), isUser = false)
                _state.update { it.copy(messages = it.messages + errorReply, error = e.localizedMessage) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun detectProfileUpdate(text: String) {

        val lower = text.lowercase()

        val numberRegex = Regex("""(\d+(\.\d+)?)""")

        // ---------------- ALLERGIAS ----------------

        val allergyTriggers = listOf(
            "alérgico a",
            "alergia a",
            "es alérgico al",
            "es alérgica al",
            "tiene alergia a",
            "le da alergia",
            "resultó alérgico a"
        )

        allergyTriggers.firstOrNull { lower.contains(it) }?.let { trigger ->

            val rawText = lower
                .substringAfter(trigger)
                .replace(Regex("[^a-zA-Záéíóúñ\\sy,]"), "")
                .trim()

            val allergies = rawText
                .split(" y ", ",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (allergies.isNotEmpty()) {

                _state.update {
                    it.copy(
                        pendingUpdate = BabyUpdate(
                            field = "alergias",
                            values = allergies
                        )
                    )
                }

                return
            }
        }

        // ---------------- PESO ----------------

        val weightTriggers = listOf(
            "pesa",
            "peso",
            "ahora pesa",
            "anda en",
            "subió a",
            "está pesando"
        )

        if (weightTriggers.any { lower.contains(it) }) {

            val match = numberRegex.find(lower)

            match?.value?.let { weight ->

                _state.update {
                    it.copy(
                        pendingUpdate = BabyUpdate(
                            field = "peso",
                            values = listOf(weight)
                        )
                    )
                }

                return
            }
        }

        // ---------------- TALLA ----------------

        val heightTriggers = listOf(
            "mide",
            "altura",
            "estatura",
            "ya mide",
            "creció hasta"
        )

        if (heightTriggers.any { lower.contains(it) }) {

            val match = numberRegex.find(lower)

            match?.value?.let { height ->

                _state.update {
                    it.copy(
                        pendingUpdate = BabyUpdate(
                            field = "talla",
                            values = listOf(height)
                        )
                    )
                }

                return
            }
        }

        // ---------------- PEDIATRA ----------------

        val doctorTriggers = listOf(
            "pediatra es",
            "doctor es",
            "doctora es",
            "nuevo pediatra",
            "su pediatra ahora es"
        )

        doctorTriggers.firstOrNull {
            lower.contains(it)
        }?.let { trigger ->

            val doctor = lower
                .substringAfter(trigger)
                .trim()

            if (doctor.isNotBlank()) {

                _state.update {
                    it.copy(
                        pendingUpdate = BabyUpdate(
                            field = "pediatra",
                            values = listOf(doctor)
                        )
                    )
                }
            }
        }
    }

    fun loadChatHistory() {
        val currentUid =
            FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            val history = chatDao.getAllMessages(currentUid)

            val mapped = history.map {
                ChatMessage(
                    text = it.message,
                    time = currentTime(),
                    isUser = it.isUser
                )
            }

            _state.update {
                it.copy(messages = mapped)
            }
        }
    }

    private suspend fun getBabyLocalContext(): String {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                ?: return "Usuario no autenticado."

            val baby =
                babyDao.getBaby(currentUid) ?: return "No hay un perfil de bebé registrado aún."

            val lastMeal = mealDao.getLastMeal(baby.idBebe)
            val lastSleep = sleepDao.getLastSleep(baby.idBebe)

            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val edadMeses = ((System.currentTimeMillis() - baby.fechaNacimiento) / (1000L * 60 * 60 * 24 * 30)).toInt()

            """
        PERFIL DEL BEBÉ:
        - Nombre: ${baby.nombre}
        - Edad: $edadMeses meses
        - Peso: ${baby.peso} kg
        - Alergias: ${if (baby.alergias.isEmpty()) "Ninguna" else baby.alergias.joinToString()}
        
        ACTIVIDAD RECIENTE:
        - Última comida: ${lastMeal?.alimentoDescripcion ?: "Sin registros"} (${lastMeal?.tipo ?: ""}).
        - Último sueño: ${if (lastSleep != null) "Durmió de ${sdf.format(Date(lastSleep.inicio))} a ${sdf.format(Date(lastSleep.fin))} (Calidad: ${lastSleep.calidad ?: "N/A"})" else "No hay registros"}.
        """.trimIndent()
        } catch (e: Exception) {
            "Error al acceder a los datos locales."
        }
    }

    fun clearChatHistory() {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        currentUid?.let { uid ->
            viewModelScope.launch {
                chatDao.deleteHistory(uid)
                // Opcional: Limpiar también la lista de mensajes en la UI
                _state.update { it.copy(messages = emptyList()) }
            }
        }
    }

    private fun currentTime(): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date(timestamp))
    }

    fun confirmUpdate() {

        val update = state.value.pendingUpdate ?: return

        viewModelScope.launch {

            val currentUid =
                FirebaseAuth
                    .getInstance()
                    .currentUser
                    ?.uid ?: return@launch

            val baby = babyDao.getBaby(currentUid)
                ?: return@launch

            val updatedBaby = when (update.field) {

                "alergias" -> {
                    baby.copy(
                        alergias = (
                                baby.alergias + update.values
                    ).distinct())
                }

                "peso" -> {
                    baby.copy(
                        peso = update.values.firstOrNull()?.toDoubleOrNull()
                            ?: baby.peso
                    )
                }

                "talla" -> {
                    baby.copy(
                        talla = update.values.firstOrNull()?.toDoubleOrNull()
                            ?: baby.talla
                    )
                }

                "pediatra" -> {
                    baby.copy(
                        pediatra = update.values.toString()
                    )
                }

                else -> baby
            }

            babyDao.updateBaby(updatedBaby)

            val confirmationMessage = ChatMessage(
                text = "He actualizado ${update.field} correctamente.",
                time = currentTime(),
                isUser = false
            )

            _state.update {
                it.copy(
                    pendingUpdate = null,
                    messages = it.messages + confirmationMessage
                )
            }
        }
    }

    fun clearPendingUpdate() {
        _state.update {
            it.copy(
                pendingUpdate = null
            )
        }
    }
}