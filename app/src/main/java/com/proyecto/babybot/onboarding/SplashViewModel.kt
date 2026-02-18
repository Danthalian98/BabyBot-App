package com.proyecto.babybot.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//Logica real de la pantalla
@HiltViewModel //Hilt se encarga de inyectar la logica en el front
class SplashViewModel @Inject constructor() : ViewModel() {

    //Se crea una variable para poder trabajar y una que no puede modificarse para mandarse al front
    //El front puede observarlo, pero no modificarlo.
    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state

    init {
        viewModelScope.launch { //Se crea la pantalla
            delay(100) //Espera un tiempo definido
            _state.value = SplashState(isLoading = false) //Cambia el estado y navega al login
        }
    }
    //El resto de funciones aqui, consulta si esta logeado o no, consulta a firebase, etc
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