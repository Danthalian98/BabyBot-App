package com.proyecto.babybot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesiones_activas")
data class ActiveSessionEntity(
    @PrimaryKey
    val sessionKey: String, // ejemplo: "<idBebe>:meal" o "<idBebe>:sleep"

    val idBebe: String,
    val sessionType: String, // "meal" o "sleep"
    val startMillis: Long,

    // meal
    val mealSide: String? = null,

    // sleep
    val sleepType: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)