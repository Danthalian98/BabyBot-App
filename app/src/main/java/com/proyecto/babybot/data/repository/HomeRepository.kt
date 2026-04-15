package com.proyecto.babybot.data.repository

import com.google.firebase.Timestamp
import com.proyecto.babybot.data.firebase.Baby
import com.proyecto.babybot.data.firebase.BabyDataSource
import com.proyecto.babybot.data.local.dao.ActiveSessionDao
import com.proyecto.babybot.data.local.dao.BabyDao
import com.proyecto.babybot.data.local.dao.DiaperDao
import com.proyecto.babybot.data.local.dao.MealDao
import com.proyecto.babybot.data.local.dao.SleepDao
import com.proyecto.babybot.data.local.entity.ActiveSessionEntity
import com.proyecto.babybot.data.local.entity.BabyEntity
import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val babyDao: BabyDao,
    private val mealDao: MealDao,
    private val diaperDao: DiaperDao,
    private val sleepDao: SleepDao,
    private val activeSessionDao: ActiveSessionDao,
    private val babyDataSource: BabyDataSource
) {

    suspend fun getBabyByUserId(idUsuario: String): BabyEntity? {
        return babyDao.getBaby(idUsuario)
    }

    suspend fun createBaby(
        userId: String,
        name: String,
        gender: String,
        birthDate: Long,
        weight: Double,
        height: Double,
        bloodType: String,
        pediatrician: String,
        notes: String,
        allergies: List<String>
    ): Boolean {
        val idBebe = UUID.randomUUID().toString()

        val localBaby = BabyEntity(
            idBebe = idBebe,
            idUsuario = userId,
            nombre = name,
            genero = gender,
            fechaNacimiento = birthDate,
            peso = weight,
            talla = height,
            tipoSangre = bloodType,
            pediatra = pediatrician,
            notas = notes,
            alergias = allergies
        )

        val remoteBaby = Baby(
            idBebe = idBebe,
            idUsuario = userId,
            nombre = name,
            genero = gender,
            fechaNacimiento = Timestamp(Date(birthDate)),
            peso = weight,
            talla = height,
            tipoSangre = bloodType,
            pediatra = pediatrician,
            notas = notes,
            alergias = allergies
        )

        return try {
            babyDao.insert(localBaby)
            babyDataSource.saveBaby(remoteBaby)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getTodayMeals(idBebe: String, start: Long, end: Long): List<MealEntity> {
        return mealDao.getMealsByDay(idBebe, start, end).first()
    }

    suspend fun getTodayDiapers(idBebe: String, start: Long, end: Long): List<DiaperEntity> {
        return diaperDao.getByDay(idBebe, start, end).first()
    }

    suspend fun getTodaySleep(idBebe: String, start: Long, end: Long): List<SleepEntity> {
        return sleepDao.getByDay(idBebe, start, end).first()
    }

    suspend fun getMealsByRange(idBebe: String, start: Long, end: Long): List<MealEntity> {
        return mealDao.getMealsByRange(idBebe, start, end)
    }

    suspend fun getDiapersByRange(idBebe: String, start: Long, end: Long): List<DiaperEntity> {
        return diaperDao.getByRange(idBebe, start, end)
    }

    suspend fun getSleepByRange(idBebe: String, start: Long, end: Long): List<SleepEntity> {
        return sleepDao.getByRange(idBebe, start, end)
    }

    suspend fun addMeal(meal: MealEntity) {
        mealDao.insert(meal)
    }

    suspend fun addDiaper(diaper: DiaperEntity) {
        diaperDao.insert(diaper)
    }

    suspend fun addSleep(sleep: SleepEntity) {
        sleepDao.insert(sleep)
    }

    suspend fun getActiveSessions(idBebe: String): List<ActiveSessionEntity> {
        return activeSessionDao.getByBaby(idBebe)
    }

    suspend fun saveActiveMealSession(
        idBebe: String,
        startMillis: Long,
        lado: String
    ) {
        activeSessionDao.upsert(
            ActiveSessionEntity(
                sessionKey = "$idBebe:meal",
                idBebe = idBebe,
                sessionType = "meal",
                startMillis = startMillis,
                mealSide = lado
            )
        )
    }

    suspend fun saveActiveSleepSession(
        idBebe: String,
        startMillis: Long,
        tipo: String
    ) {
        activeSessionDao.upsert(
            ActiveSessionEntity(
                sessionKey = "$idBebe:sleep",
                idBebe = idBebe,
                sessionType = "sleep",
                startMillis = startMillis,
                sleepType = tipo
            )
        )
    }

    suspend fun clearActiveMealSession(idBebe: String) {
        activeSessionDao.deleteByType(idBebe, "meal")
    }

    suspend fun clearActiveSleepSession(idBebe: String) {
        activeSessionDao.deleteByType(idBebe, "sleep")
    }
}