package com.proyecto.babybot.dailylog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.ui.graphics.Color
import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val comidaType = ActivityType(Color(0xFF2D9CDB), Icons.Rounded.LocalDrink)
private val panalType = ActivityType(Color(0xFFFFA000), Icons.Rounded.ChildCare)
private val suenoType = ActivityType(Color(0xFF6200EE), Icons.Rounded.Bedtime)

fun toDailySummary(
    meals: List<MealEntity>,
    diapers: List<DiaperEntity>,
    sleep: List<SleepEntity>
): List<DailySummary> {
    val totalSleepHours = sleep.sumOf { item ->
        val duration = item.fin - item.inicio
        if (duration > 0) duration / (1000.0 * 60.0 * 60.0) else 0.0
    }

    return listOf(
        DailySummary(
            icon = Icons.Rounded.LocalDrink,
            value = meals.size.toString(),
            label = "Comidas"
        ),
        DailySummary(
            icon = Icons.Rounded.Bedtime,
            value = String.format(Locale.getDefault(), "%.1fh", totalSleepHours),
            label = "Sueño"
        ),
        DailySummary(
            icon = Icons.Rounded.ChildCare,
            value = diapers.size.toString(),
            label = "Pañales"
        )
    )
}

private data class DatedActivity(
    val dayStartMillis: Long,
    val timestampMillis: Long,
    val activity: DailyActivity
)

fun buildWeeklySections(
    meals: List<MealEntity>,
    diapers: List<DiaperEntity>,
    sleep: List<SleepEntity>
): List<DailySection> {
    val allActivities = mutableListOf<DatedActivity>()

    allActivities += meals.map { meal ->
        val timestamp = meal.timestamp
        DatedActivity(
            dayStartMillis = startOfDay(timestamp),
            timestampMillis = timestamp,
            activity = DailyActivity(
                type = comidaType,
                title = "Comida",
                information = buildMealDescription(meal),
                time = formatHour(timestamp)
            )
        )
    }

    allActivities += diapers.map { diaper ->
        val timestamp = diaper.timestamp
        DatedActivity(
            dayStartMillis = startOfDay(timestamp),
            timestampMillis = timestamp,
            activity = DailyActivity(
                type = panalType,
                title = "Pañal",
                information = buildDiaperDescription(diaper),
                time = formatHour(timestamp)
            )
        )
    }

    allActivities += sleep.map { sleepItem ->
        val timestamp = sleepItem.inicio
        DatedActivity(
            dayStartMillis = startOfDay(timestamp),
            timestampMillis = timestamp,
            activity = DailyActivity(
                type = suenoType,
                title = if (sleepItem.tipo == "nocturno") "Sueño nocturno" else "Siesta",
                information = buildSleepDescription(sleepItem),
                time = formatHour(timestamp)
            )
        )
    }

    return allActivities
        .sortedByDescending { it.timestampMillis }
        .groupBy { it.dayStartMillis }
        .toSortedMap(compareByDescending { it })
        .map { (dayStart, items) ->
            DailySection(
                date = buildSectionTitle(dayStart),
                activities = items
                    .sortedByDescending { it.timestampMillis }
                    .map { it.activity }
            )
        }
}

private fun buildMealDescription(meal: MealEntity): String {
    return when (meal.tipo) {
        "lactancia" -> buildString {
            append("Lactancia")
            meal.lado?.let { append(" · $it") }
            meal.duracionMinutos?.let { append(" · ${it} min") }
            if (meal.huboComplemento) append(" · complemento")
            meal.notas?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
        }

        "biberon" -> buildString {
            append("Biberón")
            if (meal.cantidad != null && !meal.unidad.isNullOrBlank()) {
                append(" · ${meal.cantidad} ${meal.unidad}")
            }
            meal.subtipo?.let { append(" · $it") }
            meal.notas?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
        }

        "complementaria" -> buildString {
            append("Complementaria")
            meal.alimentoDescripcion?.let { append(" · $it") }
            meal.reaccion?.let { append(" · $it") }
            meal.notas?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
        }

        else -> meal.notas?.takeIf { it.isNotBlank() } ?: "Registro de alimentación"
    }
}

private fun buildDiaperDescription(diaper: DiaperEntity): String {
    return buildString {
        append(
            when (diaper.tipo) {
                "pipi" -> "Pipí"
                "popo" -> "Popó"
                "ambos" -> "Pipí y popó"
                else -> diaper.tipo
            }
        )
        diaper.color?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
        diaper.consistencia?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
        diaper.cantidad?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
        diaper.notas?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
    }
}

private fun buildSleepDescription(sleep: SleepEntity): String {
    val durationHours = (sleep.fin - sleep.inicio) / (1000.0 * 60.0 * 60.0)
    return buildString {
        append(String.format(Locale.getDefault(), "%.1f h", durationHours))
        sleep.lugar?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
        sleep.calidad?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
        sleep.notas?.takeIf { it.isNotBlank() }?.let { append(" · $it") }
    }
}

private fun formatHour(timestamp: Long): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

private fun startOfDay(timestamp: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun buildSectionTitle(dayStartMillis: Long): String {
    val todayStart = startOfDay(System.currentTimeMillis())
    val yesterdayStart = Calendar.getInstance().apply {
        timeInMillis = todayStart
        add(Calendar.DAY_OF_YEAR, -1)
    }.timeInMillis

    val formatter = SimpleDateFormat("dd MMMM", Locale("es", "MX"))

    return when (dayStartMillis) {
        todayStart -> "Hoy - ${formatter.format(Date(dayStartMillis))}"
        yesterdayStart -> "Ayer - ${formatter.format(Date(dayStartMillis))}"
        else -> formatter.format(Date(dayStartMillis))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "MX")) else it.toString() }
    }
}