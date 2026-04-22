package com.proyecto.babybot.chatbot

data class KnowledgeEntry(
    val id: String = "",
    val categoria: String = "",
    val titulo: String = "",
    val subtema: String = "",
    val palabras_clave: List<String> = emptyList(),
    val contenido: String = "",
    val fuente: String = "",
    val rango_edad: String? = null,
    val validacion_cientifica: String? = null
)