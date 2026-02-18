package com.proyecto.babybot.subscription

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrialInfoViewModel : ViewModel() {

    private val _state = MutableStateFlow(TrialInfoState())
    val state: StateFlow<TrialInfoState> = _state.asStateFlow()

    fun onContinueClicked(onNavigate: (Boolean) -> Unit) {
        val isTrialActive = _state.value.isTrialActive

        // true = puede entrar a la app
        // false = mandar a suscripción
        onNavigate(isTrialActive)
    }
}
