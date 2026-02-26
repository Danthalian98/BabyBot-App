package com.proyecto.babybot.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//Logica real de la pantalla
@HiltViewModel //Hilt se encarga de inyectar la logica en el front
class SplashViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {

            val isLogged = authDataSource.isUserLogged()

            _state.value = SplashState(
                isLoading = false,
                isLoggedIn = isLogged
            )
        }
    }
}
 /*
 * Resumen
SplashScreen se compone
        ↓
Se crea SplashViewModel
        ↓
init { delay(100) } //cambiar por una funcion que realmente valide el logeo y despues actue
        ↓
Se actualiza SplashState
        ↓
Compose detecta cambio
        ↓
LaunchedEffect se ejecuta
        ↓
Se llama a onNavigateToLogin()
        ↓
RootNavGraph navega
*/