package com.proyecto.babybot.forum

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: "usuario_anonimo"

    private val _state = MutableStateFlow(PostDetailState())
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    private val _comments = MutableStateFlow<List<CommentUi>>(emptyList())
    val comments: StateFlow<List<CommentUi>> = _comments.asStateFlow()

    init {
        val postId = savedStateHandle.get<String>("postId") ?: ""
        if (postId.isNotEmpty()) {
            loadPostDetails(postId)
            listenToComments(postId) // Solo un lugar para los comentarios
        }
    }

    fun loadPostDetails(postId: String) {
        _state.update { it.copy(isLoading = true) }

        db.collection("foro").document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    val post = PostUi(
                        id = snapshot.id,
                        userName = snapshot.getString("autor") ?: "Anónimo",
                        titulo = snapshot.getString("titulo") ?: "",
                        contenido = snapshot.getString("contenido") ?: "",
                        fecha = snapshot.getString("fecha") ?: "",
                        likes = snapshot.get("likes") as? List<String> ?: emptyList(),
                        dislikes = snapshot.get("dislikes") as? List<String> ?: emptyList(),
                        comentarios = snapshot.getLong("comentarios")?.toInt() ?: 0
                    )
                    _state.update { it.copy(post = post, isLoading = false) }
                }
            }
    }

    fun listenToComments(postId: String) {
        db.collection("foro").document(postId)
            .collection("comentarios")
            .orderBy("fecha", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FIRESTORE", "Error escuchando comentarios", e)
                    return@addSnapshotListener
                }

                val commentList = snapshot?.documents?.mapNotNull { doc ->
                    CommentUi(
                        id = doc.id,
                        autor = doc.getString("autor") ?: "Anónimo",
                        contenido = doc.getString("contenido") ?: "",
                        fecha = doc.getString("fecha") ?: "",
                        // 🟢 CORRECCIÓN: Usa el mismo nombre que en enviarComentario
                        esOficial = doc.getBoolean("esOficial") ?: false
                    )
                } ?: emptyList()

                _comments.value = commentList
            }
    }

    fun enviarComentario(postId: String, texto: String) {
        viewModelScope.launch {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val nuevoComentario = hashMapOf(
                "autor" to "Usuario",
                "contenido" to texto,
                "fecha" to fechaActual,
                "esOficial" to false
            )

            val postRef = db.collection("foro").document(postId)

            // 1. Guardar comentario
            postRef.collection("comentarios").add(nuevoComentario)
                .addOnSuccessListener {
                    // 2. Incrementar contador solo si el add fue exitoso
                    postRef.update("comentarios", FieldValue.increment(1))
                }
        }
    }

    fun toggleLike(postId: String, userId: String) {
        val postRef = db.collection("foro").document(postId)
        val post = _state.value.post ?: return
        val hasLike = post.likes.contains(userId)
        val hasDislike = post.dislikes.contains(userId)

        if (hasLike) {
            postRef.update("likes", FieldValue.arrayRemove(userId))
            Log.d("VOTOS", "Quitando Like")
        } else {
            postRef.update("likes", FieldValue.arrayUnion(userId))
            if (hasDislike) postRef.update("dislikes", FieldValue.arrayRemove(userId))
            Log.d("VOTOS", "Poniendo Like y quitando Dislike")
        }
    }

    fun toggleDislike(postId: String, userId: String) {
        val postRef = db.collection("foro").document(postId)
        val post = _state.value.post ?: return
        val hasLike = post.likes.contains(userId)
        val hasDislike = post.dislikes.contains(userId)

        if (hasDislike) {
            // 🚨 SI ESTO NO FUNCIONA, ES QUE EL NOMBRE EN FIREBASE ES DISTINTO
            postRef.update("dislikes", FieldValue.arrayRemove(userId))
            Log.d("VOTOS", "Quitando Dislike")
        } else {
            postRef.update("dislikes", FieldValue.arrayUnion(userId))
            if (hasLike) postRef.update("likes", FieldValue.arrayRemove(userId))
            Log.d("VOTOS", "Poniendo Dislike y quitando Like")
        }
    }

    fun reportarPost(postId: String, motivo: String, detalle: String = "") {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo"
            val reporte = hashMapOf(
                "postId" to postId,
                "reportadoPor" to userId,
                "motivo" to motivo,
                "detalle" to detalle,
                "fecha" to java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                "estado" to "pendiente" // Para que tú los revises luego
            )

            db.collection("reportes").add(reporte)
                .addOnSuccessListener {
                    Log.d("REPORTES", "Reporte enviado con éxito")
                }
                .addOnFailureListener { e ->
                    Log.e("REPORTES", "Error al reportar: ${e.message}")
                }
        }
    }

    fun reportarComentario(postId: String, commentId: String, motivo: String, detalle: String = "") {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: "anonimo"
            val reporte = hashMapOf(
                "tipo" to "comentario",
                "postId" to postId,
                "commentId" to commentId,
                "reportadoPor" to userId,
                "motivo" to motivo,
                "detalle" to detalle,
                "fecha" to SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                "estado" to "pendiente"
            )

            db.collection("reportes_comentarios").add(reporte)
                .addOnSuccessListener {
                    Log.d("REPORTES", "Comentario reportado con éxito")
                }
        }
    }
}