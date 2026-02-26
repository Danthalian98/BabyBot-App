package com.proyecto.babybot.home

data class HomeState(
    val babyName: String = "",
    val babyAge: String = "",
    val nextActivityTitle: String = "",
    val nextActivityTime: String = "",
    val summary: List<SummaryData> = emptyList(),
    val recentActivities: List<ActivityData> = emptyList()
)

data class SummaryData(
    val title: String,
    val value: String
)

data class ActivityData(
    val title: String,
    val description: String,
    val time: String
)