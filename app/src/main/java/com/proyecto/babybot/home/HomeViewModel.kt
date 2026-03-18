package com.proyecto.babybot.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Necesario para viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import com.proyecto.babybot.data.firebase.BabyDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // Necesario para .update { }
import kotlinx.coroutines.launch // Necesario para lanzar corrutinas
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val babyDataSource: BabyDataSource
) : ViewModel() {

    // Inicializamos el estado con tus valores por defecto o vacíos
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        checkIfUserHasBaby()
    }

    fun checkIfUserHasBaby() {
        val userId = authDataSource.getCurrentUserId() ?: return

        // Entramos al "mundo" de las corrutinas
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val babyData = babyDataSource.getBabyByUserId(userId)

            if (babyData != null) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        hasBaby = true,
                        babyName = babyData["name"] as? String ?: "",
                        babyAge = calculateAge(babyData["birthDate"] as? Long)
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, hasBaby = false) }
            }
        }
    }

    fun createBaby(
        name: String,
        gender: String,
        birthDate: Long,
        weight: Double,
        height: Double
    ) {
        val userId = authDataSource.getCurrentUserId() ?: return

        val baby: Map<String, Any> = hashMapOf(
            "userId" to userId,
            "name" to name,
            "gender" to gender,
            "birthDate" to birthDate,
            "weight" to weight,
            "height" to height,
            "createdAt" to System.currentTimeMillis()
        )

        viewModelScope.launch {
            val success = babyDataSource.saveBaby(baby)
            if (success) {
                checkIfUserHasBaby() // Refrescamos
            }
        }
    }

    fun logout() {
        authDataSource.logout()
    }

    private fun calculateAge(birthDate: Long?): String {
        if (birthDate == null) return "0 meses"
        val now = System.currentTimeMillis()
        val diff = now - birthDate
        val days = diff / (1000 * 60 * 60 * 24)
        val months = days / 30
        return "$months meses"
    }
}
fun formatDate(timestamp: Long?): String {
    return timestamp?.let {
        java.time.Instant.ofEpochMilli(it)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
            .toString()
    } ?: "Seleccionar fecha"
}