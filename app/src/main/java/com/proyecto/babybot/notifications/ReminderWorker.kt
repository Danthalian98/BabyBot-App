package com.proyecto.babybot.notifications

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.Worker
import androidx.work.WorkerParameters

// notifications/ReminderWorker.kt
class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        // Obtenemos los textos que enviamos desde el ViewModel
        val title = inputData.getString("title") ?: "BabyBot"
        val message = inputData.getString("message") ?: "Recordatorio automático"

        // AQUÍ es donde usamos el código de tu compañero de forma automática
        BabyBotNotificationHelper.showReminder(
            applicationContext,
            id = System.currentTimeMillis().toInt(),
            title = title,
            message = message
        )

        return Result.success()
    }
}