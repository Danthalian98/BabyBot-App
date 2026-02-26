package com.proyecto.babybot.chatbot

data class ChatMessage(
    val text: String,
    val time: String,
    val isUser: Boolean
)