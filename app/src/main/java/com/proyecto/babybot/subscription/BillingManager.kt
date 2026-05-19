package com.proyecto.babybot.subscription

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync

const val SUBSCRIPTION_PRODUCT_ID = "babybot_premium"

data class BillingPlan(
    val productId: String,
    val basePlanId: String?,
    val offerToken: String,
    val title: String,
    val price: String,
    val billingPeriod: String?,
    val productDetails: ProductDetails
)

sealed class BillingEvent {
    data class Ready(val plans: List<BillingPlan>) : BillingEvent()
    data class PurchaseSuccess(
        val purchase: Purchase,
        val acknowledge: suspend () -> Boolean
    ) : BillingEvent()
    data class PurchasePending(val purchase: Purchase) : BillingEvent()
    data class Error(val message: String) : BillingEvent()
    object UserCanceled : BillingEvent()
}

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext context: Context
) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _events = MutableSharedFlow<BillingEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<BillingEvent> = _events

    private var cachedPlans: List<BillingPlan> = emptyList()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    fun startConnection() {
        if (billingClient.isReady) {
            scope.launch {
                val plans = querySubscriptionPlans()
                cachedPlans = plans
                _events.emit(BillingEvent.Ready(plans))
            }
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                _events.tryEmit(BillingEvent.Error("Se perdió la conexión con Google Play. Intenta de nuevo."))
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    scope.launch {
                        val plans = querySubscriptionPlans()
                        cachedPlans = plans
                        _events.emit(BillingEvent.Ready(plans))
                        checkActiveSubscriptions()
                    }
                } else {
                    _events.tryEmit(
                        BillingEvent.Error(
                            billingResult.debugMessage.ifBlank {
                                "No se pudo conectar con Google Play Billing."
                            }
                        )
                    )
                }
            }
        })
    }

    suspend fun querySubscriptionPlans(): List<BillingPlan> {
        if (!billingClient.isReady) return emptyList()

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUBSCRIPTION_PRODUCT_ID)
                .setProductType(ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val result = billingClient.queryProductDetails(params)

        if (result.billingResult.responseCode != BillingResponseCode.OK) {
            return emptyList()
        }

        return result.productDetailsList.orEmpty().flatMap { productDetails ->
            productDetails.subscriptionOfferDetails.orEmpty().mapNotNull { offer ->
                val pricingPhase = offer.pricingPhases.pricingPhaseList.firstOrNull()
                    ?: return@mapNotNull null

                BillingPlan(
                    productId = productDetails.productId,
                    basePlanId = offer.basePlanId,
                    offerToken = offer.offerToken,
                    title = productDetails.name,
                    price = pricingPhase.formattedPrice,
                    billingPeriod = pricingPhase.billingPeriod,
                    productDetails = productDetails
                )
            }
        }
    }

    fun launchPurchase(activity: Activity, selectedPlanId: String) {
        val selectedPlan = cachedPlans.firstOrNull { plan ->
            plan.basePlanId == selectedPlanId || plan.productId == selectedPlanId
        }

        if (selectedPlan == null) {
            _events.tryEmit(
                BillingEvent.Error(
                    "Todavía no se encontró este plan en Google Play. Revisa que la suscripción esté activa en Play Console."
                )
            )
            return
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(selectedPlan.productDetails)
            .setOfferToken(selectedPlan.offerToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)

        if (result.responseCode != BillingResponseCode.OK) {
            _events.tryEmit(
                BillingEvent.Error(
                    result.debugMessage.ifBlank {
                        "No se pudo abrir el flujo de compra."
                    }
                )
            )
        }
    }

    private suspend fun checkActiveSubscriptions() {
        if (!billingClient.isReady) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.SUBS)
            .build()

        val result = billingClient.queryPurchasesAsync(params)

        result.purchasesList
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            .forEach { purchase ->
                handlePurchase(purchase)
            }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                purchases.orEmpty().forEach { purchase ->
                    scope.launch { handlePurchase(purchase) }
                }
            }

            BillingResponseCode.USER_CANCELED -> {
                _events.tryEmit(BillingEvent.UserCanceled)
            }

            else -> {
                _events.tryEmit(
                    BillingEvent.Error(
                        billingResult.debugMessage.ifBlank {
                            "No se pudo completar la compra."
                        }
                    )
                )
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                _events.emit(
                    BillingEvent.PurchaseSuccess(
                        purchase = purchase,
                        acknowledge = {
                            acknowledgeIfNeeded(purchase)
                        }
                    )
                )
            }

            Purchase.PurchaseState.PENDING -> {
                _events.emit(BillingEvent.PurchasePending(purchase))
            }

            else -> Unit
        }
    }

    private suspend fun acknowledgeIfNeeded(purchase: Purchase): Boolean {
        if (purchase.isAcknowledged) return true

        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.acknowledgePurchase(params) { billingResult ->
                val success = billingResult.responseCode == BillingResponseCode.OK

                if (continuation.isActive) {
                    continuation.resume(success)
                }
            }
        }
    }
}