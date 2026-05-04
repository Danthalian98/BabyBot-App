package com.proyecto.babybot.chatbot

import android.R.attr.text
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.proyecto.babybot.BuildConfig
import com.proyecto.babybot.ModeracionUtil
import com.proyecto.babybot.data.local.dao.BabyDao
import com.proyecto.babybot.data.local.dao.MealDao
import com.proyecto.babybot.data.local.dao.SleepDao
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
    private val mealDao: MealDao
) : ViewModel() {

    private val generativeModel = GenerativeModel(
        modelName = "models/gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val _state = MutableStateFlow(ChatbotState())
    val state: StateFlow<ChatbotState> = _state

    fun onMessageChange(text: String) {
        _state.update { it.copy(currentMessage = text) }
    }

    fun sendMessage() {
        val userText = state.value.currentMessage
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
        val userEntity = ChatEntity(
            autor = "user",
            contenido = userText,
            fecha = com.google.firebase.Timestamp.now()
        )

        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                currentMessage = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                val contextResult = repository.searchInKnowledge(userText)
                val finalContext = if (contextResult.contains("No hay registros")) "" else contextResult

                // 1. OBTENEMOS EL UID (Necesario para el DAO)
                val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

                // 2. OBTENEMOS EL OBJETO BABY (Para sacar el nombre del pediatra)
                val baby = if (currentUid != null) babyDao.getBaby(currentUid) else null

                // 3. OBTENEMOS EL STRING DE CONTEXTO (Para el prompt)
                val babyContext = getBabyLocalContext()

                val chatSession = generativeModel.startChat(
                    history = state.value.messages.map {
                        content(if (it.isUser) "user" else "model") { text(it.text) }
                    }
                )

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
                2. Si la información NO está en el contexto o es insuficiente, USA TU CONOCIMIENTO GENERAL de pediatría para responder de forma segura y profesional, pero SIEMPRE agrega las fuentes del contexto, para mantener la seguridad.
                3. Siempre personaliza la respuesta usando los <datos_del_bebe_usuario>.
                4. IMPORTANTE: Si el usuario pregunta algo que represente un riesgo vital, indica claramente que debe acudir a urgencias.
                5. Mantén siempre el aviso: "Esta es una guía informativa, consulta siempre a tu pediatra ${baby?.pediatra ?: "de confianza"}".
                """.trimIndent()

                val response = chatSession.sendMessage(prompt)

                // --- FILTRO DE SALIDA (por seguridad extra) ---
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

    private suspend fun getBabyLocalContext(): String {
        return try {
            // Obtenemos el ID del usuario actual de Firebase
            val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

            if (currentUid == null) return "Usuario no autenticado."

            // Ahora sí le pasamos el ID que el DAO está esperando
            val baby = babyDao.getBaby(currentUid)

            if (baby == null) return "No hay un perfil de bebé registrado aún."

            val edadMeses = ((System.currentTimeMillis() - baby.fechaNacimiento) / (1000L * 60 * 60 * 24 * 30)).toInt()

            """
        PERFIL DEL BEBÉ:
        - Nombre: ${baby.nombre}
        - Edad: $edadMeses meses
        - Peso: ${baby.peso} kg
        - Alergias: ${if (baby.alergias.isEmpty()) "Ninguna" else baby.alergias.joinToString()}
        """.trimIndent()
        } catch (e: Exception) {
            "Error al acceder a los datos locales."
        }
    }
    private fun currentTime(): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
}