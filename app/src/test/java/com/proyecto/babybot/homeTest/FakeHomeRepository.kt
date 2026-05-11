package com.proyecto.babybot.homeTest

import com.google.firebase.Timestamp
import com.proyecto.babybot.data.firebase.Baby
import com.proyecto.babybot.data.repository.HomeRepository
import com.proyecto.babybot.data.local.entity.ActiveSessionEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import org.mockito.Mockito
import java.util.Date

class FakeHomeRepository : HomeRepository(
    Mockito.mock(com.proyecto.babybot.data.local.dao.BabyDao::class.java),
    Mockito.mock(com.proyecto.babybot.data.local.dao.MealDao::class.java),
    Mockito.mock(com.proyecto.babybot.data.local.dao.DiaperDao::class.java),
    Mockito.mock(com.proyecto.babybot.data.local.dao.SleepDao::class.java),
    Mockito.mock(com.proyecto.babybot.data.local.dao.ActiveSessionDao::class.java),
    Mockito.mock(com.proyecto.babybot.data.firebase.BabyDataSource::class.java)
) {
    var simularBebeNulo = false

    override suspend fun getBabyByUserId(userId: String): Baby? {
        return if (simularBebeNulo) null
        else Baby(
            idBebe = "123",
            nombre = "Bebé de Prueba",
            // ✅ Corregido: Usamos Timestamp para coincidir con el nuevo modelo
            fechaNacimiento = Timestamp(Date(1715299200000L))
        )
    }

    override suspend fun getTodayMeals(id: String, s: Long, e: Long) = emptyList<com.proyecto.babybot.data.local.entity.MealEntity>()
    override suspend fun getTodayDiapers(id: String, s: Long, e: Long) = emptyList<com.proyecto.babybot.data.local.entity.DiaperEntity>()
    override suspend fun getTodaySleep(id: String, s: Long, e: Long) = emptyList<com.proyecto.babybot.data.local.entity.SleepEntity>()
    override suspend fun getActiveSessions(id: String) = emptyList<com.proyecto.babybot.data.local.entity.ActiveSessionEntity>()

    override suspend fun getMealsByRange(
        idBebe: String,
        start: Long,
        end: Long
    ): List<MealEntity> {
        return emptyList()
    }

    override suspend fun getDiapersByRange(
        idBebe: String,
        start: Long,
        end: Long
    ): List<DiaperEntity> {
        return emptyList()
    }

    override suspend fun getSleepByRange(
        idBebe: String,
        start: Long,
        end: Long
    ): List<SleepEntity> {
        return emptyList()
    }
}