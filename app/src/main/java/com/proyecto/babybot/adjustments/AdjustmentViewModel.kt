package com.proyecto.babybot.adjustments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdjustmentViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(AdjustmentState())
    val state = _state.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val userId = authDataSource.getCurrentUserId() ?: return
        
        _state.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            val user = authDataSource.getUserData(userId)
            if (user != null) {
                _state.update {
                    it.copy(
                        userName = user.nombre,
                        userEmail = user.correo,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Error al cargar datos del usuario") }
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _state.update { it.copy(isNotificationsEnabled = enabled) }
        // Aquí se podría implementar la lógica para actualizar en Firestore o DataStore
    }

    fun toggleDarkMode(enabled: Boolean) {
        _state.update { it.copy(isDarkModeEnabled = enabled) }
        // Aquí se podría implementar la lógica para actualizar el tema de la app
    }

    fun logout() {
        authDataSource.logout()
    }
}