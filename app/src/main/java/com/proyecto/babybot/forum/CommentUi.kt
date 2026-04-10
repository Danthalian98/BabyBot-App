package com.proyecto.babybot.forum

data class CommentUi(
    val id: String = "",
    val autor: String = "",
    val contenido: String = "",
    val fecha: String = "",
    val esOficial: Boolean = false
)