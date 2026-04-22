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
import com.proyecto.babybot.ModeracionUtil
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
    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
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
        // val newFilter = if (_state.value.selectedFilter == filter) "" else filter
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

    fun publicarEnForo(titulo: String, contenido: String, categoria: String) {
        val firebaseUser = auth.currentUser

        // Si no hay usuario, no permitimos publicar
        if (firebaseUser == null) {
            Log.e("FORO_AUTH", "Intento de publicación sin sesión activa")
            return
        }

        viewModelScope.launch {
            try {
                // Aplicamos el filtro de lenguaje antes de subir nada
                if (!ModeracionUtil.esContenidoSeguro(titulo) || !ModeracionUtil.esContenidoSeguro(contenido)) {
                    Log.w("MODERACION", "Contenido bloqueado por lenguaje inapropiado")
                    // Aquí podrías actualizar un estado de la UI para mostrar un aviso
                    return@launch
                }

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val fechaActual = sdf.format(Date())

                val nuevoPost = hashMapOf(
                    "autor" to (firebaseUser.displayName ?: "Usuario de BabyBot"),
                    "autorId" to firebaseUser.uid, // Guardamos el ID para el futuro historial
                    "titulo" to titulo,
                    "contenido" to contenido,
                    "categoria" to categoria,
                    "fecha" to fechaActual,
                    "tags" to listOf(categoria),
                    "likes" to emptyList<String>(),
                    "dislikes" to emptyList<String>(),
                    "comentarios" to 1
                )

                val docRef = db.collection("foro").add(nuevoPost).await()
                generarRespuestaBabyBot(docRef.id, titulo, contenido, fechaActual)

            } catch (e: Exception) {
                Log.e("FORO_ERROR", "Error al publicar: ${e.message}")
            }
        }
    }

    private fun generarRespuestaBabyBot(postId: String, titulo: String, contenido: String, fecha: String) {
        applicationScope.launch {
            var intentoExitoso = false
            var intentosTotales = 0
            val maxRetries = 3

            // Bucle de reintentos para manejar el Error 503 / Rate Limit
            while (intentosTotales < maxRetries && !intentoExitoso) {
                try {
                    Log.d("BABYBOT_IA", "Intento ${intentosTotales + 1} para post: $postId")

                    val contexto = repository.searchInKnowledge("$titulo $contenido")
                    val promptIA = """
                    Actúa como BabyBot, un asistente pediátrico experto. 
                    Contexto: $contexto
                    Usuario pregunta: $titulo - $contenido
                    Respuesta breve y basada estrictamente en el contexto:
                """.trimIndent()

                    val result = generativeModel.generateContent(promptIA)
                    val respuestaTexto = result.text ?: "Consulte a su médico para obtener orientación personalizada."

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

                    intentoExitoso = true
                    Log.d("BABYBOT_IA", "¡ÉXITO! BabyBot respondió correctamente.")

                } catch (e: Exception) {
                    intentosTotales++
                    val errorMsg = e.message ?: "Error desconocido"
                    Log.e("BABYBOT_IA", "Fallo en intento $intentosTotales: $errorMsg")

                    if (intentosTotales < maxRetries) {
                        // Si es un error de cuota o servidor (como el 503), esperamos antes de reintentar
                        // 2 seg, luego 4 seg...
                        val delayTime = intentosTotales * 2000L
                        kotlinx.coroutines.delay(delayTime)
                    } else {
                        // Si agotamos los intentos, dejamos un mensaje de error en el log o un comentario genérico
                        Log.e("BABYBOT_IA", "Se agotaron los reintentos para el post: $postId")
                    }
                }
            }
        }
    }

    // Dentro de ForumViewModel

    fun fetchMiActividad() {
        val currentUserId = auth.currentUser?.uid ?: return

        _state.update { it.copy(isLoading = true) }

        // 1. Obtener mis Publicaciones
        viewModelScope.launch {
            db.collection("foro")
                .whereEqualTo("autorId", currentUserId)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener

                    val misPosts = snapshot?.documents?.mapNotNull { doc ->
                        // Reutilizamos tu lógica de mapeo de PostUi
                        mapDocToPostUi(doc)
                    } ?: emptyList()

                    // Aquí podrías actualizar un estado específico para "Mis Posts"
                }
        }

        // 2. Obtener mis Comentarios (Historial de qué ha respondido el usuario)
        viewModelScope.launch {
            db.collectionGroup("comentarios")
                .whereEqualTo("autorId", currentUserId)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("FORO_HISTORIAL", "Error en collectionGroup: ${e.message}")
                        return@addSnapshotListener
                    }

                    val comentarios = snapshot?.documents?.mapNotNull { doc ->
                        // Mapeo a un objeto simple de comentario
                        CommentUi(
                            contenido = doc.getString("contenido") ?: "",
                            fecha = doc.getString("fecha") ?: "",
                            id = doc.reference.parent.parent?.id ?: "" // ID del post original
                        )
                    } ?: emptyList()

                    _state.update { it.copy(misComentarios = comentarios, isLoading = false) }
                }
        }
    }

    // Función auxiliar para no repetir código de mapeo
    private fun mapDocToPostUi(doc: com.google.firebase.firestore.DocumentSnapshot): PostUi {
        return PostUi(
            id = doc.id,
            userName = doc.getString("autor") ?: "Anónimo",
            fecha = doc.getString("fecha") ?: "",
            titulo = doc.getString("titulo") ?: "",
            contenido = doc.getString("contenido") ?: "",
            tags = (doc.get("tags") as? List<String>) ?: listOf(doc.getString("categoria") ?: ""),
            likes = doc.get("likes") as? List<String> ?: emptyList(),
            comentarios = doc.getLong("comentarios")?.toInt() ?: 0,
            avatarColor = Color.Gray
        )
    }
}