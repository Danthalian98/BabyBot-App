package com.proyecto.babybot.notifications

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SessionForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var sessionType: String? = null
    private var startedAt: Long = 0L
    private var babyId: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            SessionNotificationHelper.ACTION_STOP_SERVICE -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }

            else -> {
                sessionType = intent?.getStringExtra(SessionNotificationHelper.EXTRA_SESSION_TYPE)
                startedAt = intent?.getLongExtra(
                    SessionNotificationHelper.EXTRA_STARTED_AT,
                    System.currentTimeMillis()
                ) ?: System.currentTimeMillis()

                babyId = intent?.getStringExtra(SessionNotificationHelper.EXTRA_BABY_ID)
                    ?: return START_NOT_STICKY
            }
        }

        val type = sessionType ?: return START_NOT_STICKY
        val currentBabyId = babyId ?: return START_NOT_STICKY

        if (!canPostNotifications()) {
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = SessionNotificationHelper.buildNotification(
            context = this,
            sessionType = type,
            startedAt = startedAt,
            babyId = currentBabyId
        )

        try {
            startForeground(SessionNotificationHelper.NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
            stopSelf()
            return START_NOT_STICKY
        }

        startTicker()

        return START_STICKY
    }

    private fun startTicker() {
        serviceScope.launch {
            while (isActive) {
                delay(1000)

                val type = sessionType ?: continue
                val currentBabyId = babyId ?: continue

                if (canPostNotifications()) {
                    try {
                        SessionNotificationHelper.notify(
                            context = this@SessionForegroundService,
                            sessionType = type,
                            startedAt = startedAt,
                            babyId = currentBabyId
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        stopSelf()
                        break
                    }
                }
            }
        }
    }

    private fun canPostNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}