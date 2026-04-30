package com.proyecto.babybot.notifications

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class NotificationType {
    FORUM_REPLY,
    FEEDING_REMINDER,
    DIAPER_REMINDER,
    SLEEP_REMINDER,
    GENERAL
}