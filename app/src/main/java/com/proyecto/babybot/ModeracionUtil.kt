package com.proyecto.babybot

object ModeracionUtil {
    // Lista de palabras para evitar ambiente tóxico
    private val blacklist = listOf(
        "estúpido", "estúpida", "idiota", "imbécil", "pendejo", "pendeja",
        "maldito", "maldita", "basura", "puto", "puta", "mierda", "cajeta",
        "pendejadas", "pendejez", "baboso", "babosa", "culero", "culera"
        // Puedes agregar más variantes locales aquí
    )

    fun esContenidoSeguro(texto: String): Boolean {
        // Normalizamos el texto: quitamos acentos y pasamos a minúsculas
        val textoNormalizado = texto.lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")

        // Verificamos si alguna palabra de la lista está en el texto
        return blacklist.none { palabra ->
            textoNormalizado.contains(palabra)
        }
    }
}