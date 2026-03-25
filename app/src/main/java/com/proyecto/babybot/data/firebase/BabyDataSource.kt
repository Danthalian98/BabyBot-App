package com.proyecto.babybot.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.util.Calendar
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
            val docRef = firestore.collection("bebes").document()

            val babyToSave = baby.copy(
                idBebe = docRef.id,
                fechaCreacion = Timestamp.now(),
                activo = true
            )

            docRef.set(babyToSave).await()

            createInitialDailyLogs(
                idBebe = docRef.id,
                idUsuario = baby.idUsuario
            )

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getTodayDailyLog(idBebe: String): DailyLog? {
        return try {
            val result = firestore.collection("registros_diarios")
                .whereEqualTo("idBebe", idBebe)
                .get()
                .await()

            val logs = result.documents.mapNotNull { doc ->
                doc.toObject(DailyLog::class.java)
            }

            val startOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            logs.firstOrNull { log ->
                val fechaMillis = log.fecha?.toDate()?.time ?: return@firstOrNull false
                fechaMillis in startOfDay..endOfDay
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getRecentDailyLogs(idBebe: String, limit: Long = 7): List<DailyLog> {
        return try {
            val result = firestore.collection("registros_diarios")
                .whereEqualTo("idBebe", idBebe)
                .get()
                .await()

            result.documents
                .mapNotNull { it.toObject(DailyLog::class.java) }
                .sortedByDescending { it.fecha?.toDate()?.time ?: 0L }
                .take(limit.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addMeal(
        dailyLogId: String,
        cantidadMl: Int,
        tipo: String = "Leche"
    ): Boolean {
        return try {
            val dailyLogRef = firestore.collection("registros_diarios").document(dailyLogId)
            val snapshot = dailyLogRef.get().await()
            val dailyLog = snapshot.toObject(DailyLog::class.java) ?: return false

            val updatedMeals = dailyLog.comidas.toMutableList().apply {
                add(
                    MealEntry(
                        hora = Timestamp.now(),
                        cantidadMl = cantidadMl,
                        tipo = tipo
                    )
                )
            }

            val updatedSummary = dailyLog.resumen.copy(
                totalComidas = updatedMeals.size
            )

            dailyLogRef.update(
                mapOf(
                    "comidas" to updatedMeals,
                    "resumen" to updatedSummary
                )
            ).await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addDiaperChange(
        dailyLogId: String,
        tipo: String
    ): Boolean {
        return try {
            val dailyLogRef = firestore.collection("registros_diarios").document(dailyLogId)
            val snapshot = dailyLogRef.get().await()
            val dailyLog = snapshot.toObject(DailyLog::class.java) ?: return false

            val updatedDiapers = dailyLog.panales.toMutableList().apply {
                add(
                    DiaperEntry(
                        hora = Timestamp.now(),
                        tipo = tipo
                    )
                )
            }

            val updatedSummary = dailyLog.resumen.copy(
                totalPanales = updatedDiapers.size
            )

            dailyLogRef.update(
                mapOf(
                    "panales" to updatedDiapers,
                    "resumen" to updatedSummary
                )
            ).await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addSleepEntry(
        dailyLogId: String,
        inicio: Timestamp,
        fin: Timestamp
    ): Boolean {
        return try {
            val dailyLogRef = firestore.collection("registros_diarios").document(dailyLogId)
            val snapshot = dailyLogRef.get().await()
            val dailyLog = snapshot.toObject(DailyLog::class.java) ?: return false

            val updatedSleep = dailyLog.sueno.toMutableList().apply {
                add(
                    SleepEntry(
                        inicio = inicio,
                        fin = fin
                    )
                )
            }

            val totalHours = updatedSleep.sumOf { sleep ->
                val start = sleep.inicio?.toDate()?.time ?: 0L
                val end = sleep.fin?.toDate()?.time ?: 0L
                if (end > start) {
                    (end - start) / (1000.0 * 60.0 * 60.0)
                } else {
                    0.0
                }
            }

            val updatedSummary = dailyLog.resumen.copy(
                horasSueno = totalHours
            )

            dailyLogRef.update(
                mapOf(
                    "sueno" to updatedSleep,
                    "resumen" to updatedSummary
                )
            ).await()

            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun createInitialDailyLogs(idBebe: String, idUsuario: String) {
        val batch = firestore.batch()
        val hoy = Calendar.getInstance()

        for (i in 0 until 7) {
            val fecha = (hoy.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -i)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val docRef = firestore.collection("registros_diarios").document()

            val registro = DailyLog(
                idRegistro = docRef.id,
                idBebe = idBebe,
                idUsuario = idUsuario,
                fecha = Timestamp(fecha.time),
                comidas = emptyList(),
                panales = emptyList(),
                sueno = emptyList(),
                medicamentos = emptyList(),
                notas = "",
                resumen = DailySummary(
                    totalComidas = 0,
                    totalPanales = 0,
                    horasSueno = 0.0
                )
            )

            batch.set(docRef, registro)
        }

        batch.commit().await()
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