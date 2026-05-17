package com.proyecto.babybot.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat

object NotificationPreferences {

    private const val PREFS_NAME = "babybot_notification_preferences"

    private const val KEY_GENERAL_ENABLED = "general_notifications_enabled"
    private const val KEY_SESSION_ENABLED = "session_notifications_enabled"
    private const val KEY_REMINDERS_ENABLED = "reminders_enabled"
    private const val KEY_FORUM_ENABLED = "forum_notifications_enabled"

    fun areSystemNotificationsAllowed(context: Context): Boolean {
        val appNotificationsEnabled =
            NotificationManagerCompat.from(context).areNotificationsEnabled()

        val runtimePermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        return appNotificationsEnabled && runtimePermissionGranted
    }

    fun areGeneralNotificationsEnabled(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_GENERAL_ENABLED, true)
    }

    fun setGeneralNotificationsEnabled(context: Context, enabled: Boolean) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_GENERAL_ENABLED, enabled)
            .apply()
    }

    fun areNotificationsAllowed(context: Context): Boolean {
        return areSystemNotificationsAllowed(context) &&
                areGeneralNotificationsEnabled(context)
    }

    fun areSessionNotificationsEnabled(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_SESSION_ENABLED, true)
    }

    fun setSessionNotificationsEnabled(context: Context, enabled: Boolean) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SESSION_ENABLED, enabled)
            .apply()
    }

    fun areRemindersEnabled(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_REMINDERS_ENABLED, false)
    }

    fun setRemindersEnabled(context: Context, enabled: Boolean) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_REMINDERS_ENABLED, enabled)
            .apply()
    }

    fun areForumNotificationsEnabled(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_FORUM_ENABLED, false)
    }

    fun setForumNotificationsEnabled(context: Context, enabled: Boolean) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_FORUM_ENABLED, enabled)
            .apply()
    }
}