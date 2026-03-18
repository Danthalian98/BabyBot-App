package com.proyecto.babybot

data class KnowledgeEntry(
    val id: String = "",
    val categoria: String = "",
    val subtema: String = "",
    val titulo: String = "",
    val palabras_clave: List<String> = emptyList(),
    val contenido: String = "",
    val fuente: String = "",
    val rango_edad: String = "",
    val validacion_cientifica: String? = null // Se usa ? porque es opcional (solo para mitos)
)