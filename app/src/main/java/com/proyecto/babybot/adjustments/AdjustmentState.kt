package com.proyecto.babybot.adjustments

data class AdjustmentState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val isNotificationsEnabled: Boolean = true,
    val isDarkModeEnabled: Boolean = false,
    val error: String? = null
)