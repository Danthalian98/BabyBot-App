package com.proyecto.babybot.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.proyecto.babybot.MainActivity
import com.proyecto.babybot.R
import java.util.concurrent.TimeUnit

object BabyBotNotificationHelper {

    private const val CHANNEL_ID = "babybot_reminders_channel"
    const val DESTINATION_KEY = "destination_route"

    fun createReminderChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Recordatorios BabyBot",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Recordatorios inteligentes y avisos de BabyBot"
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun scheduleSmartReminder(
        context: Context,
        minutes: Long,
        title: String,
        message: String,
        destination: String = "home",
        tag: String
    ) {
        // Datos que le pasamos al Worker para que sepa qué decir al activarse
        val data = workDataOf(
            "title" to title,
            "message" to message,
            "destination" to destination
        )

        val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(minutes, TimeUnit.MINUTES) // El tiempo dinámico
            .setInputData(data)
            .addTag(tag) // Tag para poder cancelar después si queremos
            .build()

        // Encolamos la tarea
        WorkManager.getInstance(context).enqueueUniqueWork(
            "work_${tag}", // ID único basado en tiempo
            ExistingWorkPolicy.REPLACE,
            reminderRequest
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showReminder(
        context: Context,
        id: Int,
        title: String,
        message: String,
        destination: String? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (destination != null) {
                putExtra(DESTINATION_KEY, destination)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_babybot)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}