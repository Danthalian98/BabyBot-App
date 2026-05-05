package com.proyecto.babybot.settings.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.proyecto.babybot.notifications.SessionNotificationHelper
import com.proyecto.babybot.notifications.SessionNotificationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val app = getApplication<Application>()

    private val _state = MutableStateFlow(
        NotificationSettingsState(
            sessionNotificationsEnabled =
                SessionNotificationPreferences.areSessionNotificationsEnabled(app)
        )
    )

    val state = _state.asStateFlow()

    fun setSessionNotificationsEnabled(value: Boolean) {
        SessionNotificationPreferences.setSessionNotificationsEnabled(app, value)

        _state.update {
            it.copy(sessionNotificationsEnabled = value)
        }

        if (!value) {
            SessionNotificationHelper.cancel(app)
        }
    }

    fun setRemindersEnabled(value: Boolean) {
        _state.update { it.copy(remindersEnabled = value) }
    }

    fun setForumNotificationsEnabled(value: Boolean) {
        _state.update { it.copy(forumNotificationsEnabled = value) }
    }
}