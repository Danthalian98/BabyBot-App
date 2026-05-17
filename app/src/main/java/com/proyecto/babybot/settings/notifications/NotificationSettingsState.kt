package com.proyecto.babybot.settings.notifications

data class NotificationSettingsState(
    val systemNotificationsAllowed: Boolean = true,
    val generalNotificationsEnabled: Boolean = true,
    val sessionNotificationsEnabled: Boolean = true,
    val remindersEnabled: Boolean = false,
    val forumNotificationsEnabled: Boolean = false
) {
    val canUseNotificationFeatures: Boolean
        get() = systemNotificationsAllowed && generalNotificationsEnabled
}