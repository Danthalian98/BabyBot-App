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
class RegisterViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onAcceptTermsChange(accepted: Boolean) {
        _state.update { it.copy(acceptTerms = accepted) }
    }

    fun onRegisterClick() {
        viewModelScope.launch {

            val currentState = state.value

            // Validaciones básicas
            if (currentState.password != currentState.confirmPassword) {
                _state.update {
                    it.copy(error = "Las contraseñas no coinciden")
                }
                return@launch
            }

            if (!currentState.acceptTerms) {
                _state.update {
                    it.copy(error = "Debes aceptar los términos")
                }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            val result = authDataSource.register(
                currentState.email,
                currentState.password
            )

            result.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
            )
        }
    }
}
