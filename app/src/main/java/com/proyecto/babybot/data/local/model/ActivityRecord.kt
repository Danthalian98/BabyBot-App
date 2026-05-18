package com.proyecto.babybot.data.local.model

import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity

sealed class ActivityRecord {

    abstract val localId: Long
    abstract val timestampMillis: Long

    data class Meal(
        val meal: MealEntity
    ) : ActivityRecord() {
        override val localId: Long = meal.id
        override val timestampMillis: Long = meal.timestamp
    }

    data class Diaper(
        val diaper: DiaperEntity
    ) : ActivityRecord() {
        override val localId: Long = diaper.id
        override val timestampMillis: Long = diaper.timestamp
    }

    data class Sleep(
        val sleep: SleepEntity
    ) : ActivityRecord() {
        override val localId: Long = sleep.id
        override val timestampMillis: Long = sleep.inicio
    }
}