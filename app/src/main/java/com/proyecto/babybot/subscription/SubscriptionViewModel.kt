package com.proyecto.babybot.subscription

import android.app.Activity
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
class SubscriptionViewModel @Inject constructor(
    private val billingManager: BillingManager,
    private val authDataSource: AuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionState())
    val state: StateFlow<SubscriptionState> = _state.asStateFlow()

    init {
        observeBillingEvents()
        billingManager.startConnection()
    }

    private fun observeBillingEvents() {
        viewModelScope.launch {
            billingManager.events.collect { event ->
                when (event) {
                    is BillingEvent.Ready -> {
                        val googlePlans = event.plans.map { plan ->
                            SubscriptionPlan(
                                id = plan.basePlanId ?: plan.productId,
                                title = when (plan.basePlanId) {
                                    "monthly" -> "Plan mensual"
                                    "quarterly" -> "Plan trimestral"
                                    "yearly" -> "Plan anual"
                                    else -> plan.title
                                },
                                price = plan.price,
                                subtitle = billingPeriodToText(plan.billingPeriod),
                                badge = when (plan.basePlanId) {
                                    "yearly" -> "MEJOR VALOR"
                                    else -> null
                                }
                            )
                        }

                        _state.value = _state.value.copy(
                            isLoading = false,
                            plans = googlePlans.ifEmpty { _state.value.plans },
                            selectedPlanId = googlePlans.firstOrNull()?.id ?: _state.value.selectedPlanId,
                            plansFromGooglePlay = googlePlans.isNotEmpty(),
                            error = if (googlePlans.isEmpty()) {
                                "Aún no hay planes activos en Google Play. Puedes subir esta versión y crearlos después en Play Console."
                            } else {
                                null
                            }
                        )
                    }

                    is BillingEvent.PurchaseSuccess -> {
                        handlePurchaseSuccess(event)
                    }

                    is BillingEvent.PurchasePending -> {
                        _state.value = _state.value.copy(
                            isPurchaseLoading = false,
                            error = "Tu pago está pendiente. Activaremos la licencia cuando Google Play confirme el cobro."
                        )
                    }

                    is BillingEvent.UserCanceled -> {
                        _state.value = _state.value.copy(
                            isPurchaseLoading = false,
                            error = "Compra cancelada."
                        )
                    }

                    is BillingEvent.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isPurchaseLoading = false,
                            error = event.message
                        )
                    }
                }
            }
        }
    }

    fun selectPlan(planId: String) {
        _state.value = _state.value.copy(
            selectedPlanId = planId,
            error = null
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

        if (total == 0) return

        _state.value = _state.value.copy(
            currentFeatureIndex = (current + 1) % total
        )
    }

    fun previousFeature() {
        val current = _state.value.currentFeatureIndex
        val total = _state.value.features.size

        if (total == 0) return

        _state.value = _state.value.copy(
            currentFeatureIndex = if (current == 0) total - 1 else current - 1
        )
    }

    fun onContinueClick(activity: Activity) {
        if (!_state.value.plansFromGooglePlay) {
            _state.value = _state.value.copy(
                error = "Primero crea y activa la suscripción babybot_premium en Play Console."
            )
            return
        }

        _state.value = _state.value.copy(
            isPurchaseLoading = true,
            error = null
        )

        billingManager.launchPurchase(
            activity = activity,
            selectedPlanId = _state.value.selectedPlanId
        )
    }

    private fun markPremiumAsActive(
        productId: String,
        purchaseToken: String
    ) {
        viewModelScope.launch {
            try {
                /*
                 * Aquí conectamos con Firestore.
                 * Si tu AuthDataSource todavía no tiene esta función,
                 * abajo te dejo una versión simple para agregarla.
                 */
                authDataSource.activateGooglePlayLicense(
                    productId = productId,
                    purchaseToken = purchaseToken
                )

                _state.value = _state.value.copy(
                    isPurchaseLoading = false,
                    error = null,
                    purchaseCompleted = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isPurchaseLoading = false,
                    error = e.message ?: "La compra se completó, pero no se pudo actualizar la licencia."
                )
            }
        }
    }

    private fun billingPeriodToText(period: String?): String {
        return when (period) {
            "P1M" -> "Renovación mensual"
            "P3M" -> "Renovación trimestral"
            "P1Y" -> "Renovación anual"
            else -> "Pago seguro con Google Play"
        }
    }

    private fun handlePurchaseSuccess(event: BillingEvent.PurchaseSuccess) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val purchase = event.purchase

                authDataSource.activateGooglePlayLicense(
                    productId = purchase.products.firstOrNull().orEmpty(),
                    purchaseToken = purchase.purchaseToken
                )

                val acknowledged = event.acknowledge()

                if (!acknowledged) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Tu licencia se activó, pero falta confirmar la compra con Google Play. Cierra y abre la app para reintentarlo."
                    )
                    return@launch
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    purchaseCompleted = true,
                    error = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "No se pudo activar la suscripción. Intenta de nuevo."
                )
            }
        }
    }
}