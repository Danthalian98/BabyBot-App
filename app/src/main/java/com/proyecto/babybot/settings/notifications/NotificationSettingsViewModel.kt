package com.proyecto.babybot.settings.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.proyecto.babybot.notifications.BabyBotNotificationHelper
import com.proyecto.babybot.notifications.NotificationPreferences
import com.proyecto.babybot.notifications.SessionNotificationHelper
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

    private val _state = MutableStateFlow(loadState())
    val state = _state.asStateFlow()

    private fun loadState(): NotificationSettingsState {
        val systemAllowed = NotificationPreferences.areSystemNotificationsAllowed(app)

        if (!systemAllowed) {
            NotificationPreferences.setGeneralNotificationsEnabled(app, false)
            SessionNotificationHelper.cancel(app)
        }

        return NotificationSettingsState(
            systemNotificationsAllowed = systemAllowed,
            generalNotificationsEnabled =
                systemAllowed && NotificationPreferences.areGeneralNotificationsEnabled(app),
            sessionNotificationsEnabled = NotificationPreferences.areSessionNotificationsEnabled(app),
            remindersEnabled = NotificationPreferences.areRemindersEnabled(app),
            forumNotificationsEnabled = NotificationPreferences.areForumNotificationsEnabled(app)
        )
    }

    fun refreshPermissionStatus() {
        _state.value = loadState()
    }

    fun setGeneralNotificationsEnabled(value: Boolean) {
        val systemAllowed = NotificationPreferences.areSystemNotificationsAllowed(app)

        if (!systemAllowed) {
            NotificationPreferences.setGeneralNotificationsEnabled(app, false)
            SessionNotificationHelper.cancel(app)
            BabyBotNotificationHelper.cancelAll(app)

            _state.update {
                it.copy(
                    systemNotificationsAllowed = false,
                    generalNotificationsEnabled = false
                )
            }

            return
        }

        NotificationPreferences.setGeneralNotificationsEnabled(app, value)

        _state.update {
            it.copy(
                systemNotificationsAllowed = true,
                generalNotificationsEnabled = value
            )
        }

        if (!value) {
            SessionNotificationHelper.cancel(app)
            BabyBotNotificationHelper.cancelAll(app)
        }
    }

    fun setSessionNotificationsEnabled(value: Boolean) {
        NotificationPreferences.setSessionNotificationsEnabled(app, value)

        _state.update {
            it.copy(sessionNotificationsEnabled = value)
        }

        if (!value) {
            SessionNotificationHelper.cancel(app)
        }
    }

    fun setRemindersEnabled(value: Boolean) {
        NotificationPreferences.setRemindersEnabled(app, value)

        _state.update {
            it.copy(remindersEnabled = value)
        }
    }

    fun setForumNotificationsEnabled(value: Boolean) {
        NotificationPreferences.setForumNotificationsEnabled(app, value)

        _state.update {
            it.copy(forumNotificationsEnabled = value)
        }
    }
}