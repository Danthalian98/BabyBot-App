package com.proyecto.babybot.dailylog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import com.proyecto.babybot.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DailyLogViewModel @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyLogState())
    val state = _state.asStateFlow()

    init {
        loadDailyLog()
    }

    fun loadDailyLog() {
        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: run {
                _state.update {
                    it.copy(
                        resumen = emptyList(),
                        sections = emptyList()
                    )
                }
                return@launch
            }

            val (startRange, endRange) = getLast7DaysRange()
            val (startToday, endToday) = getTodayRange()

            val weeklyMeals = homeRepository.getMealsByRange(baby.idBebe, startRange, endRange)
            val weeklyDiapers = homeRepository.getDiapersByRange(baby.idBebe, startRange, endRange)
            val weeklySleep = homeRepository.getSleepByRange(baby.idBebe, startRange, endRange)

            val todayMeals = weeklyMeals.filter { it.timestamp in startToday..endToday }
            val todayDiapers = weeklyDiapers.filter { it.timestamp in startToday..endToday }
            val todaySleep = weeklySleep.filter { it.inicio in startToday..endToday }

            _state.update {
                it.copy(
                    title = "Registro de actividades",
                    resumen = toDailySummary(todayMeals, todayDiapers, todaySleep),
                    sections = buildWeeklySections(
                        meals = weeklyMeals,
                        diapers = weeklyDiapers,
                        sleep = weeklySleep
                    )
                )
            }
        }
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

    private fun getLast7DaysRange(): Pair<Long, Long> {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, -6)
        }.timeInMillis

        val end = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        return start to end
    }
}