package com.proyecto.babybot.settings.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import com.proyecto.babybot.data.firebase.BabyDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val babyDataSource: BabyDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(AccountSettingsState())
    val state = _state.asStateFlow()

    init {
        loadAccount()
    }

    fun loadAccount() {
        val userId = authDataSource.getCurrentUserId()

        if (userId.isNullOrBlank()) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "No se encontró una sesión activa."
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val userResult = authDataSource.getCurrentUserProfile()
            val baby = babyDataSource.getBabyByUserId(userId)

            userResult.onSuccess { data ->
                _state.update {
                    it.copy(
                        name = data["nombre"] as? String
                            ?: authDataSource.getCurrentUserName().orEmpty(),
                        email = data["correo"] as? String
                            ?: authDataSource.getCurrentUserEmail().orEmpty(),
                        accountStatus = data["estadoCuenta"] as? String ?: "TRIAL"
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        name = authDataSource.getCurrentUserName().orEmpty(),
                        email = authDataSource.getCurrentUserEmail().orEmpty(),
                        errorMessage = error.message ?: "No pudimos cargar tu información."
                    )
                }
            }

            if (baby != null) {
                val birthMillis = baby.fechaNacimiento?.toDate()?.time ?: 0L

                _state.update {
                    it.copy(
                        isLoading = false,
                        babiesCount = 1,
                        babyName = baby.nombre,
                        babyAge = calculateAge(birthMillis),
                        babyGender = formatGender(baby.genero),
                        babyWeight = if (baby.peso > 0.0) "${baby.peso} kg" else "No registrado",
                        babyHeight = if (baby.talla > 0.0) "${baby.talla} cm" else "No registrado"
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        babiesCount = 0,
                        babyName = "",
                        babyAge = "",
                        babyGender = "",
                        babyWeight = "",
                        babyHeight = ""
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun formatGender(gender: String): String {
        return when (gender.uppercase()) {
            "M" -> "Masculino"
            "F" -> "Femenino"
            else -> gender.ifBlank { "No registrado" }
        }
    }

    private fun calculateAge(birthMillis: Long): String {
        if (birthMillis <= 0L) return "No disponible"

        val birth = Calendar.getInstance().apply { timeInMillis = birthMillis }
        val now = Calendar.getInstance()

        var years = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        var months = now.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
        val days = now.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH)

        if (days < 0) months--
        if (months < 0) {
            years--
            months += 12
        }

        return when {
            years > 0 && months > 0 -> "$years año(s), $months mes(es)"
            years > 0 -> "$years año(s)"
            months > 0 -> "$months mes(es)"
            else -> "Menos de 1 mes"
        }
    }
}