package com.proyecto.babybot.home

import androidx.lifecycle.ViewModel
import com.proyecto.babybot.data.firebase.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    fun logout() {
        authDataSource.logout()
    }

    private val _state = MutableStateFlow(
        HomeState(
            babyName = "Oliver",
            babyAge = "3 meses",
            nextActivityTitle = "Alimentar al bebé",
            nextActivityTime = "12:00 AM",
            summary = listOf(
                SummaryData("¿Cuánto comió hoy?", "3 veces"),
                SummaryData("¿Cuánto durmió hoy?", "4 horas"),
                SummaryData("¿Cuánto se cambió el pañal?", "2 veces")
            ),
            recentActivities = listOf(
                ActivityData("Comida", "120ml", "10:30 AM"),
                ActivityData("Sueño", "2 horas", "08:00 AM")
            )
        )
    )

    val state = _state.asStateFlow()
}