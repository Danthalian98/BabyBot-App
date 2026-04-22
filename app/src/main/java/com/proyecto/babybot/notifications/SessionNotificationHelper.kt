package com.proyecto.babybot.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object SessionNotificationHelper {

    const val CHANNEL_ID = "babybot_session_channel"
    const val NOTIFICATION_ID = 1001

    const val EXTRA_SESSION_TYPE = "extra_session_type"
    const val EXTRA_STARTED_AT = "extra_started_at"
    const val EXTRA_BABY_ID = "extra_baby_id"

    const val ACTION_FINISH_MEAL = "action_finish_meal"
    const val ACTION_CANCEL_MEAL = "action_cancel_meal"
    const val ACTION_FINISH_SLEEP = "action_finish_sleep"
    const val ACTION_CANCEL_SLEEP = "action_cancel_sleep"
    const val ACTION_STOP_SERVICE = "action_stop_service"

    // broadcast interno para refrescar Home cuando una sesión cambie desde notificación
    const val ACTION_SESSION_CHANGED = "com.proyecto.babybot.ACTION_SESSION_CHANGED"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sesiones activas",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificación para cronómetros activos"
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun buildNotification(
        context: Context,
        sessionType: String,
        startedAt: Long,
        babyId: String
    ): Notification {
        val title = if (sessionType == "meal") {
            "🍼 Lactancia en curso"
        } else {
            "😴 Sueño en curso"
        }

        val finishAction = if (sessionType == "meal") ACTION_FINISH_MEAL else ACTION_FINISH_SLEEP
        val cancelAction = if (sessionType == "meal") ACTION_CANCEL_MEAL else ACTION_CANCEL_SLEEP

        val finishIntent = Intent(context, SessionActionReceiver::class.java).apply {
            action = finishAction
            putExtra(EXTRA_SESSION_TYPE, sessionType)
            putExtra(EXTRA_BABY_ID, babyId)
        }

        val cancelIntent = Intent(context, SessionActionReceiver::class.java).apply {
            action = cancelAction
            putExtra(EXTRA_SESSION_TYPE, sessionType)
            putExtra(EXTRA_BABY_ID, babyId)
        }

        val finishPending = PendingIntent.getBroadcast(
            context,
            if (sessionType == "meal") 100 else 200,
            finishIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelPending = PendingIntent.getBroadcast(
            context,
            if (sessionType == "meal") 101 else 201,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val elapsedMillis = System.currentTimeMillis() - startedAt
        val totalSeconds = elapsedMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val elapsedText = if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("Tiempo transcurrido · $elapsedText")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setStyle(
                NotificationCompat.DecoratedCustomViewStyle()
            )
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .addAction(0, "Finalizar", finishPending)
            .addAction(0, "Cancelar", cancelPending)
            .build()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notify(
        context: Context,
        sessionType: String,
        startedAt: Long,
        babyId: String
    ) {
        val notification = buildNotification(context, sessionType, startedAt, babyId)

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        NotificationManagerCompat.from(context)
            .cancel(NOTIFICATION_ID)
    }

    private fun formatElapsed(elapsedMillis: Long): String {
        val totalSeconds = elapsedMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}