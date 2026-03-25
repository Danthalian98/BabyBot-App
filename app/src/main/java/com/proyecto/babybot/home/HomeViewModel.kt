package com.proyecto.babybot.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.proyecto.babybot.data.firebase.AuthDataSource
import com.proyecto.babybot.data.firebase.BabyDataSource
import com.proyecto.babybot.data.firebase.Baby
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val babyDataSource: BabyDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        val userId = authDataSource.getCurrentUserId() ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val baby = babyDataSource.getBabyByUserId(userId)

            if (baby == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        hasBaby = false
                    )
                }
                return@launch
            }

            val todayLog = babyDataSource.getTodayDailyLog(baby.idBebe)

            _state.update {
                it.copy(
                    isLoading = false,
                    hasBaby = true,
                    babyName = baby.nombre,
                    babyAge = calculateAge(baby.fechaNacimiento),
                    summary = todayLog?.toSummaryList() ?: emptyList(),
                    recentActivities = todayLog?.toRecentActivities() ?: emptyList()
                )
            }
        }
    }

    fun createBaby(
        name: String,
        gender: String,
        birthDate: Long,
        weight: Double,
        height: Double,
        bloodType: String,
        pediatrician: String,
        notes: String,
        allergies: List<String>
    ) {
        val userId = authDataSource.getCurrentUserId() ?: return

        val baby = Baby(
            idUsuario = userId,
            nombre = name,
            genero = gender,
            fechaNacimiento = Timestamp(Date(birthDate)),
            peso = weight,
            talla = height,
            tipoSangre = bloodType,
            pediatra = pediatrician,
            notas = notes,
            alergias = allergies
        )

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val success = babyDataSource.saveBaby(baby)

            if (success) {
                loadHomeData()
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun logout() {
        authDataSource.logout()
    }

    private fun calculateAge(birthDate: Timestamp?): String {
        if (birthDate == null) return "0 meses"

        val now = System.currentTimeMillis()
        val diff = now - birthDate.toDate().time
        val days = diff / (1000 * 60 * 60 * 24)
        val months = days / 30

        return when {
            months <= 0 -> "0 meses"
            months == 1L -> "1 mes"
            else -> "$months meses"
        }
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