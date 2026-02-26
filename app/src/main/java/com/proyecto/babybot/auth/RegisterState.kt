package com.proyecto.babybot.auth

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val acceptTerms: Boolean = false,
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null
)