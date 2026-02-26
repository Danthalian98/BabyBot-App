package com.proyecto.babybot.forum

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PostDetailState())
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    init {
        // En tu gráfica de navegación, deberás pasar un argumento llamado "postId"
        val postId = savedStateHandle.get<Int>("postId") ?: -1
        loadPostDetails(postId)
    }

    private fun loadPostDetails(id: Int) {
        // 2. CORRECCIÓN: Llamamos a fakePosts() con el nombre correcto
        val fakePostsList = fakePosts()
        val postFound = fakePostsList.find { it.id == id }

        _state.value = PostDetailState(
            post = postFound,
            isLoading = false
        )
    }

    private fun fakePosts(): List<PostUi> {
        return listOf(
            PostUi(
                id = 1,
                userName = "Sarah M.",
                fecha = "Hace 2h",
                titulo = "¡Mi bebé durmió toda la noche por primera vez!",
                contenido = "Después de 4 meses de noches sin dormir...",
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
                tags = listOf("Alimentación"),
                likes = 56,
                comentarios = 22,
                avatarColor = Color.Magenta
            )
        )
    }
}