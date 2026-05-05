package com.proyecto.babybot.notifications

import android.content.Context

object SessionNotificationPreferences {

    private const val PREFS_NAME = "babybot_notification_preferences"
    private const val KEY_SESSION_NOTIFICATIONS_ENABLED = "session_notifications_enabled"

    fun areSessionNotificationsEnabled(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_SESSION_NOTIFICATIONS_ENABLED, true)
    }

    fun setSessionNotificationsEnabled(context: Context, enabled: Boolean) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SESSION_NOTIFICATIONS_ENABLED, enabled)
            .apply()
    }
}