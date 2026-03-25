package com.proyecto.babybot.home

import com.proyecto.babybot.data.firebase.DailyLog
import java.text.SimpleDateFormat
import java.util.Locale

fun DailyLog.toSummaryList(): List<SummaryData> {
    return listOf(
        SummaryData(
            title = "Veces que comió",
            value = "${resumen.totalComidas} veces"
        ),
        SummaryData(
            title = "Horas que durmió",
            value = "${String.format(Locale.getDefault(), "%.1f", resumen.horasSueno)} horas"
        ),
        SummaryData(
            title = "Cambio de pañal",
            value = "${resumen.totalPanales} veces"
        )
    )
}

fun DailyLog.toRecentActivities(): List<ActivityData> {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val mealActivities = comidas.map { comida ->
        ActivityData(
            icon = "🍼",
            title = "Alimento",
            description = "${comida.cantidadMl} ml",
            time = comida.hora?.toDate()?.let { formatter.format(it) } ?: "--:--",
            timestampMillis = comida.hora?.toDate()?.time ?: 0L
        )
    }

    val diaperActivities = panales.map { panal ->
        ActivityData(
            icon = "🧷",
            title = "Pañal",
            description = panal.tipo,
            time = panal.hora?.toDate()?.let { formatter.format(it) } ?: "--:--",
            timestampMillis = panal.hora?.toDate()?.time ?: 0L
        )
    }

    val sleepActivities = sueno.map { sleep ->
        val start = sleep.inicio?.toDate()
        val end = sleep.fin?.toDate()

        val durationText = if (start != null && end != null) {
            val hours = (end.time - start.time) / (1000.0 * 60.0 * 60.0)
            "${String.format(Locale.getDefault(), "%.1f", hours)} h"
        } else {
            "Sueño"
        }

        ActivityData(
            icon = "😴",
            title = "Siesta",
            description = durationText,
            time = start?.let { formatter.format(it) } ?: "--:--",
            timestampMillis = start?.time ?: 0L
        )
    }

    return (mealActivities + diaperActivities + sleepActivities)
        .sortedByDescending { it.timestampMillis }
        .take(5)
}