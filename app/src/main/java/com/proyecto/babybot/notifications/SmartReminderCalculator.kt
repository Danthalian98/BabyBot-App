package com.proyecto.babybot.notifications

import com.proyecto.babybot.data.local.entity.BabyEntity
import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import java.util.Calendar
import kotlin.math.max

data class SmartReminderPlan(
    val delayMinutes: Long,
    val title: String,
    val message: String,
    val tag: String,
    val destination: String = "home",
    val delaySeconds: Long? = null
)

object SmartReminderCalculator {

    fun calculateAgeMonths(birthDateMillis: Long): Int {
        val birth = Calendar.getInstance().apply {
            timeInMillis = birthDateMillis
        }

        val now = Calendar.getInstance()

        var months = (now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)) * 12
        months += now.get(Calendar.MONTH) - birth.get(Calendar.MONTH)

        if (now.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) {
            months--
        }

        return max(0, months)
    }

    fun mealReminder(
        baby: BabyEntity,
        meal: MealEntity
    ): SmartReminderPlan {
        val ageMonths = calculateAgeMonths(baby.fechaNacimiento)

        val delay = when (meal.tipo.lowercase()) {
            "lactancia" -> calculateBreastfeedingDelay(ageMonths, meal)
            "biberon" -> calculateBottleDelay(ageMonths, meal)
            "complementaria" -> calculateSolidFoodDelay(ageMonths, meal)
            else -> defaultMealDelay(ageMonths)
        }.coerceIn(60, 300)

        val message = when {
            meal.tipo.equals("lactancia", ignoreCase = true) &&
                    (meal.duracionMinutos ?: 0) < 10 ->
                "La toma fue corta. Observa señales de hambre antes de ofrecer otra toma."

            meal.tipo.equals("biberon", ignoreCase = true) &&
                    (meal.cantidad ?: 0.0) > 0.0 ->
                "Han pasado alrededor de ${delay / 60} h desde el biberón. Revisa si muestra señales de hambre."

            meal.tipo.equals("complementaria", ignoreCase = true) &&
                    ageMonths < 12 ->
                "Revisa si necesita leche o comida según sus señales de hambre."

            else ->
                "Puede ser buen momento para observar si tu bebé muestra señales de hambre."
        }

        return SmartReminderPlan(
            delayMinutes = delay.toLong(),
            title = "Próxima comida 🍼",
            message = message,
            tag = "meal_reminder"
        )
    }

    private fun calculateBreastfeedingDelay(ageMonths: Int, meal: MealEntity): Int {
        val duration = meal.duracionMinutos ?: 0
        val complementAmount = meal.cantidadComplemento ?: 0.0

        var delay = when {
            ageMonths < 1 -> 120
            ageMonths < 6 -> 150
            ageMonths < 12 -> 180
            else -> 210
        }

        if (duration in 1..9) delay -= 45
        if (duration >= 20) delay += 30

        if (meal.huboComplemento && complementAmount >= 60.0) {
            delay += 30
        }

        return delay
    }

    private fun calculateBottleDelay(ageMonths: Int, meal: MealEntity): Int {
        val amountMl = toMl(meal.cantidad, meal.unidad)

        var delay = when {
            ageMonths < 1 -> 150
            ageMonths < 6 -> 180
            ageMonths < 12 -> 210
            else -> 240
        }

        if (amountMl != null) {
            when {
                amountMl < 60 -> delay -= 60
                amountMl < 120 -> delay -= 30
                amountMl >= 180 -> delay += 30
            }
        }

        return delay
    }

    private fun calculateSolidFoodDelay(ageMonths: Int, meal: MealEntity): Int {
        var delay = when {
            ageMonths < 6 -> 120
            ageMonths < 12 -> 180
            else -> 240
        }

        val ateLittle =
            meal.reaccion == "comio_poco" ||
                    meal.reaccion == "rechazo" ||
                    meal.terminoTodo == false

        if (ateLittle) delay -= 60

        return delay
    }

    private fun defaultMealDelay(ageMonths: Int): Int {
        return when {
            ageMonths < 1 -> 120
            ageMonths < 6 -> 180
            ageMonths < 12 -> 210
            else -> 240
        }
    }

    fun diaperReminder(
        baby: BabyEntity,
        diaper: DiaperEntity
    ): SmartReminderPlan {
        val ageMonths = calculateAgeMonths(baby.fechaNacimiento)

        var delay = when {
            ageMonths < 3 -> 120
            ageMonths < 12 -> 180
            else -> 210
        }

        val tipo = diaper.tipo.lowercase()

        if (tipo == "popo" || tipo == "ambos") {
            delay = 120
        }

        when (diaper.cantidad?.lowercase()) {
            "mucha" -> delay -= 30
            "poca" -> delay += 30
        }

        delay = delay.coerceIn(90, 240)

        val message = if (tipo == "popo" || tipo == "ambos") {
            "Revisa si necesita otro cambio y observa que la piel esté limpia y seca."
        } else {
            "Puede ser buen momento para revisar si el pañal sigue seco."
        }

        return SmartReminderPlan(
            delayMinutes = delay.toLong(),
            title = "Revisión de pañal 🧷",
            message = message,
            tag = "diaper_reminder"
        )
    }

    fun sleepReminder(
        baby: BabyEntity,
        sleep: SleepEntity
    ): SmartReminderPlan {
        val ageMonths = calculateAgeMonths(baby.fechaNacimiento)
        val duration = sleep.duracionMinutos ?: 0

        val baseWakeWindowMinutes = when {
            ageMonths < 1 -> 50      // recién nacido: revisar más pronto
            ageMonths < 2 -> 60
            ageMonths < 4 -> 75
            ageMonths < 6 -> 120
            ageMonths < 9 -> 150
            ageMonths < 12 -> 180
            ageMonths < 15 -> 210    // nuevo caso: transición hacia menos siestas
            ageMonths < 18 -> 240
            ageMonths < 24 -> 270    // nuevo caso: bebé más grande
            else -> 300
        }

        val adjustmentMinutes = when {
            duration in 1..19 -> -baseWakeWindowMinutes
            duration in 20..39 -> -30
            duration in 40..59 -> 0
            duration in 60..89 -> 30
            duration in 90..119 -> 45
            duration >= 120 -> 60
            else -> 0
        }

        val calculatedDelay = (baseWakeWindowMinutes + adjustmentMinutes)
            .coerceIn(30, 360)

        val message = when {
            duration in 1..19 ->
                "La siesta fue muy corta. Es probable que no haya descansado suficiente; observa si sigue adormilado, irritable o necesita volver a dormir."

            duration in 20..39 ->
                "Fue un descanso corto. Puede necesitar otra siesta antes de lo habitual si notas bostezos, irritabilidad o mirada cansada."

            duration in 40..59 ->
                "Tuvo una siesta breve pero útil. Revisa más adelante si vuelve a mostrar señales de sueño."

            duration in 60..89 ->
                "Tuvo un buen descanso. Más adelante puedes observar si empieza a mostrar señales de cansancio."

            duration in 90..119 ->
                "Durmió bastante bien. Puedes esperar un poco más antes de revisar señales de sueño."

            duration >= 120 ->
                "Fue un descanso largo. Observa su energía y señales naturales antes de intentar otra siesta."

            ageMonths < 4 ->
                "Observa señales de sueño: bostezos, mirada perdida, inquietud o irritabilidad."

            else ->
                "Ya pasó un tiempo desde que despertó. Revisa si empieza a mostrar señales de sueño."
        }

        return if (duration in 1..19) {
            SmartReminderPlan(
                delayMinutes = 1,
                delaySeconds = 45,
                title = "Siesta muy corta 😴",
                message = message,
                tag = "sleep_reminder"
            )
        } else {
            SmartReminderPlan(
                delayMinutes = calculatedDelay.toLong(),
                title = "Próximo descanso 😴",
                message = message,
                tag = "sleep_reminder"
            )
        }
    }

    private fun toMl(amount: Double?, unit: String?): Double? {
        if (amount == null) return null

        return when (unit?.lowercase()) {
            "ml", "mililitros" -> amount
            "oz", "onza", "onzas" -> amount * 29.57
            else -> amount
        }
    }
}