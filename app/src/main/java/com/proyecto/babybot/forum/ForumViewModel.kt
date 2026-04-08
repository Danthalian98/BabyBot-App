package com.proyecto.babybot.forum

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.proyecto.babybot.chatbot.ChatRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.proyecto.babybot.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val generativeModel = GenerativeModel(
        modelName = "models/gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val _state = MutableStateFlow(ForumState(selectedFilter = ""))
    val state: StateFlow<ForumState> = _state.asStateFlow()

    private var allPosts = listOf<PostUi>()

    init {
        fetchPostsFromFirestore()
    }

    private fun fetchPostsFromFirestore() {
        db.collection("foro")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FORO_VM", "Error al escuchar Firestore: ${e.message}")
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    PostUi(
                        id = doc.id,
                        userName = doc.getString("autor") ?: "Anónimo",
                        fecha = doc.getString("fecha") ?: "",
                        titulo = doc.getString("titulo") ?: "",
                        contenido = doc.getString("contenido") ?: "",
                        tags = (doc.get("tags") as? List<String>) ?: listOf(doc.getString("categoria") ?: ""),
                        // 🟢 CORRECCIÓN: Leemos likes como lista de IDs
                        likes = doc.get("likes") as? List<String> ?: emptyList(),
                        comentarios = doc.getLong("comentarios")?.toInt() ?: 0,
                        avatarColor = Color.Gray
                    )
                } ?: emptyList()

                allPosts = posts
                applyFilter(_state.value.selectedFilter)
            }
    }

    fun onFilterSelected(filter: String) {
        val newFilter = if (_state.value.selectedFilter == filter) "" else filter
        _state.update { it.copy(selectedFilter = newFilter) }
        applyFilter(newFilter)
    }

    private fun applyFilter(filter: String) {
        val filtered = if (filter.isEmpty()) {
            allPosts
        } else {
            allPosts.filter { it.tags.contains(filter) }
        }
        _state.update { it.copy(posts = filtered) }
    }

    fun publicarEnForo(titulo: String, contenido: String, categoria: String, nombreUsuario: String) {
        viewModelScope.launch {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val fechaActual = sdf.format(Date())

                val nuevoPost = hashMapOf(
                    "autor" to nombreUsuario,
                    "titulo" to titulo,
                    "contenido" to contenido,
                    "categoria" to categoria,
                    "fecha" to fechaActual,
                    "tags" to listOf(categoria),
                    "likes" to emptyList<String>(), // 🟢 Iniciamos como Lista vacía
                    "comentarios" to 1 // El de BabyBot
                )

                // 1. Guardar Post
                val docRef = db.collection("foro").add(nuevoPost).await()

                // 2. IA Responderá de forma asíncrona
                generarRespuestaBabyBot(docRef.id, titulo, contenido, fechaActual)

            } catch (e: Exception) {
                Log.e("FORO_ERROR", "Error: ${e.message}")
            }
        }
    }

    private fun generarRespuestaBabyBot(postId: String, titulo: String, contenido: String, fecha: String) {
        viewModelScope.launch {
            try {
                // Buscamos contexto en tus JSON locales
                val contexto = repository.searchInKnowledge("$titulo $contenido")

                val promptIA = """
                    Actúa como BabyBot, asistente pediátrico experto.
                    Usa este contexto técnico: $contexto
                    Pregunta: $titulo - $contenido
                    Respuesta:
                """.trimIndent()

                val result = generativeModel.generateContent(promptIA)
                val respuestaTexto = result.text ?: "Consultando con especialistas..."

                val comentarioIA = hashMapOf(
                    "autor" to "BabyBot",
                    "contenido" to respuestaTexto,
                    "fecha" to fecha,
                    "esOficial" to true // 🟢 Coincide con tu CommentUi
                )

                db.collection("foro").document(postId)
                    .collection("comentarios")
                    .add(comentarioIA)
                    .await()

            } catch (e: Exception) {
                Log.e("BABYBOT_IA", "Falla IA: ${e.message}")
            }
        }
    }
}