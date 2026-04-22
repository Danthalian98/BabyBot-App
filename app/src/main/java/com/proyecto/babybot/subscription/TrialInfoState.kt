package com.proyecto.babybot.subscription

data class TrialInfoState(
    val isLoading: Boolean = true,
    val isTrialActive: Boolean = true,
    val error: String? = null
)