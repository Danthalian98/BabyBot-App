package com.proyecto.babybot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bebes")
data class BabyEntity(
    @PrimaryKey
    val idBebe: String,
    val idUsuario: String,
    val nombre: String,
    val genero: String,
    val fechaNacimiento: Long,
    val peso: Double,
    val talla: Double,
    val tipoSangre: String,
    val pediatra: String,
    val notas: String,
    val alergias: List<String> = emptyList()
)