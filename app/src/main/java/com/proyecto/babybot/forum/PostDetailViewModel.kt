package com.proyecto.babybot.forum

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(PostDetailState())
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    // Flujo para los comentarios en tiempo real
    private val _comments = MutableStateFlow<List<CommentUi>>(emptyList())
    val comments: StateFlow<List<CommentUi>> = _comments.asStateFlow()

    init {
        // IMPORTANTE: El argumento de navegación ahora debe ser String
        val postId = savedStateHandle.get<String>("postId") ?: ""
        if (postId.isNotEmpty()) {
            loadPostDetails(postId)
            listenToComments(postId)
        }
    }

    fun loadPostDetails(postId: String) {
        _state.update { it.copy(isLoading = true) }

        db.collection("foro").document(postId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val post = PostUi(
                        id = doc.id,
                        userName = doc.getString("autor") ?: "Anónimo",
                        fecha = doc.getString("fecha") ?: "",
                        titulo = doc.getString("titulo") ?: "",
                        contenido = doc.getString("contenido") ?: "",
                        tags = doc.get("tags") as? List<String> ?: emptyList(),
                        likes = doc.getLong("likes")?.toInt() ?: 0,
                        comentarios = doc.getLong("comentarios")?.toInt() ?: 0
                    )
                    _state.update { it.copy(post = post, isLoading = false) }
                }
            }
            .addOnFailureListener { _state.update { it.copy(isLoading = false) } }
    }

    fun listenToComments(postId: String) {
        db.collection("foro").document(postId)
            .collection("comentarios")
            .orderBy("fecha", Query.Direction.ASCENDING) // BabyBot suele ser el primero por fecha
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val commentList = snapshot?.documents?.mapNotNull { doc ->
                    CommentUi(
                        autor = doc.getString("autor") ?: "Anónimo",
                        contenido = doc.getString("contenido") ?: "",
                        fecha = doc.getString("fecha") ?: "",
                        esOficial = doc.getBoolean("isOficial") ?: false
                    )
                } ?: emptyList()

                _comments.value = commentList
            }
    }

    fun enviarComentario(postId: String, texto: String) {
        viewModelScope.launch {
            try {
                val fechaActual = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())

                val nuevoComentario = hashMapOf(
                    "autor" to "Usuario", // Aquí podrías usar el nombre del usuario logueado
                    "contenido" to texto,
                    "fecha" to fechaActual,
                    "esOficial" to false
                )

                db.collection("foro").document(postId)
                    .collection("comentarios")
                    .add(nuevoComentario)

            } catch (e: Exception) {
                android.util.Log.e("FORO_DETALLE", "Error al enviar comentario: ${e.message}")
            }
        }
    }
}