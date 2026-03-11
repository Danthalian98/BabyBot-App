package com.proyecto.babybot.auth

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val acceptTerms: Boolean = false,

    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val termsError: String? = null,

    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val isFormValid: Boolean = false,
    val error: String? = null
)