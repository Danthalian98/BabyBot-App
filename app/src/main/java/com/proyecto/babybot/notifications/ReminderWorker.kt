package com.proyecto.babybot.notifications

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "BabyBot"
        val message = inputData.getString("message") ?: "Recordatorio automático"
        val destination = inputData.getString("destination") ?: "home"

        BabyBotNotificationHelper.showReminder(
            applicationContext,
            id = System.currentTimeMillis().toInt(),
            title = title,
            message = message,
            destination = destination
        )

        return Result.success()
    }
}