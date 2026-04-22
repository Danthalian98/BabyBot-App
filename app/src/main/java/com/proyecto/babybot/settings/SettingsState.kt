package com.proyecto.babybot.settings

data class SettingsState(
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val userName: String = "",
    val userEmail: String = ""
)