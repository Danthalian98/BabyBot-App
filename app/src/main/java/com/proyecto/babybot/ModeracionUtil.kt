package com.proyecto.babybot

import java.util.Locale

object ModeracionUtil {
    private val blacklist = listOf(
        "estupido", "estupida", "idiota", "imbecil", "pendejo", "pendeja",
        "maldito", "maldita", "basura", "puto", "puta", "mierda", "cajeta",
        "pendejadas", "pendejez", "baboso", "babosa", "culero", "culera"
    )

    fun esContenidoSeguro(texto: String): Boolean {
        if (texto.isBlank()) return true

        // 1. Normalizar: minúsculas y quitar acentos (para que coincida con la blacklist sin acentos)
        val textoNormalizado = texto.lowercase(Locale.getDefault())
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")

        // 2. Extraer palabras: Separamos por cualquier cosa que no sea letra o número (puntuación, espacios, etc.)
        // Esto convierte "imbécil?" en la palabra limpia "imbecil"
        val palabrasEnMensaje = textoNormalizado.split(Regex("[^\\p{L}\\p{N}]+"))

        // 3. Verificar si alguna palabra limpia está en la lista negra
        return palabrasEnMensaje.none { palabra ->
            blacklist.contains(palabra)
        }
    }
}