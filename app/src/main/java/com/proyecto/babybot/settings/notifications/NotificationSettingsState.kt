package com.proyecto.babybot.settings.notifications

data class NotificationSettingsState(
    val sessionNotificationsEnabled: Boolean = true,
    val remindersEnabled: Boolean = false,
    val forumNotificationsEnabled: Boolean = false
)