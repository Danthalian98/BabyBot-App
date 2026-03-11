package com.proyecto.babybot

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Type

class ChatRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()

    fun uploadJsonToFirestore() {
        val gson = Gson()
        // Lista de los archivos en la carpeta assets
        val archivos = listOf("basic_health.json", "feeding.json", "growing.json", "help.json")

        archivos.forEach { nombreArchivo ->
            try {
                // Leer el archivo desde assets
                val jsonString = context.assets.open(nombreArchivo).bufferedReader().use { it.readText() }

                // Definir el tipo de lista para Gson
                val listType: Type = object : TypeToken<List<KnowledgeEntry>>() {}.type

                // Convertir JSON a lista de objetos
                val entradas: List<KnowledgeEntry> = gson.fromJson(jsonString, listType)

                // Subir cada entrada a Firestore
                entradas.forEach { entrada ->
                    db.collection("knowledge")
                        .document(entrada.id) // Usa el ID del JSON (ej: FEED_001)
                        .set(entrada)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun searchInKnowledge(answer: String): String {
        val query = answer.lowercase()
        return try {
            val snapshot = db.collection("knowledge") // Usa el nombre exacto de tu colección
                .get()
                .await()

            // Filtramos de forma sencilla por palabras clave o título
            val results = snapshot.documents.mapNotNull { it.toObject(KnowledgeEntry::class.java) }
                .filter { entry ->
                    entry.titulo.lowercase().contains(query) ||
                            entry.palabras_clave.any { it.contains(query) }
                }

            if (results.isEmpty()) "No hay datos específicos en la base de datos."
            else results.joinToString("\n\n") { "${it.titulo}: ${it.contenido}" }
        } catch (e: Exception) {
            "Error al buscar contexto: ${e.message}"
        }
    }
}