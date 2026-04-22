package com.proyecto.babybot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_sueno")
data class SleepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val idBebe: String,

    val inicio: Long,
    val fin: Long,

    val duracionMinutos: Int? = null,

    // "siesta", "nocturno", "otro"
    val tipo: String = "siesta",

    // "durmio_tranquilo", "inquieto", "desperto_varias_veces", etc.
    val calidad: String? = null,

    // "cuna", "brazos", "carriola", "cama", etc.
    val lugar: String? = null,

    val notas: String? = null,
    val etiquetas: List<String> = emptyList()
)