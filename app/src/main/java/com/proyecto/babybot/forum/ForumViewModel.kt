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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val repository: ChatRepository // Inyectamos tu buscador de contexto
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Configuración de Gemini (asegúrate de usar la versión correcta de tu BuildConfig)
    private val generativeModel = GenerativeModel(
        modelName = "models/gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val _state = MutableStateFlow(ForumState(selectedFilter = ""))
    val state: StateFlow<ForumState> = _state

    private var allPosts = listOf<PostUi>()

    init {
        fetchPostsFromFirestore()
    }

    // 1. Escuchar Firestore en tiempo real
    private fun fetchPostsFromFirestore() {
        db.collection("foro")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    // Mapeo manual para asegurar compatibilidad con PostUi
                    PostUi(
                        id = doc.id, // El ID ahora es String
                        userName = doc.getString("autor") ?: "Anónimo",
                        fecha = doc.getString("fecha") ?: "",
                        titulo = doc.getString("titulo") ?: "",
                        contenido = doc.getString("contenido") ?: "",
                        tags = (doc.get("tags") as? List<String>) ?: listOf(doc.getString("categoria") ?: ""),
                        likes = doc.getLong("likes")?.toInt() ?: 0,
                        comentarios = doc.getLong("comentarios")?.toInt() ?: 0,
                        avatarColor = Color.Gray // O lógica para colores aleatorios
                    )
                } ?: emptyList()

                allPosts = posts
                applyFilter(_state.value.selectedFilter)
            }
    }

    // 2. Lógica de filtrado
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

    // 3. FUNCIÓN PARA PUBLICAR + RESPUESTA DE IA
    fun publicarEnForo(titulo: String, contenido: String, categoria: String, nombreUsuario: String) {
        viewModelScope.launch {
            try {
                val fechaActual = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                val nuevoPost = hashMapOf(
                    "autor" to nombreUsuario,
                    "titulo" to titulo,
                    "contenido" to contenido,
                    "categoria" to categoria,
                    "fecha" to fechaActual,
                    "tags" to listOf(categoria),
                    "likes" to 0,
                    "comentarios" to 1 // El primer comentario será de BabyBot
                )

                // A. Guardamos el post
                val docRef = db.collection("foro").add(nuevoPost).await()

                // B. Buscamos en el conocimiento local (Tus JSONs)
                val contexto = repository.searchInKnowledge("$titulo $contenido")

                // C. Generamos la respuesta de BabyBot
                val promptIA = """
                    Actúa como BabyBot, asistente pediátrico oficial del foro.
                    Usa el <contexto> para responder a la duda del padre.
                    Si el contexto no tiene la respuesta, sé honesto y sugiere ver a un médico.
                    
                    <contexto>
                    $contexto
                    </contexto>
                    
                    Pregunta del padre: $titulo - $contenido
                """.trimIndent()

                val aiResponse = generativeModel.generateContent(promptIA)

                // D. Guardamos el comentario de la IA en una subcolección
                val comentarioIA = hashMapOf(
                    "autor" to "BabyBot",
                    "contenido" to (aiResponse.text ?: "Consultando con especialistas..."),
                    "fecha" to fechaActual,
                    "isOficial" to true
                )

                db.collection("foro").document(docRef.id)
                    .collection("comentarios")
                    .add(comentarioIA)
                    .await()

            } catch (e: Exception) {
                Log.e("FORO", "Error al crear post: ${e.message}")
            }
        }
    }
}