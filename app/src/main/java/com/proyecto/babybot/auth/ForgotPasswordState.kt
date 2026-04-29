package com.proyecto.babybot.auth

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)