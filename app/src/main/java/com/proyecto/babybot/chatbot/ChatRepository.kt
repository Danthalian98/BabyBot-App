package com.proyecto.babybot.chatbot

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Type

class ChatRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: "anonimo"

    // --- SECCIÓN DE HISTORIAL DE CHAT ---

    // Guarda el mensaje (tanto del usuario como de BabyBot)
    fun salvarMensajeEnHistorial(chatEntity: ChatEntity) {
        if (userId == "anonimo") return

        // Guardamos en: usuarios/{userId}/mensajes/{randomId}
        db.collection("usuarios").document(userId)
            .collection("mensajes")
            .add(chatEntity)
    }

    // Recupera todos los mensajes del chat ordenados por fecha
    fun obtenerHistorialChat(): Query {
        return db.collection("usuarios").document(userId)
            .collection("mensajes")
            .orderBy("fecha", Query.Direction.ASCENDING)
    }

    // --- SECCIÓN DE HISTORIAL DE FOROS ---

    // Recupera los posts que el usuario ha creado
    fun obtenerMisPublicaciones(): Query {
        return db.collection("foros")
            .whereEqualTo("autorId", userId)
            .orderBy("fecha", Query.Direction.DESCENDING)
    }

    // Recupera los comentarios que el usuario ha hecho en cualquier post
    // Nota: Requiere crear un índice de "Collection Group" en la consola de Firebase
    fun obtenerMisComentarios(): Query {
        return db.collectionGroup("comentarios")
            .whereEqualTo("autorId", userId)
            .orderBy("fecha", Query.Direction.DESCENDING)
    }

    // 1. Optimización de subida: Solo subir si el documento no existe
    fun uploadJsonToFirestore() {
        val gson = Gson()
        val archivos = listOf("basic_health.json", "feeding.json", "growing.json", "help.json")

        archivos.forEach { nombreArchivo ->
            try {
                val jsonString = context.assets.open(nombreArchivo).bufferedReader().use { it.readText() }
                val listType: Type = object : TypeToken<List<KnowledgeEntry>>() {}.type
                val entradas: List<KnowledgeEntry> = gson.fromJson(jsonString, listType)

                // Dentro de uploadJsonToFirestore, en el bucle de entradas:
                entradas.forEach { entrada ->
                    // Creamos una copia de la entrada con las palabras clave normalizadas
                    val entradaLimpia = entrada.copy(
                        palabras_clave = entrada.palabras_clave.map { it.normalize() }
                    )
                    // Usamos .set() para actualizar los registros actuales en Firestore
                    db.collection("conocimiento").document(entradaLimpia.id).set(entradaLimpia)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // 2. Búsqueda optimizada por relevancia
    suspend fun searchInKnowledge(userText: String): String {
        val cleanQuery = userText.normalize()

        // Extraemos palabras clave
        val keywords = cleanQuery.split(" ", "?", "¿", ",", ".")
            .filter { it.length > 3 } // Evitamos "el", "la", "de"
            .distinct()
            .take(10)

        if (keywords.isEmpty()) return "Sin contexto adicional."

        return try {
            val snapshot = db.collection("conocimiento")
                .whereArrayContainsAny("palabras_clave", keywords)
                .get()
                .await()

            val results = snapshot.toObjects(KnowledgeEntry::class.java)

            if (results.isEmpty()) {
                " "
            } else {
                results.joinToString("\n---\n") { entry ->
                    val validacion = entry.validacion_cientifica?.let { "\nVALIDACIÓN CIENTÍFICA: $it" } ?: ""
                    "TÍTULO: ${entry.titulo}\n" + "CONTENIDO: ${entry.contenido}" + validacion + "\nFUENTE_REAL: ${entry.fuente}"
                }
            }
        } catch (e: Exception) {
            "Error al buscar contexto: ${e.localizedMessage}"
        }
    }
    // Función auxiliar de normalización
    private fun String.normalize(): String = this.lowercase()
        .replace('á', 'a').replace('é', 'e')
        .replace('í', 'i').replace('ó', 'o')
        .replace('ú', 'u').replace('ñ', 'n')
        .trim()
}

// Convierte lo que viene de Firebase a lo que necesita tu UI
fun ChatEntity.toUiModel(): ChatMessage {
    val sfd = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    val horaFormateada = fecha?.toDate()?.let { sfd.format(it) } ?: "00:00"

    return ChatMessage(
        text = this.contenido,
        time = horaFormateada,
        isUser = this.autor == "user"
    )
}