package com.proyecto.babybot.homeTest

import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import com.proyecto.babybot.home.toSummaryList
import com.proyecto.babybot.home.toRecentActivities
import org.junit.Test
import org.junit.Assert.*

class HomeMapperTest {

    @Test
    fun `PU-16 - Lista vacia de actividades retorna resumen en ceros`() {
        val result = toSummaryList(emptyList(), emptyList(), emptyList())
        assertEquals("3 elementos en el resumen", 3, result.size)
        assertEquals("0 veces", result[0].value)
        assertEquals("0.0 horas", result[1].value)
    }

    @Test
    fun `PU-17 - Calculo de horas de sueno es correcto`() {
        val inicio = 1000L * 60 * 60 * 10
        val fin = 1000L * 60 * 60 * 12
        val sleepList = listOf(SleepEntity(idBebe = "1", inicio = inicio, fin = fin, tipo = "siesta"))

        val result = toSummaryList(emptyList(), emptyList(), sleepList)
        assertEquals("2.0 horas", result[1].value)
    }

    @Test
    fun `PU-17 - Actividades recientes solo toma las ultimas 5 y las ordena`() {
        val meals = (1..6).map { i ->
            MealEntity(idBebe = "1", tipo = "biberon", cantidad = i.toDouble(), unidad = "oz", timestamp = 1000L * i)
        }
        val result = toRecentActivities(meals, emptyList(), emptyList())
        assertEquals(5, result.size)
        assertTrue(result[0].description.contains("6.0 oz"))
    }
}