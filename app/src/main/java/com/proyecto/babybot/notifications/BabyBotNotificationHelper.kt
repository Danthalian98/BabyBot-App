package com.proyecto.babybot.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.proyecto.babybot.R
import android.graphics.BitmapFactory

object BabyBotNotificationHelper {

    private const val CHANNEL_ID = "babybot_reminders_channel"

    fun createReminderChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Recordatorios BabyBot",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Recordatorios inteligentes y avisos de BabyBot"
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(channel)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showReminder(
        context: Context,
        id: Int,
        title: String,
        message: String
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_babybot)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }
}