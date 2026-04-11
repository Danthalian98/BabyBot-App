package com.proyecto.babybot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_comida")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val idBebe: String,

    // Momento principal del registro
    val timestamp: Long,

    // Tipo principal
    // "lactancia", "biberon", "complementaria", "otro"
    val tipo: String,

    // Subtipo o detalle
    // Ej: "pecho", "formula", "leche_extraida", "papilla", "pure", "fruta"
    val subtipo: String? = null,

    // Para registros por cantidad
    val cantidad: Double? = null,
    val unidad: String? = null, // "ml", "oz", "g", etc.

    // Para lactancia o sesiones
    val inicio: Long? = null,
    val fin: Long? = null,
    val duracionMinutos: Int? = null,

    // Lactancia
    val lado: String? = null, // "izquierdo", "derecho", "ambos"
    val huboComplemento: Boolean = false,
    val tipoComplemento: String? = null,
    val cantidadComplemento: Double? = null,
    val unidadComplemento: String? = null,

    // Alimentación complementaria / aceptación
    val alimentoDescripcion: String? = null,
    val reaccion: String? = null, // "le_gusto", "comio_poco", "rechazo", "sin_problema"

    // Registro general
    val terminoTodo: Boolean? = null,
    val notas: String? = null,
    val etiquetas: List<String> = emptyList()
)