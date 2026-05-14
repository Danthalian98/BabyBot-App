package com.proyecto.babybot.notifications

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        return try {
            val title = inputData.getString("title") ?: "BabyBot"
            val message = inputData.getString("message") ?: "Recordatorio automático"
            val destination = inputData.getString("destination") ?: "home"

            // Es vital que el ID sea único para que no se sobreescriban si hay varios
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
            // Si algo falla, Result.retry() intentará lanzarla más tarde
            Result.retry()
        }
    }
}