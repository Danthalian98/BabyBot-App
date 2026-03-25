package com.proyecto.babybot.chatbot

import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

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

    /*init {
        listModels()
    }*/

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
                val context = repository.searchInKnowledge(userText)

                val prompt = """
                Eres BabyBot, un asistente experto en pediatría, amable, claro y basado en evidencia científica.

                CONTEXTO:
                $context
    
                REGLAS:
                - Usa SOLO la información del contexto si existe.
                - Si no hay información suficiente, responde con consejo general y recomienda consultar pediatra.
                - Si es un mito, explica por qué usando evidencia.
                - Siempre termina con: "Fuente: ..."

                Pregunta:
                $userText
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
            /*try {
                val response = generativeModel.generateContent("Di solo: funcionando")
                Log.d("GEMINI_TEST", response.text ?: "sin respuesta")
            }
            catch (e: Exception) {
                android.util.Log.e("GEMINI_ERROR", "Error completo", e)
            }*/
        }
    }
    /*fun listModels() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val url = URL("https://generativelanguage.googleapis.com/v1beta/models?key=${BuildConfig.GEMINI_API_KEY}")
                    url.readText()
                }

                android.util.Log.d("MODELS_LIST", response)

            } catch (e: Exception) {
                android.util.Log.e("MODELS_ERROR", "Error", e)
            }
        }
    }*/

    private fun currentTime(): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
}