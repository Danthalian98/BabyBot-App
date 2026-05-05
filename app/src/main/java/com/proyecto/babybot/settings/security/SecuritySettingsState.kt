package com.proyecto.babybot.settings.security

data class SecuritySettingsState(
    val email: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)