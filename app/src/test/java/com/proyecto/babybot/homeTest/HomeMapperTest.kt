package com.proyecto.babybot.homeTest

import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.home.toSummaryList
import com.proyecto.babybot.home.toRecentActivities
import org.junit.Test
import org.junit.Assert.*

class HomeMapperTest {

    @Test
    fun `PU-16 - Lista vacia de actividades retorna resumen en ceros`() {
        // Given: Listas vacías
        val meals = emptyList<MealEntity>()
        val diapers = emptyList<Nothing>()
        val sleep = emptyList<Nothing>()

        // When: Ejecutamos el mapper de resumen
        val result = toSummaryList(meals, diapers, sleep)

        // Then: Verificamos que los valores sean "0 veces" o "0.0 horas"
        assertEquals("3 elementos en el resumen", 3, result.size)
        assertEquals("Alimentaciones", result[0].title)
        assertEquals("0 veces", result[0].value)
        assertEquals("0.0 horas", result[1].value)
    }

    @Test
    fun `PU-17 - Calculo de horas de sueno es correcto`() {
        // Given: Un registro de sueño de 2 horas (en milisegundos)
        val inicio = 1000L * 60 * 60 * 10 // 10:00 AM
        val fin = 1000L * 60 * 60 * 12    // 12:00 PM

        // Creamos una entidad simulada (ajusta los campos según tu SleepEntity)
        val sleepList = listOf(
            com.proyecto.babybot.data.local.entity.SleepEntity(
                idBebe = "1",
                inicio = inicio,
                fin = fin,
                tipo = "siesta"
            )
        )

        // When
        val result = toSummaryList(emptyList(), emptyList(), sleepList)

        // Then: El segundo elemento (índice 1) es el sueño
        assertEquals("2.0 horas", result[1].value)
    }

    @Test
    fun `PU-17 - Actividades recientes solo toma las ultimas 5 y las ordena`() {
        // Given: Simulamos 6 comidas en diferentes tiempos
        val meals = (1..6).map { i ->
            com.proyecto.babybot.data.local.entity.MealEntity(
                idBebe = "1",
                tipo = "biberon",
                cantidad = i.toDouble(),
                unidad = "oz",
                timestamp = 1000L * i
            )
        }

        // When
        val result = toRecentActivities(meals, emptyList(), emptyList())

        // Then
        assertEquals("Debe retornar máximo 5 elementos", 5, result.size)
        // La primera debe ser la más reciente (la número 6)
        assertTrue(result[0].description.contains("6.0 oz"))
    }
}