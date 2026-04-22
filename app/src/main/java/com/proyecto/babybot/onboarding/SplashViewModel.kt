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

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state

    init {
        resolveDestination()
    }

    private fun resolveDestination() {
        viewModelScope.launch {
            delay(1800)

            val licenseInfo = authDataSource.getLicenseInfo()

            val destination = when {
                !licenseInfo.isLoggedIn -> SplashDestination.LOGIN
                licenseInfo.isPremium -> SplashDestination.HOME
                licenseInfo.isTrialActive && !licenseInfo.isTrialNoticeShown -> SplashDestination.TRIAL_INFO
                licenseInfo.isTrialActive && licenseInfo.isTrialNoticeShown -> SplashDestination.HOME
                else -> SplashDestination.SUBSCRIPTIONS
            }

            _state.value = SplashState(
                isLoading = false,
                destination = destination
            )
        }
    }
}