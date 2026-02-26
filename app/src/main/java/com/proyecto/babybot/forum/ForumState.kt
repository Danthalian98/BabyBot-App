package com.proyecto.babybot.forum

import androidx.compose.ui.graphics.Color

data class PostUi(
    val id: Int,
    val userName: String,
    val fecha: String,
    val titulo: String,
    val contenido: String,
    val tags: List<String>,
    val likes: Int,
    val comentarios: Int,
    val avatarColor: Color
)

data class ForumState(
    val posts: List<PostUi> = emptyList(),
    val selectedFilter: String = "Nuevos"
)