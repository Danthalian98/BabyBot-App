package com.proyecto.babybot.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NotificationsViewModel : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())

    val notifications: StateFlow<List<NotificationItem>> = _notifications

    fun markAsRead(id: String) {
        _notifications.update { list ->
            list.map {
                if (it.id == id) it.copy(isRead = true) else it
            }
        }
    }

    fun clearAll() {
        _notifications.value = emptyList()
    }

    fun addNotification(notification: NotificationItem) {
        _notifications.update { list ->
            listOf(notification) + list
        }
    }

    fun createTestNotification(): NotificationItem {
        return NotificationItem(
            id = System.currentTimeMillis().toString(),
            title = "Recordatorio de prueba",
            message = "Esta es una notificación de prueba de BabyBot.",
            type = NotificationType.GENERAL
        )
    }
}