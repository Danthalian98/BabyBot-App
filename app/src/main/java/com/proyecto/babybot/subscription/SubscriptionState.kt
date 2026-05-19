package com.proyecto.babybot.subscription

import com.proyecto.babybot.R

data class SubscriptionFeature(
    val titleRes: Int,
    val descriptionRes: Int,
    val imageRes: Int
)

data class SubscriptionPlan(
    val id: String,
    val title: String,
    val price: String,
    val subtitle: String,
    val badge: String? = null
)

data class SubscriptionState(
    val currentFeatureIndex: Int = 0,
    val selectedPlanId: String = "monthly",
    val isLoading: Boolean = true,
    val isPurchaseLoading: Boolean = false,
    val purchaseCompleted: Boolean = false,
    val error: String? = null,
    val plansFromGooglePlay: Boolean = false,
    val features: List<SubscriptionFeature> = listOf(
        SubscriptionFeature(
            titleRes = R.string.sub_title_1,
            descriptionRes = R.string.sub_desc_1,
            imageRes = R.drawable.img_subs_1
        ),
        SubscriptionFeature(
            titleRes = R.string.sub_title_2,
            descriptionRes = R.string.sub_desc_2,
            imageRes = R.drawable.img_subs_2
        ),
        SubscriptionFeature(
            titleRes = R.string.sub_title_3,
            descriptionRes = R.string.sub_desc_3,
            imageRes = R.drawable.img_subs_3
        ),
        SubscriptionFeature(
            titleRes = R.string.sub_title_4,
            descriptionRes = R.string.sub_desc_4,
            imageRes = R.drawable.img_subs_4
        ),
        SubscriptionFeature(
            titleRes = R.string.sub_title_5,
            descriptionRes = R.string.sub_desc_5,
            imageRes = R.drawable.img_subs_5
        ),
        SubscriptionFeature(
            titleRes = R.string.sub_title_6,
            descriptionRes = R.string.sub_desc_6,
            imageRes = R.drawable.img_subs_6
        ),
        SubscriptionFeature(
            titleRes = R.string.sub_title_7,
            descriptionRes = R.string.sub_desc_7,
            imageRes = R.drawable.img_subs_7
        )
    ),
    val plans: List<SubscriptionPlan> = listOf(
        SubscriptionPlan(
            id = "monthly",
            title = "Plan mensual",
            price = "Próximamente",
            subtitle = "Pago seguro con Google Play"
        )
    )
)