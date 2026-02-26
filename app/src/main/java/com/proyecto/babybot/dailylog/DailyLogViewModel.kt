package com.proyecto.babybot.dailylog

import androidx.lifecycle.ViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.outlined.CalendarToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.compose.ui.graphics.Color


@HiltViewModel
class DailyLogViewModel @Inject constructor() : ViewModel() {

    private val comida = ActivityType(Color(0xFF2D9CDB), Icons.Rounded.Favorite)
    private val pañal = ActivityType(Color(0xFFFFA000), Icons.Rounded.ChildCare)
    private val sueño = ActivityType(Color(0xFF6200EE), Icons.Rounded.Bedtime)
    private val baño = ActivityType(Color(0xFF00C853), Icons.Rounded.Shower)

    private val _state = MutableStateFlow(
        DailyLogState(
            resumen = listOf(
                DailySummary(Icons.Rounded.LocalDrink, "8", "Comidas"),
                DailySummary(Icons.Rounded.Bedtime, "12h", "Sueño"),
                DailySummary(Icons.Rounded.ChildCare, "5", "Pañales")
            ),
            sections = listOf(
                DailySection(
                    date = "Hoy - 24 Febrero",
                    activities = listOf(
                        DailyActivity(comida, "Comida", "Papilla", "12:00 AM"),
                        DailyActivity(pañal, "Pañal", "Desechos líquidos", "12:30 AM"),
                        DailyActivity(sueño, "Sueño", "1h 32min", "8:50 AM"),
                        DailyActivity(baño, "Baño", "Ya está limpio", "9:24 AM")
                    )
                ),
                DailySection(
                    date = "Ayer - 23 Febrero",
                    activities = listOf(
                        DailyActivity(comida, "Comida", "Leche 120ml", "10:00 PM")
                    )
                )
            )
        )
    )

    val state = _state.asStateFlow()
}