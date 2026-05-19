package com.proyecto.babybot.subscription

data class TrialInfoState(
    val isLoading: Boolean = true,
    val isTrialActive: Boolean = true,
    val isPremium: Boolean = false,
    val error: String? = null
)