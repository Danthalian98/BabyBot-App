package com.proyecto.babybot.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrialInfoViewModel @Inject constructor(
    private val authDataSource: AuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(TrialInfoState())
    val state: StateFlow<TrialInfoState> = _state.asStateFlow()

    init {
        checkTrial()
    }

    private fun checkTrial() {
        viewModelScope.launch {
            try {
                val licenseInfo = authDataSource.getLicenseInfo()

                _state.value = TrialInfoState(
                    isLoading = false,
                    isTrialActive = licenseInfo.isTrialActive,
                    isPremium = licenseInfo.isPremium
                )
            } catch (e: Exception) {
                _state.value = TrialInfoState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onTrialContinueClicked(onNavigateHome: () -> Unit) {
        viewModelScope.launch {
            authDataSource.markTrialNoticeAsShown()
            onNavigateHome()
        }
    }

    fun onExpiredContinueClicked(onNavigateSubscriptions: () -> Unit) {
        onNavigateSubscriptions()
    }
}