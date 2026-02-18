package com.proyecto.babybot.subscription

data class TrialInfoState(
    val isLoading: Boolean = false,
    val isTrialActive: Boolean = true,
    val error: String? = null
)
