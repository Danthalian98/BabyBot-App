package com.proyecto.babybot.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import com.proyecto.babybot.data.repository.HomeRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SessionActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    appContext,
                    SessionReceiverEntryPoint::class.java
                )

                val homeRepository = entryPoint.homeRepository()

                val babyId = intent?.getStringExtra(SessionNotificationHelper.EXTRA_BABY_ID)
                val sessionType = intent?.getStringExtra(SessionNotificationHelper.EXTRA_SESSION_TYPE)

                if (babyId.isNullOrBlank() || sessionType.isNullOrBlank()) {
                    stopService(appContext)
                    return@launch
                }

                when (intent.action) {
                    SessionNotificationHelper.ACTION_FINISH_MEAL -> {
                        val session = homeRepository.getActiveSessions(babyId)
                            .firstOrNull { it.sessionType == "meal" }

                        if (session != null) {
                            val end = System.currentTimeMillis()
                            val durationMinutes =
                                ((end - session.startMillis) / 60000L).toInt().coerceAtLeast(1)

                            val meal = MealEntity(
                                idBebe = babyId,
                                timestamp = end,
                                tipo = "lactancia",
                                subtipo = "pecho",
                                inicio = session.startMillis,
                                fin = end,
                                duracionMinutos = durationMinutes,
                                lado = session.mealSide ?: "ambos",
                                notas = null,
                                etiquetas = emptyList()
                            )

                            homeRepository.addMeal(meal)
                            homeRepository.clearActiveMealSession(babyId)
                        }

                        stopService(appContext)
                        notifySessionChanged(appContext, "meal")
                    }

                    SessionNotificationHelper.ACTION_CANCEL_MEAL -> {
                        homeRepository.clearActiveMealSession(babyId)
                        stopService(appContext)
                        notifySessionChanged(appContext, "meal")
                    }

                    SessionNotificationHelper.ACTION_FINISH_SLEEP -> {
                        val session = homeRepository.getActiveSessions(babyId)
                            .firstOrNull { it.sessionType == "sleep" }

                        if (session != null) {
                            val end = System.currentTimeMillis()
                            val durationMinutes =
                                ((end - session.startMillis) / 60000L).toInt().coerceAtLeast(1)

                            val sleep = SleepEntity(
                                idBebe = babyId,
                                inicio = session.startMillis,
                                fin = end,
                                duracionMinutos = durationMinutes,
                                tipo = session.sleepType ?: "siesta",
                                notas = null,
                                etiquetas = emptyList()
                            )

                            homeRepository.addSleep(sleep)
                            homeRepository.clearActiveSleepSession(babyId)
                        }

                        stopService(appContext)
                        notifySessionChanged(appContext, "sleep")
                    }

                    SessionNotificationHelper.ACTION_CANCEL_SLEEP -> {
                        homeRepository.clearActiveSleepSession(babyId)
                        stopService(appContext)
                        notifySessionChanged(appContext, "sleep")
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun stopService(context: Context) {
        SessionNotificationHelper.cancel(context)

        val serviceIntent = Intent(context, SessionForegroundService::class.java).apply {
            action = SessionNotificationHelper.ACTION_STOP_SERVICE
        }
        context.stopService(Intent(context, SessionForegroundService::class.java))
    }

    private fun notifySessionChanged(context: Context, sessionType: String) {
        val refreshIntent = Intent(SessionNotificationHelper.ACTION_SESSION_CHANGED).apply {
            setPackage(context.packageName)
            putExtra(SessionNotificationHelper.EXTRA_SESSION_TYPE, sessionType)
        }
        context.sendBroadcast(refreshIntent)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SessionReceiverEntryPoint {
    fun homeRepository(): HomeRepository
}