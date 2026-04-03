package com.proyecto.babybot.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.proyecto.babybot.ChatRepository
import com.proyecto.babybot.BuildConfig
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
    private val repository: ChatRepository
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
        // OPTIMIZACIÓN: Si está vacío O ya está cargando una respuesta, bloqueamos la ejecución
        if (userText.isBlank() || state.value.isLoading) return
        // 1. Preparamos la UI: Añadimos mensaje, limpiamos input y ACTIVAMOS carga
        val userMessage = ChatMessage(text = userText, time = currentTime(), isUser = true)
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
                // 1. Obtenemos el contexto
                val contextResult = repository.searchInKnowledge(userText)
                // 2. Construcción del Prompt
                // Añadimos una validación: si el repositorio devolvió el mensaje de "No hay registros",
                // pasamos un string vacío para que el bot no se confunda.
                val finalContext = if (contextResult.contains("No hay registros")) "" else contextResult
                val prompt = """
                Eres BabyBot, un asistente experto en pediatría y cuidado infantil.
        
                INSTRUCCIONES CRÍTICAS:
                1. Tu respuesta debe basarse PRIORITARIAMENTE en la información dentro de <contexto>.
                2. Si el <contexto> contiene información, responde de forma amable pero técnica.
                3. Si el <contexto> menciona un "Mito", explica la "VALIDACIÓN CIENTÍFICA" incluida.
                4. Al final de cada respuesta basada en el contexto, añade siempre: "Fuente: [Nombre de la fuente]".
                5. SI EL <CONTEXTO> ESTÁ VACÍO O ES INSUFICIENTE: 
                    - Responde amablemente que no tienes esa información específica en tu base de datos local.
                    - Sugiere SIEMPRE consultar con un pediatra.
                    - No inventes fuentes médicas si no están en el contexto.

                <contexto>
                $finalContext
                </contexto>

                Pregunta del usuario: $userText
                """.trimIndent()

                // 3. Consulta a Gemini
                val response = generativeModel.generateContent(prompt)
                val botReply = ChatMessage(
                    text = response.text ?: "Lo siento, no pude procesar la respuesta. Por favor, intenta de nuevo o consulta a tu pediatra.",
                    time = currentTime(),
                    isUser = false
                )

                _state.update { it.copy(messages = it.messages + botReply) }
            } catch (e: Exception) {
                // Manejo de errores de saturación (Quota) o conexión
                val errorMsg = if (e.localizedMessage?.contains("429") == true) {
                    "Estamos recibiendo muchas preguntas. Por favor, intenta de nuevo en un minuto."
                } else {
                    "Error de conexión. Revisa tu internet."
                }

                val errorReply = ChatMessage(
                    text = errorMsg,
                    time = currentTime(),
                    isUser = false
                )
                _state.update { it.copy(messages = it.messages + errorReply, error = e.localizedMessage) }
            } finally {
                // 4. DESBLOQUEO: Pase lo que pase (éxito o error), quitamos el estado de carga
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    private fun currentTime(): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
}