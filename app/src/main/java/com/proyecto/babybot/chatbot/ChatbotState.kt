package com.proyecto.babybot.chatbot

data class ChatbotState(
    val messages: List<ChatMessage> = emptyList(),
    val suggestions: List<String> = listOf(
        "¿Mi bebé puede tomar Coca-Cola?",
        "¿Cómo cargar a un bebé?",
        "Ciclo de sueño"
    ),
    val currentMessage: String = "",
    val isLoading: Boolean = false, // CAMBIO: Para bloquear el botón y mostrar progreso
    val error: String? = null       // CAMBIO: Por si falla la cuota de Gemini (Error 429)
)