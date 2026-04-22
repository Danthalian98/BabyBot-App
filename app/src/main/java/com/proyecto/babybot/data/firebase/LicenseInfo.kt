package com.proyecto.babybot.data.firebase

data class LicenseInfo(
    val isLoggedIn: Boolean = false,
    val isTrialActive: Boolean = false,
    val isTrialNoticeShown: Boolean = false,
    val isPremium: Boolean = false
)