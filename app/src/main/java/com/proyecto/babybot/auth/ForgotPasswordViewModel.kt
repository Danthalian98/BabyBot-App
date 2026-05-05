package com.proyecto.babybot.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state

    fun onEmailChange(email: String) {
        _state.update {
            it.copy(
                email = email,
                error = null,
                message = null
            )
        }
    }

    fun onSendResetClick() {
        val email = state.value.email.trim()

        if (email.isBlank()) {
            _state.update { it.copy(error = "Ingresa tu correo electrónico.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, message = null) }

            val result = authDataSource.sendPasswordReset(email)

            result.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            message = "Te enviamos un correo. Revisa tu bandeja de entrada o spam."
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = translateError(e.message)
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun translateError(errorMessage: String?): String {
        return when {
            errorMessage?.contains("invalid-email") == true -> "Correo no válido."
            errorMessage?.contains("user-not-found") == true -> "No existe una cuenta con este correo."
            errorMessage?.contains("network-request-failed") == true -> "Sin conexión a internet."
            else -> "No se pudo enviar el correo. Inténtalo de nuevo."
        }
    }
}