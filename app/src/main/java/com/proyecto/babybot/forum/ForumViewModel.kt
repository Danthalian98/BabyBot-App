package com.proyecto.babybot.forum

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor() : ViewModel() {

    // Lista maestra que siempre contiene TODOS los posts.
    private val allPosts = fakePosts()

    // Inicializamos el State. Arrancamos sin ningún filtro seleccionado (mostrando todos).
    private val _state = MutableStateFlow(
        ForumState(
            posts = allPosts,
            selectedFilter = "" // Un string vacío significa "ningún filtro activo"
        )
    )
    val state: StateFlow<ForumState> = _state

    fun onFilterSelected(filter: String) {
        _state.update { currentState ->
            // Si el usuario toca el filtro que YA estaba seleccionado, lo deseleccionamos.
            if (currentState.selectedFilter == filter) {
                currentState.copy(
                    selectedFilter = "", // Quitamos la selección
                    posts = allPosts     // Mostramos todos los posts de nuevo
                )
            } else {
                // Si toca un filtro nuevo, aplicamos el filtro a la lista maestra.
                val filteredPosts = allPosts.filter { post ->
                    post.tags.contains(filter)
                }

                currentState.copy(
                    selectedFilter = filter, // Actualizamos el filtro activo
                    posts = filteredPosts    // Mostramos solo los filtrados
                )
            }
        }
    }

    private fun fakePosts(): List<PostUi> {
        return listOf(
            PostUi(
                id = 1,
                userName = "Sarah M.",
                fecha = "Hace 2h",
                titulo = "¡Mi bebé durmió toda la noche por primera vez!",
                contenido = "Después de 4 meses de noches sin dormir...",
                // Etiquetas exactas a los filtros de la UI
                tags = listOf("Nuevos", "Sueño"),
                likes = 234,
                comentarios = 45,
                avatarColor = Color.Green
            ),
            PostUi(
                id = 2,
                userName = "Michael R.",
                fecha = "Hace 5h",
                titulo = "¿Mejores papillas para empezar?",
                contenido = "Mi pequeño ya tiene 6 meses y quiero empezar con sólidos...",
                // Etiquetas exactas a los filtros de la UI
                tags = listOf("Populares", "Alimentación"),
                likes = 189,
                comentarios = 67,
                avatarColor = Color.Red
            ),
            PostUi(
                id = 3,
                userName = "Lucía G.",
                fecha = "Hace 10m",
                titulo = "¿Cuántas siestas son normales a los 8 meses?",
                contenido = "Mi bebé últimamente se resiste a la segunda siesta del día...",
                // Etiquetas exactas a los filtros de la UI
                tags = listOf("Nuevos", "Sueño"),
                likes = 15,
                comentarios = 4,
                avatarColor = Color.Blue
            ),
            PostUi(
                id = 4,
                userName = "Carlos T.",
                fecha = "Ayer",
                titulo = "Alergia a la proteína de la leche",
                contenido = "Nos acaban de diagnosticar y estoy un poco perdido...",
                // Etiquetas exactas a los filtros de la UI
                tags = listOf("Alimentación"),
                likes = 56,
                comentarios = 22,
                avatarColor = Color.Magenta
            )
        )
    }
}