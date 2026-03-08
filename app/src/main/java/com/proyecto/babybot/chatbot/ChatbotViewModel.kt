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
//import kotlin.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatbotViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        ChatbotState(
            messages = listOf(
                ChatMessage(
                    text = "Texto de ejemplo de cómo se vería el chatbot",
                    time = currentTime(),
                    isUser = false
                )
            )
        )
    )
    val state: StateFlow<ChatbotState> = _state

    fun onMessageChange(text: String) {
        _state.update { it.copy(currentMessage = text) }
    }

    fun sendMessage() {
        val message = state.value.currentMessage
        if (message.isBlank()) return

        val userMessage = ChatMessage(
            text = message,
            time = currentTime(),
            isUser = true
        )

        val botReply = ChatMessage(
            text = "Respuesta simulada de la IA 🤖",
            time = currentTime(),
            isUser = false
        )

        _state.update {
            it.copy(
                messages = it.messages + userMessage + botReply,
                currentMessage = ""
            )
        }
    }

    private fun currentTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }
}