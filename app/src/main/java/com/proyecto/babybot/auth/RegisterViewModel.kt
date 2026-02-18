package com.proyecto.babybot.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

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
            _state.update { it.copy(isLoading = true) }

            // Simulación
            _state.update {
                it.copy(
                    isLoading = false,
                    isRegistered = true
                )
            }
        }
    }
}
