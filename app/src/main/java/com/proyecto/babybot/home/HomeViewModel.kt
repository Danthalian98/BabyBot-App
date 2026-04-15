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
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Intent
import androidx.core.content.ContextCompat
import com.proyecto.babybot.notifications.SessionForegroundService
import com.proyecto.babybot.notifications.SessionNotificationHelper

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val authDataSource: AuthDataSource,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadHomeData()
    }

    private fun startSessionNotification(
        sessionType: String,
        startedAt: Long,
        babyId: String
    ) {
        try {
            SessionNotificationHelper.createChannel(appContext)

            val intent = Intent(appContext, SessionForegroundService::class.java).apply {
                putExtra(SessionNotificationHelper.EXTRA_SESSION_TYPE, sessionType)
                putExtra(SessionNotificationHelper.EXTRA_STARTED_AT, startedAt)
                putExtra(SessionNotificationHelper.EXTRA_BABY_ID, babyId)
            }

            ContextCompat.startForegroundService(appContext, intent)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopSessionNotification() {
        SessionNotificationHelper.cancel(appContext)

        val intent = Intent(appContext, SessionForegroundService::class.java).apply {
            action = SessionNotificationHelper.ACTION_STOP_SERVICE
        }

        runCatching {
            appContext.startService(intent)
        }
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
                        recentActivities = emptyList(),
                        activeMealStartMillis = null,
                        activeMealSide = null,
                        activeSleepStartMillis = null,
                        activeSleepType = null
                    )
                }
                return@launch
            }

            val (startOfDay, endOfDay) = getTodayRange()

            val meals = homeRepository.getTodayMeals(baby.idBebe, startOfDay, endOfDay)
            val diapers = homeRepository.getTodayDiapers(baby.idBebe, startOfDay, endOfDay)
            val sleep = homeRepository.getTodaySleep(baby.idBebe, startOfDay, endOfDay)
            val activeSessions = homeRepository.getActiveSessions(baby.idBebe)

            val activeMeal = activeSessions.firstOrNull { it.sessionType == "meal" }
            val activeSleep = activeSessions.firstOrNull { it.sessionType == "sleep" }

            when {
                activeMeal != null -> startSessionNotification(
                    sessionType = "meal",
                    startedAt = activeMeal.startMillis,
                    babyId = baby.idBebe
                )
                activeSleep != null -> startSessionNotification(
                    sessionType = "sleep",
                    startedAt = activeSleep.startMillis,
                    babyId = baby.idBebe
                )
                //else -> stopSessionNotification()
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    hasBaby = true,
                    babyName = baby.nombre,
                    babyAge = calculateAge(baby.fechaNacimiento),
                    nextActivityTitle = "",
                    nextActivityTime = "",
                    summary = toSummaryList(meals, diapers, sleep),
                    recentActivities = toRecentActivities(meals, diapers, sleep),
                    activeMealStartMillis = activeMeal?.startMillis,
                    activeMealSide = activeMeal?.mealSide,
                    activeSleepStartMillis = activeSleep?.startMillis,
                    activeSleepType = activeSleep?.sleepType
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

    fun consumePendingMessage() {
        _state.update { it.copy(pendingMessage = null) }
    }

    fun startMealTimer(lado: String) {
        if (_state.value.activeMealStartMillis != null || _state.value.activeSleepStartMillis != null) return

        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: return@launch
            val startedAt = System.currentTimeMillis()

            homeRepository.saveActiveMealSession(
                idBebe = baby.idBebe,
                startMillis = startedAt,
                lado = lado
            )

            _state.update {
                it.copy(
                    activeMealStartMillis = startedAt,
                    activeMealSide = lado
                )
            }

            startSessionNotification(
                sessionType = "meal",
                startedAt = startedAt,
                babyId = baby.idBebe
            )
        }
    }

    fun cancelMealTimer() {
        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: return@launch
            homeRepository.clearActiveMealSession(baby.idBebe)

            _state.update {
                it.copy(
                    activeMealStartMillis = null,
                    activeMealSide = null
                )
            }

            stopSessionNotification()
        }
    }

    fun quickFinishMealTimer() {
        finishMealTimer(
            huboComplemento = false,
            tipoComplemento = null,
            cantidadComplemento = null,
            unidadComplemento = null,
            notas = "",
            etiquetas = emptyList()
        )
    }

    fun finishMealTimer(
        huboComplemento: Boolean,
        tipoComplemento: String?,
        cantidadComplemento: Double?,
        unidadComplemento: String?,
        notas: String,
        etiquetas: List<String>
    ) {
        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: return@launch

            val session = homeRepository.getActiveSessions(baby.idBebe)
                .firstOrNull { it.sessionType == "meal" }

            if (session == null) {
                _state.update {
                    it.copy(
                        activeMealStartMillis = null,
                        activeMealSide = null,
                        showMealDialog = false
                    )
                }
                loadHomeData()
                return@launch
            }

            val start = session.startMillis
            val lado = session.mealSide ?: "ambos"
            val end = System.currentTimeMillis()
            val durationMinutes = ((end - start) / 60000L).toInt().coerceAtLeast(1)

            val meal = MealEntity(
                idBebe = baby.idBebe,
                timestamp = end,
                tipo = "lactancia",
                subtipo = "pecho",
                inicio = start,
                fin = end,
                duracionMinutos = durationMinutes,
                lado = lado,
                huboComplemento = huboComplemento,
                tipoComplemento = if (huboComplemento) tipoComplemento else null,
                cantidadComplemento = if (huboComplemento) cantidadComplemento else null,
                unidadComplemento = if (huboComplemento) unidadComplemento else null,
                notas = notas.ifBlank { null },
                etiquetas = etiquetas
            )

            homeRepository.addMeal(meal)
            homeRepository.clearActiveMealSession(baby.idBebe)

            _state.update {
                it.copy(
                    activeMealStartMillis = null,
                    activeMealSide = null,
                    showMealDialog = false,
                    pendingMessage = "Lactancia guardada"
                )
            }

            stopSessionNotification()
            loadHomeData()
        }
    }

    fun startSleepTimer(tipo: String) {
        if (_state.value.activeSleepStartMillis != null || _state.value.activeMealStartMillis != null) return

        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: return@launch
            val startedAt = System.currentTimeMillis()

            homeRepository.saveActiveSleepSession(
                idBebe = baby.idBebe,
                startMillis = startedAt,
                tipo = tipo
            )

            _state.update {
                it.copy(
                    activeSleepStartMillis = startedAt,
                    activeSleepType = tipo
                )
            }

            startSessionNotification(
                sessionType = "sleep",
                startedAt = startedAt,
                babyId = baby.idBebe
            )
        }
    }

    fun cancelSleepTimer() {
        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: return@launch
            homeRepository.clearActiveSleepSession(baby.idBebe)

            _state.update {
                it.copy(
                    activeSleepStartMillis = null,
                    activeSleepType = null
                )
            }
            stopSessionNotification()
        }
    }

    fun quickFinishSleepTimer() {
        finishSleepTimer(
            lugar = "",
            calidad = "",
            notas = "",
            etiquetas = emptyList()
        )
    }

    fun finishSleepTimer(
        lugar: String,
        calidad: String,
        notas: String,
        etiquetas: List<String>
    ) {
        val userId = authDataSource.getCurrentUserId() ?: return

        viewModelScope.launch {
            val baby = homeRepository.getBabyByUserId(userId) ?: return@launch

            val session = homeRepository.getActiveSessions(baby.idBebe)
                .firstOrNull { it.sessionType == "sleep" }

            if (session == null) {
                _state.update {
                    it.copy(
                        activeSleepStartMillis = null,
                        activeSleepType = null,
                        showSleepDialog = false
                    )
                }
                loadHomeData()
                return@launch
            }

            val start = session.startMillis
            val tipo = session.sleepType ?: "siesta"
            val end = System.currentTimeMillis()
            val durationMinutes = ((end - start) / 60000L).toInt().coerceAtLeast(1)

            val sleep = SleepEntity(
                idBebe = baby.idBebe,
                inicio = start,
                fin = end,
                duracionMinutos = durationMinutes,
                tipo = tipo,
                calidad = calidad.ifBlank { null },
                lugar = lugar.ifBlank { null },
                notas = notas.ifBlank { null },
                etiquetas = etiquetas
            )

            homeRepository.addSleep(sleep)
            homeRepository.clearActiveSleepSession(baby.idBebe)

            _state.update {
                it.copy(
                    activeSleepStartMillis = null,
                    activeSleepType = null,
                    showSleepDialog = false,
                    pendingMessage = "Sueño guardado"
                )
            }

            stopSessionNotification()
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