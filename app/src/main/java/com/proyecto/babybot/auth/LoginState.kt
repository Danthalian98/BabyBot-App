package com.proyecto.babybot.auth

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val nextRoute: String? = null,
    val error: String? = null,
    val message: String? = null
)
