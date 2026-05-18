package com.proyecto.babybot.home

import com.proyecto.babybot.data.local.model.ActivityRecord

data class HomeState(
    val isLoading: Boolean = true,
    val hasBaby: Boolean = false,
    val babyName: String = "",
    val babyAge: String = "",
    val nextActivityTitle: String = "",
    val nextActivityTime: String = "",
    val summary: List<SummaryData> = emptyList(),
    val recentActivities: List<ActivityData> = emptyList(),

    val showMealDialog: Boolean = false,
    val showDiaperDialog: Boolean = false,
    val showSleepDialog: Boolean = false,

    val activeMealStartMillis: Long? = null,
    val activeMealSide: String? = null,
    val activeSleepStartMillis: Long? = null,
    val activeSleepType: String? = null,
    val pendingMessage: String? = null,

    val selectedActivity: ActivityRecord? = null,
    val isEditingActivity: Boolean = false,
)

data class SummaryData(
    val title: String,
    val value: String
)

data class ActivityData(
    val icon: String,
    val title: String,
    val description: String,
    val time: String,
    val timestampMillis: Long,
    val record: ActivityRecord
)