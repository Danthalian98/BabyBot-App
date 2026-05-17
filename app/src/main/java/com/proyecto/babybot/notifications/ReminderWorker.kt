package com.proyecto.babybot.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            if (!NotificationPreferences.areNotificationsAllowed(applicationContext)) {
                return Result.success()
            }

            if (!NotificationPreferences.areRemindersEnabled(applicationContext)) {
                return Result.success()
            }

            val title = inputData.getString("title") ?: "BabyBot"
            val message = inputData.getString("message") ?: "Recordatorio automático"
            val destination = inputData.getString("destination") ?: "home"

            val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

            BabyBotNotificationHelper.showReminder(
                applicationContext,
                id = notificationId,
                title = title,
                message = message,
                destination = destination
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}