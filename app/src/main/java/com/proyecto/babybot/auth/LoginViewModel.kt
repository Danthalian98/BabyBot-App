package com.proyecto.babybot.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.babybot.data.firebase.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ... (imports iguales)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    //private val auth = FirebaseAuth.getInstance()
    //private val db = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

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
                    val licenseInfo = authDataSource.getLicenseInfo()

                    val nextRoute = when {
                        licenseInfo.isPremium -> "home"
                        !licenseInfo.isTrialActive -> "subscriptions"
                        !licenseInfo.isTrialNoticeShown -> "trial"
                        else -> "home"
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            nextRoute = nextRoute
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isLoading = false, error = translateError(e.message))
                    }
                }
            )
        }
    }
    fun onEmailChange(email: String) { _state.update { it.copy(email = email) } }
    fun onPasswordChange(password: String) { _state.update { it.copy(password = password) } }
    fun onClearError() { _state.update { it.copy(error = null) } }
    fun clearMessage() { _state.update { it.copy(message = null) } }

    private fun translateError(errorMessage: String?): String {
        return when {
            errorMessage?.contains("configuration-not-found") == true -> "Error de configuración."
            errorMessage?.contains("invalid-email") == true -> "Correo no válido."
            errorMessage?.contains("user-not-found") == true -> "Cuenta no encontrada."
            errorMessage?.contains("wrong-password") == true -> "Contraseña incorrecta."
            errorMessage?.contains("network-request-failed") == true -> "Sin conexión a internet."
            else -> "Error al iniciar sesión. Inténtalo de nuevo."
        }
    }

    fun onForgotPasswordClick() {
        val email = state.value.email
        if (email.isBlank()) {
            _state.update { it.copy(error = "Ingresa tu correo primero") }
            return
        }
        viewModelScope.launch {
            val result = authDataSource.sendPasswordReset(email)
            result.fold(
                onSuccess = {
                    _state.update { it.copy(message = "Correo de recuperación enviado.") }
                },
                onFailure = { e ->
                    _state.update { it.copy(error = e.message) }
                }
            )
        }
    }
}