package com.proyecto.babybot.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import com.proyecto.babybot.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val homeRepository: HomeRepository
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

            val baby = homeRepository.getBabyByUserId(userId)

            if (baby == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        hasBaby = false,
                        babyName = "",
                        babyAge = "",
                        summary = emptyList(),
                        recentActivities = emptyList()
                    )
                }
                return@launch
            }

            val (startOfDay, endOfDay) = getTodayRange()

            val meals = homeRepository.getTodayMeals(baby.idBebe, startOfDay, endOfDay)
            val diapers = homeRepository.getTodayDiapers(baby.idBebe, startOfDay, endOfDay)
            val sleep = homeRepository.getTodaySleep(baby.idBebe, startOfDay, endOfDay)

            _state.update {
                it.copy(
                    isLoading = false,
                    hasBaby = true,
                    babyName = baby.nombre,
                    babyAge = calculateAge(baby.fechaNacimiento),
                    nextActivityTitle = "",
                    nextActivityTime = "",
                    summary = toSummaryList(meals, diapers, sleep),
                    recentActivities = toRecentActivities(meals, diapers, sleep)
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

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val success = homeRepository.createBaby(
                userId = userId,
                name = name,
                gender = gender,
                birthDate = birthDate,
                weight = weight,
                height = height,
                bloodType = bloodType,
                pediatrician = pediatrician,
                notes = notes,
                allergies = allergies
            )

            if (success) loadHomeData()
            else _state.update { it.copy(isLoading = false) }
        }
    }

    fun openMealDialog() = _state.update { it.copy(showMealDialog = true) }
    fun closeMealDialog() = _state.update { it.copy(showMealDialog = false) }

    fun openDiaperDialog() = _state.update { it.copy(showDiaperDialog = true) }
    fun closeDiaperDialog() = _state.update { it.copy(showDiaperDialog = false) }

    fun openSleepDialog() = _state.update { it.copy(showSleepDialog = true) }
    fun closeSleepDialog() = _state.update { it.copy(showSleepDialog = false) }

    fun saveMeal(meal: MealEntity) {
        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: return@launch
            homeRepository.addMeal(meal.copy(idBebe = baby.idBebe))
            closeMealDialog()
            loadHomeData()
        }
    }

    fun saveDiaper(diaper: DiaperEntity) {
        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: return@launch
            homeRepository.addDiaper(diaper.copy(idBebe = baby.idBebe))
            closeDiaperDialog()
            loadHomeData()
        }
    }

    fun saveSleep(sleep: SleepEntity) {
        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: return@launch
            homeRepository.addSleep(sleep.copy(idBebe = baby.idBebe))
            closeSleepDialog()
            loadHomeData()
        }
    }

    fun logout() {
        authDataSource.logout()
    }

    private fun getTodayRange(): Pair<Long, Long> {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val end = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        return start to end
    }

    private fun calculateAge(birthDateMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - birthDateMillis
        val days = diff / (1000L * 60L * 60L * 24L)
        val months = days / 30L

        return when {
            months <= 0L -> "0 meses"
            months == 1L -> "1 mes"
            else -> "$months meses"
        }
    }
}

fun formatDate(timestamp: Long?): String {
    return timestamp?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .toString()
    } ?: "Seleccionar fecha"
}