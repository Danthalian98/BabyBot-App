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
import kotlinx.coroutines.Dispatchers
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
    // Scope que no se cancela al cerrar el ViewModel
    private val applicationScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO)

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
        // Si quieres que al hacer clic en el mismo filtro se quite:
        // val newFilter = if (_state.value.selectedFilter == filter) "" else filter

        // Si quieres que siempre haya uno seleccionado (mejor para UX):
        _state.update { it.copy(selectedFilter = filter) }
        applyFilter(filter)
    }

    private fun applyFilter(filter: String) {
        var filtered = allPosts

        // 1. Filtrar por etiquetas (Salud, Alimentación, etc.)
        if (filter.isNotEmpty() && filter != "Nuevos" && filter != "Populares") {
            filtered = allPosts.filter { it.tags.contains(filter) }
        }

        // 2. Aplicar ordenamiento sobre lo que quedó filtrado
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        filtered = when (filter) {
            "Populares" -> filtered.sortedByDescending { it.likes.size }
            "Nuevos" -> filtered.sortedByDescending {
                try { sdf.parse(it.fecha) } catch (e: Exception) { Date(0) }
            }
            else -> filtered // Mantiene el orden original de la consulta a Firestore
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
                    "likes" to emptyList<String>(),
                    "dislikes" to emptyList<String>(), // 👈 Agregamos dislikes para que no de error luego
                    "comentarios" to 1
                )

                // 1. Guardar Post y esperar el ID
                val docRef = db.collection("foro").add(nuevoPost).await()

                // 2. Llamar a la IA (ya estamos en un Scope, no hace falta otro launch interno necesariamente,
                // pero lo mantenemos si quieres que la UI se libere de inmediato)
                generarRespuestaBabyBot(docRef.id, titulo, contenido, fechaActual)

            } catch (e: Exception) {
                Log.e("FORO_ERROR", "Error al publicar: ${e.message}")
            }
        }
    }

    private fun generarRespuestaBabyBot(postId: String, titulo: String, contenido: String, fecha: String) {
        // 💡 CAMBIO CLAVE: Usamos applicationScope en lugar de viewModelScope
        applicationScope.launch {
            try {
                Log.d("BABYBOT_IA", "Iniciando generación para post: $postId")

                val contexto = repository.searchInKnowledge("$titulo $contenido")
                val promptIA = """
                Actúa como BabyBot, un asistente pediátrico experto. 
                Contexto: $contexto
                Usuario pregunta: $titulo - $contenido
                Respuesta breve:
            """.trimIndent()

                val result = generativeModel.generateContent(promptIA)
                val respuestaTexto = result.text ?: "Consulte a su médico."

                val comentarioIA = hashMapOf(
                    "autor" to "BabyBot",
                    "contenido" to respuestaTexto,
                    "fecha" to fecha,
                    "esOficial" to true,
                    "autorId" to "babybot_oficial"
                )

                db.collection("foro").document(postId)
                    .collection("comentarios")
                    .add(comentarioIA)
                    .await()

                Log.d("BABYBOT_IA", "¡ÉXITO! BabyBot respondió correctamente.")

            } catch (e: Exception) {
                Log.e("BABYBOT_IA", "ERROR FATAL: ${e.message}")
            }
        }
    }
}