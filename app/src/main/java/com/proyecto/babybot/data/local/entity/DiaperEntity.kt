package com.proyecto.babybot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_panal")
data class DiaperEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val idBebe: String,
    val timestamp: Long,

    // "pipi", "popo", "ambos"
    val tipo: String,

    val color: String? = null,
    val consistencia: String? = null,
    val cantidad: String? = null, // "poca", "normal", "mucha"

    val notas: String? = null,
    val etiquetas: List<String> = emptyList()
)