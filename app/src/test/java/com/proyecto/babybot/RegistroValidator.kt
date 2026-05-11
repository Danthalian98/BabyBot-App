package com.proyecto.babybot

object RegistroValidator {
    fun esCantidadValida(cantidad: Double): Boolean = cantidad > 0

    fun esDuracionSuenoValida(inicio: Long, fin: Long): Boolean = fin > inicio

    fun esTipoPanalValido(tipo: String): Boolean = tipo.isNotBlank()
}