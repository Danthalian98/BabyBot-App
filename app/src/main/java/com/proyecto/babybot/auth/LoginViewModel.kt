package com.proyecto.babybot.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }
    fun onClearError() {
        _state.update { it.copy(error = null) }
    }

    fun clearMessage() {
        _state.update { it.copy(message = null) }
    }

    private fun translateError(errorMessage: String?): String {
        return when {
            errorMessage?.contains("configuration-not-found") == true -> "Error de configuración en el servidor."
            errorMessage?.contains("invalid-email") == true -> "El formato del correo electrónico no es válido."
            errorMessage?.contains("user-not-found") == true -> "No existe ninguna cuenta con este correo."
            errorMessage?.contains("wrong-password") == true -> "La contraseña es incorrecta."
            errorMessage?.contains("email-already-in-use") == true -> "Este correo ya está registrado."
            errorMessage?.contains("network-request-failed") == true -> "No hay conexión a internet."
            errorMessage?.contains("weak-password") == true -> "La contraseña es muy débil (mínimo 6 caracteres)."
            else -> "Ocurrió un error inesperado. Inténtalo de nuevo."
        }
    }

    fun onLoginClick() {
        if (_state.value.isLoading) return
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }

            val result = authDataSource.login(
                state.value.email,
                state.value.password
            )

            result.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true
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

    fun onForgotPasswordClick() {

        val email = state.value.email

        if (email.isBlank()) {
            _state.update {
                it.copy(error = "Ingresa tu correo primero")
            }
            return
        }

        viewModelScope.launch {

            val result = authDataSource.sendPasswordReset(email)

            result.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            message = "Te enviamos un correo para restablecer tu contraseña, si no lo encuentras en la bandeja de entrada revisa tu carpeta de spam."
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(error = e.message)
                    }
                }
            )
        }
    }

}
