package com.proyecto.babybot.settings.account.edit

import com.proyecto.babybot.data.local.entity.BabyEntity

data class EditAccountInfoState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false,

    val userName: String = "",
    val userEmail: String = "",

    val licenseType: String = "",
    val licenseStatus: String = "",
    val licenseExpirationDateMillis: Long = 0L,

    val baby: BabyEntity? = null
)