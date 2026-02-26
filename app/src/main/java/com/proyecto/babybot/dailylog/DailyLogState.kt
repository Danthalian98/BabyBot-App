package com.proyecto.babybot.dailylog

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class DailyLogState(
    val title: String = "Registro de actividades",
    val resumen: List<DailySummary> = emptyList(),
    val sections: List<DailySection> = emptyList()
)

data class DailySummary(
    val icon: ImageVector,
    val value: String,
    val label: String
)

data class DailySection(
    val date: String,
    val activities: List<DailyActivity>
)

data class DailyActivity(
    val type: ActivityType,
    val title: String,
    val information: String,
    val time: String
)

data class ActivityType(
    val color: Color,
    val icon: ImageVector
)