package com.proyecto.babybot.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.ChatRepository
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.Firebase
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

    // Inicializamos Gemini 1.5 Flash (requiere dependencia Vertex AI en Gradle)
    private val generativeModel = Firebase.vertexAI.generativeModel(modelName = "gemini-1.5-flash")

    private val _state = MutableStateFlow(ChatbotState())
    val state: StateFlow<ChatbotState> = _state

    fun onMessageChange(text: String) {
        _state.update { it.copy(currentMessage = text) }
    }

    fun sendMessage() {
        val userText = state.value.currentMessage
        if (userText.isBlank()) return

        // 1. Añadimos el mensaje del usuario a la lista inmediatamente
        val userMessage = ChatMessage(text = userText, time = currentTime(), isUser = true)
        _state.update { it.copy(messages = it.messages + userMessage, currentMessage = "") }

        // 2. Iniciamos la búsqueda en Firestore y la consulta a Gemini
        viewModelScope.launch {
            try {
                // Buscamos información relevante en tus 120 entradas
                val context = repository.searchInKnowledge(userText)

                val prompt = """
                    Eres BabyBot, un asistente experto en pediatría y cuidado infantil. 
                    Responde de forma tierna y profesional.
                    Usa esta información para tu respuesta: $context
                    Pregunta: $userText
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)

                val botReply = ChatMessage(
                    text = response.text ?: "Lo siento, no pude procesar eso. Le recomiendo que visite a un pediatra.",
                    time = currentTime(),
                    isUser = false
                )

                _state.update { it.copy(messages = it.messages + botReply) }

            } catch (e: Exception) {
                val errorReply = ChatMessage(
                    text = "Hubo un error: ${e.localizedMessage}",
                    time = currentTime(),
                    isUser = false
                )
                _state.update { it.copy(messages = it.messages + errorReply) }
            }
        }
    }

    private fun currentTime(): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
}