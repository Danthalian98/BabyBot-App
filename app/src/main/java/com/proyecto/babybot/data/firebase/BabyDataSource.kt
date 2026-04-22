package com.proyecto.babybot.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BabyDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getBabyByUserId(idUsuario: String): Baby? {
        return try {
            val result = firestore.collection("bebes")
                .whereEqualTo("idUsuario", idUsuario)
                .whereEqualTo("activo", true)
                .limit(1)
                .get()
                .await()

            result.documents.firstOrNull()?.toObject(Baby::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveBaby(baby: Baby): Boolean {
        return try {
            val babyToSave = baby.copy(
                fechaCreacion = Timestamp.now(),
                activo = true
            )

            firestore.collection("bebes")
                .document(baby.idBebe)
                .set(babyToSave)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }
}

data class Baby(
    val idBebe: String = "",
    val idUsuario: String = "",
    val nombre: String = "",
    val genero: String = "",
    val fechaNacimiento: Timestamp? = null,
    val peso: Double = 0.0,
    val talla: Double = 0.0,
    val tipoSangre: String = "",
    val pediatra: String = "",
    val notas: String = "",
    val alergias: List<String> = emptyList(),
    val fechaCreacion: Timestamp? = null,
    val activo: Boolean = true
)

data class MealEntry(
    val hora: Timestamp? = null,
    val cantidadMl: Int = 0,
    val tipo: String = ""
)

data class DiaperEntry(
    val hora: Timestamp? = null,
    val tipo: String = ""
)

data class SleepEntry(
    val inicio: Timestamp? = null,
    val fin: Timestamp? = null
)

data class MedicineEntry(
    val hora: Timestamp? = null,
    val nombre: String = "",
    val dosis: String = ""
)

data class DailySummary(
    val totalComidas: Int = 0,
    val totalPanales: Int = 0,
    val horasSueno: Double = 0.0
)

data class DailyLog(
    val idRegistro: String = "",
    val idBebe: String = "",
    val idUsuario: String = "",
    val fecha: Timestamp? = null,
    val comidas: List<MealEntry> = emptyList(),
    val panales: List<DiaperEntry> = emptyList(),
    val sueno: List<SleepEntry> = emptyList(),
    val medicamentos: List<MedicineEntry> = emptyList(),
    val notas: String = "",
    val resumen: DailySummary = DailySummary()
)