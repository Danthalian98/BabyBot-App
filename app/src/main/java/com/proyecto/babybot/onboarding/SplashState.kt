package com.proyecto.babybot.onboarding

enum class SplashDestination {
    NONE,
    LOGIN,
    TRIAL_INFO,
    HOME,
    SUBSCRIPTIONS
}

data class SplashState(
    val isLoading: Boolean = true,
    val destination: SplashDestination = SplashDestination.NONE
)