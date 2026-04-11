package com.proyecto.babybot.subscription

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionState())
    val state: StateFlow<SubscriptionState> = _state.asStateFlow()

    fun selectPlan(planId: String) {
        _state.value = _state.value.copy(
            selectedPlanId = planId
        )
    }

    fun selectFeature(index: Int) {
        val features = _state.value.features
        if (index in features.indices) {
            _state.value = _state.value.copy(
                currentFeatureIndex = index
            )
        }
    }

    fun nextFeature() {
        val current = _state.value.currentFeatureIndex
        val total = _state.value.features.size

        _state.value = _state.value.copy(
            currentFeatureIndex = (current + 1) % total
        )
    }

    fun previousFeature() {
        val current = _state.value.currentFeatureIndex
        val total = _state.value.features.size

        _state.value = _state.value.copy(
            currentFeatureIndex = if (current == 0) total - 1 else current - 1
        )
    }

    fun onContinueClick(
        onOpenBillingPlaceholder: (String) -> Unit
    ) {
        onOpenBillingPlaceholder(_state.value.selectedPlanId)
    }
}