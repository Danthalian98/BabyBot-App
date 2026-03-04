package com.proyecto.babybot.onboarding

data class SplashState( //Variables por si esta cargando y por si esta logeado
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false
)