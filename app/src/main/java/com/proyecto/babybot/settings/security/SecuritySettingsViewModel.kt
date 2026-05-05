package com.proyecto.babybot.settings.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SecuritySettingsViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(
        SecuritySettingsState(
            email = authDataSource.getCurrentUserEmail().orEmpty()
        )
    )
    val state: StateFlow<SecuritySettingsState> = _state.asStateFlow()

    fun sendPasswordReset() {
        val email = _state.value.email

        if (email.isBlank()) {
            _state.value = _state.value.copy(
                errorMessage = "No se encontró un correo asociado a esta cuenta."
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                successMessage = null,
                errorMessage = null
            )

            val result = authDataSource.sendPasswordReset(email)

            _state.value = if (result.isSuccess) {
                _state.value.copy(
                    isLoading = false,
                    successMessage = "Te enviamos un correo para restablecer tu contraseña."
                )
            } else {
                _state.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No pudimos enviar el correo. Intenta de nuevo."
                )
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }
}