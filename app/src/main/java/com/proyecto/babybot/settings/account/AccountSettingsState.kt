package com.proyecto.babybot.settings.account

data class AccountSettingsState(
    val isLoading: Boolean = false,

    val name: String = "",
    val email: String = "",
    val accountStatus: String = "",

    val babiesCount: Int = 0,
    val babyName: String = "",
    val babyAge: String = "",
    val babyGender: String = "",
    val babyWeight: String = "",
    val babyHeight: String = "",

    val errorMessage: String? = null
)