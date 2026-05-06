package com.proyecto.babybot.dailylog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.proyecto.babybot.data.firebase.AuthDataSource
import com.proyecto.babybot.data.local.entity.SleepEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.repository.HomeRepository
import com.proyecto.babybot.notifications.BabyBotNotificationHelper
import com.proyecto.babybot.notifications.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DailyLogViewModel @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val homeRepository: HomeRepository,
    @ApplicationContext private val context: Context
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

    // Registro automático de Comida
    @android.annotation.SuppressLint("MissingPermission")
    fun registerMeal(idBebe: String, tipo: String, cantidad: String) {
        viewModelScope.launch {
            val meal = MealEntity(
                idBebe = idBebe,
                tipo = tipo,
                cantidad = cantidad.toDoubleOrNull(),
                timestamp = System.currentTimeMillis()
            )
            homeRepository.addMeal(meal)

            // 1. Notificación Inmediata
            BabyBotNotificationHelper.showReminder(
                context,
                id = 1,
                title = "¡Alimentación registrada!",
                message = "Se ha guardado el registro de $tipo ($cantidad)."
            )

            // 2. RECORDATORIO DINÁMICO (Ejemplo: 3 horas = 180 minutos)
            // Primero cancelamos los previos para no encimar notificaciones
            WorkManager.getInstance(context).cancelAllWorkByTag("meal_reminder_tag")

            BabyBotNotificationHelper.scheduleSmartReminder(
                context = context,
                timeValue = 3,
                title = "Próxima toma 🍼",
                message = "Han pasado 3 horas desde la última comida, ¿es momento de alimentar al bebé?",
                tag = "meal_reminder_tag" // Pasamos el tag para controlarlo
            )

            loadDailyLog()
        }
    }

    // Registro automático de Sueño
    @android.annotation.SuppressLint("MissingPermission")
    fun registerSleep(idBebe: String, inicio: Long, fin: Long, tipo: String) {
        viewModelScope.launch {
            val sleep = SleepEntity(
                idBebe = idBebe,
                inicio = inicio,
                fin = fin,
                tipo = tipo,
                duracionMinutos = ((fin - inicio) / 60000).toInt()
            )
            homeRepository.addSleep(sleep)

            // 1. Notificación Inmediata
            BabyBotNotificationHelper.showReminder(
                context,
                id = 2,
                title = "Descanso guardado",
                message = "El registro de sueño (${tipo}) se ha completado."
            )

            // 2. RECORDATORIO DINÁMICO (Ejemplo: 2 horas = 120 minutos)
            WorkManager.getInstance(context).cancelAllWorkByTag("sleep_reminder_tag")

            BabyBotNotificationHelper.scheduleSmartReminder(
                context = context,
                timeValue = 2, // <--- TIEMPO DINÁMICO
                title = "Revisión de siesta 😴",
                message = "El bebé lleva un tiempo descansando, revisa si ya es hora de despertar.",
                tag = "sleep_reminder_tag"
            )

            loadDailyLog()
        }
    }
}