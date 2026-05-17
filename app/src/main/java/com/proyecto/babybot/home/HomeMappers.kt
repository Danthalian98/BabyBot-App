package com.proyecto.babybot.home

import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import java.text.SimpleDateFormat
import java.util.Locale

fun toSummaryList(
    meals: List<MealEntity>,
    diapers: List<DiaperEntity>,
    sleep: List<SleepEntity>
): List<SummaryData> {
    val totalSleepMinutes = sleep.sumOf { item ->
        val duration = item.fin - item.inicio
        if (duration > 0) (duration / 60000L).toInt() else 0
    }

    return listOf(
        SummaryData(
            title = "Alimentaciones",
            value = "${meals.size} veces"
        ),
        SummaryData(
            title = "Sueño",
            value = "$totalSleepMinutes min"
        ),
        SummaryData(
            title = "Cambios de pañal",
            value = "${diapers.size} veces"
        )
    )
}

fun toRecentActivities(
    meals: List<MealEntity>,
    diapers: List<DiaperEntity>,
    sleep: List<SleepEntity>
): List<ActivityData> {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val mealActivities = meals.map { meal ->
        val description = when (meal.tipo) {
            "lactancia" -> {
                buildString {
                    append("Lactancia")
                    meal.lado?.let { append(" · $it") }
                    meal.duracionMinutos?.let { append(" · ${it} min") }
                    if (meal.huboComplemento) append(" · con complemento")
                }
            }
            "biberon" -> {
                val amount = if (meal.cantidad != null && !meal.unidad.isNullOrBlank()) {
                    "${meal.cantidad} ${meal.unidad}"
                } else {
                    "Cantidad no especificada"
                }
                "Biberón · $amount"
            }
            "complementaria" -> {
                meal.alimentoDescripcion?.let { "Complementaria · $it" }
                    ?: "Alimentación complementaria"
            }
            else -> meal.notas ?: "Registro de alimentación"
        }

        ActivityData(
            icon = "🍼",
            title = "Alimentación",
            description = description,
            time = formatter.format(meal.timestamp),
            timestampMillis = meal.timestamp
        )
    }

    val diaperActivities = diapers.map { diaper ->
        val desc = buildString {
            append(
                when (diaper.tipo) {
                    "pipi" -> "Pipí"
                    "popo" -> "Popó"
                    "ambos" -> "Pipí y popó"
                    else -> diaper.tipo
                }
            )
            diaper.color?.let { append(" · $it") }
            diaper.consistencia?.let { append(" · $it") }
            diaper.cantidad?.let { append(" · $it") }
        }

        ActivityData(
            icon = "🧷",
            title = "Pañal",
            description = desc,
            time = formatter.format(diaper.timestamp),
            timestampMillis = diaper.timestamp
        )
    }

    val sleepActivities = sleep.map { nap ->
        val durationMinutes = ((nap.fin - nap.inicio) / 60000L)
            .toInt()
            .coerceAtLeast(0)

        val desc = buildString {
            append(
                when (nap.tipo) {
                    "nocturno" -> "Sueño nocturno"
                    "siesta" -> "Siesta"
                    else -> "Sueño"
                }
            )
            append(" · $durationMinutes min")
            nap.lugar?.let { append(" · $it") }
        }

        ActivityData(
            icon = "😴",
            title = "Sueño",
            description = desc,
            time = formatter.format(nap.inicio),
            timestampMillis = nap.inicio
        )
    }

    return (mealActivities + diaperActivities + sleepActivities)
        .sortedByDescending { it.timestampMillis }
        .take(5)
}