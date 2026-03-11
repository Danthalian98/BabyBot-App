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

    private inline fun updateState(update: (RegisterState) -> RegisterState) {
        _state.update {
            val newState = update(it)
            newState.copy(
                isFormValid = validateForm(newState)
            )
        }
    }

    fun onNameChange(name: String) {

        updateState { state ->
            state.copy(
                name = name,
                nameError = validateName(name)
            )
        }
    }

    fun onEmailChange(email: String) {

        updateState { state ->
            state.copy(
                email = email,
                emailError = validateEmail(email)
            )
        }
    }

    fun onPasswordChange(password: String) {

        updateState { state ->
            state.copy(
                password = password,
                passwordError = validatePassword(password),
                confirmPasswordError =
                    validateConfirmPassword(password, state.confirmPassword)
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {

        updateState { state ->
            state.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError =
                    validateConfirmPassword(state.password, confirmPassword)
            )
        }
    }

    fun onAcceptTermsChange(accepted: Boolean) {

        updateState { state ->
            state.copy(
                acceptTerms = accepted,
                termsError =
                    if (!accepted) "Debes aceptar los términos y condiciones"
                    else null
            )
        }
    }

    private fun validateForm(state: RegisterState): Boolean {

        return state.nameError == null &&
                state.emailError == null &&
                state.passwordError == null &&
                state.confirmPasswordError == null &&
                state.name.isNotBlank() &&
                state.email.isNotBlank() &&
                state.password.isNotBlank() &&
                state.confirmPassword.isNotBlank() &&
                state.acceptTerms
    }

    private fun validateName(name: String): String? {
        return if (name.isBlank()) {
            "Debes ingresar un nombre de usuario"
        } else null
    }

    private fun validateEmail(email: String): String? {
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Correo electrónico no válido"
        } else null
    }

    private fun validatePassword(password: String): String? {

        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,12}$")

        return if (!regex.matches(password)) {
            "Debe tener 6-12 caracteres, mayúsculas, minúsculas y números"
        } else null
    }

    private fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): String? {

        return if (password != confirmPassword) {
            "Las contraseñas no coinciden"
        } else null
    }

    fun onRegisterClick() {

        viewModelScope.launch {

            val currentState = state.value

            if (!currentState.isFormValid) return@launch

            _state.update { it.copy(isLoading = true, error = null) }

            val result = authDataSource.register(
                currentState.name,
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
