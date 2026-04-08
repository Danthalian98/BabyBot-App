package com.proyecto.babybot.forum

import androidx.compose.ui.graphics.Color

data class PostUi(
    val id: String = "",
    val userName: String = "",
    val fecha: String = "",
    val titulo: String = "",
    val contenido: String = "",
    val tags: List<String> = emptyList(),
    val likes: List<String> = emptyList(),
    val dislikes: List<String> = emptyList(),
    val comentarios: Int = 0,
    val avatarColor: Color = Color.Gray
)

data class ForumState(
    val posts: List<PostUi> = emptyList(),
    val selectedFilter: String = "Nuevos"
)