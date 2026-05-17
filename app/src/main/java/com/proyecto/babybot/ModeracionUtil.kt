package com.proyecto.babybot

import java.util.Locale

object ModeracionUtil {
    private val blacklist = listOf(
        // Insultos comunes
        "estupido", "estupida", "idiota", "imbecil", "inutil",
        "mensote", "mensota", "tarado", "tarada", "bruta", "ignorante", "necio", "necia",
        "patetico", "patetica", "miserable", "mediocre", "fracasado", "fracasada",
        "perdedor", "perdedora", "asqueroso", "asquerosa", "repugnante", "despreciable",
        "lacra", "escoria",

        // Groserías fuertes / ofensivas frecuentes
        "pendejo", "pendeja", "pendejos", "pendejas", "pendejadas", "pendejez",
        "pndjo", "pndja", "pendejete", "pendejeta", "pendejito", "pendejita",
        "puto", "puta", "putos", "putas", "putito", "putita", "putiza", "putear",
        "zorra", "zorro", "malparido", "malparida",
        "culero", "culera", "culeros", "culeras", "culazo",
        "cabrón", "cabron", "cabrona", "cabrones", "cabronas", "cabronsito", "cabroncito",
        "chingada", "chingado", "chingados", "chingadas", "chingas",
        "chingona", "chingones", "chingonas", "chinga", "chingadera",
        "verga", "verguita", "vergazo", "vergazos",
        "mamón", "mamon", "mamona", "mamones", "mamonas", "mamadas", "mamador", "mamadora",
        "jodido", "jodida", "jodidos", "jodidas", "joder", "jodete",
        "pinche", "pinches", "mierdero", "mierdoso", "mierdosa",
        "maldito", "maldita", "malditos", "malditas", "desgraciado", "desgraciada",

        // Variantes mexicanas / latinoamericanas
        "culichi", "culon", "culona", "culón", "culóna", "naco", "naca", "nacos", "nacas",
        "huevon", "huevona", "huevón", "huevóna", "webon", "webona", "guevon", "guevona",
        "huevones", "huevonas", "webones", "webonas", "guevones", "guevonas",
        "mamerto", "mamerta", "pelmazo", "pelmaza", "cretino", "cretina",
        "bastardo", "bastarda", "bastardos", "bastardas", "infeliz",
        "patan", "patán", "rata", "rastrero", "rastrera", "sangano", "sángano",
        "marica", "maricon", "maricón", "maricona", "maricones", "mariconas",
        "joto", "jota", "jotos", "jotas", "puñetas", "punetas", "puñetero", "punetero",

        // Contenido sexual explícito / no apto para nombres o foros familiares
        // Nota: se retiraron palabras anatómicas o de lactancia como "senos", "chichis", "teta", "pene", etc.
        // porque pueden usarse en dudas reales sobre pecho, pañal, salud o cuidado del bebé.
        "porno", "pornografia", "pornografía", "pornografico", "pornográfico",
        "orgasmo", "eyacular", "eyaculacion", "eyaculación",
        "prostituta", "prostituto", "prostitucion", "prostitución", "ramera",

        // Lenguaje agresivo o de amenaza
        "matar", "matate", "mátate", "muerete", "muérete", "morir", "muerto", "muerta",
        "asesino", "asesina", "asesinar", "golpear", "golpearte", "golpeado", "golpeada",
        "odio", "odiar", "odioso", "odiosa", "amenaza", "amenazar",

        // Discriminación / ataques a personas
        "retrasado", "retrasada", "subnormal", "mongol", "mongola",
        "cerdo", "cerda", "cochino", "cochina",

        // Formas disfrazadas frecuentes con números o símbolos escritos como letras
        "p3ndejo", "p3ndeja", "pendej0", "pendej4", "idi0ta", "imb3cil", "estupid0", "estupid4",
        "put0", "put4", "culer0", "culer4", "cabr0n", "cabr0na", "m1erda", "mierd4",
        "ver9a", "v3rga", "ching4", "chingad4", "mam0n", "mam0na",

        // Palabras que ya tenías
        "cajeta", "baboso", "babosa", "culero", "culera"
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
