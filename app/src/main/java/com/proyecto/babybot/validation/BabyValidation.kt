package com.proyecto.babybot.validation

import java.util.Calendar
import java.util.Locale

enum class BabyField {
    NAME,
    GENDER,
    BIRTH_DATE,
    WEIGHT,
    HEIGHT,
    BLOOD_TYPE
}

data class BabyValidationResult(
    val isValid: Boolean,
    val errors: Map<BabyField, String>
)

object BabyValidation {

    private val validBloodTypes = setOf(
        "O+",
        "O-",
        "A+",
        "A-",
        "B+",
        "B-",
        "AB+",
        "AB-"
    )

    fun validateBabyData(
        name: String,
        gender: String,
        birthDateMillis: Long?,
        weightText: String,
        heightText: String,
        bloodType: String,
        requireBloodType: Boolean = false
    ): BabyValidationResult {
        val errors = mutableMapOf<BabyField, String>()

        validateName(name)?.let {
            errors[BabyField.NAME] = it
        }

        validateGender(gender)?.let {
            errors[BabyField.GENDER] = it
        }

        validateBirthDate(birthDateMillis)?.let {
            errors[BabyField.BIRTH_DATE] = it
        }

        validateWeight(weightText)?.let {
            errors[BabyField.WEIGHT] = it
        }

        validateHeight(heightText)?.let {
            errors[BabyField.HEIGHT] = it
        }

        validateBloodType(
            bloodType = bloodType,
            required = requireBloodType
        )?.let {
            errors[BabyField.BLOOD_TYPE] = it
        }

        return BabyValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    fun parseDecimal(value: String): Double? {
        return value
            .trim()
            .replace(",", ".")
            .toDoubleOrNull()
    }

    private fun validateName(name: String): String? {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return "El nombre del bebé es obligatorio."
        }

        if (cleanName.length < 2) {
            return "El nombre debe tener al menos 2 caracteres."
        }

        if (cleanName.length > 40) {
            return "El nombre no debe superar los 40 caracteres."
        }

        val validNameRegex = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s'-]+$")

        if (!validNameRegex.matches(cleanName)) {
            return "El nombre solo debe contener letras y espacios."
        }

        return null
    }

    private fun validateGender(gender: String): String? {
        if (gender.isBlank()) {
            return "Selecciona el género del bebé."
        }

        if (gender !in listOf("M", "F")) {
            return "Selecciona un género válido."
        }

        return null
    }

    private fun validateBirthDate(birthDateMillis: Long?): String? {
        if (birthDateMillis == null) {
            return "La fecha de nacimiento es obligatoria."
        }

        val today = Calendar.getInstance()
        clearTime(today)

        val birthDate = Calendar.getInstance().apply {
            timeInMillis = birthDateMillis
        }
        clearTime(birthDate)

        if (birthDate.after(today)) {
            return "La fecha de nacimiento no puede ser futura."
        }

        val ageInMonths = calculateAgeInMonths(birthDate, today)

        if (ageInMonths > 36) {
            return "BabyBot está pensado para bebés de 0 a 3 años."
        }

        return null
    }

    private fun validateWeight(weightText: String): String? {
        if (weightText.isBlank()) {
            return "El peso es obligatorio."
        }

        val weight = parseDecimal(weightText)

        if (weight == null) {
            return "Ingresa un peso válido. Ejemplo: 3.200"
        }

        if (weight < 1.0 || weight > 25.0) {
            return "El peso debe estar entre 1 y 25 kg."
        }

        return null
    }

    private fun validateHeight(heightText: String): String? {
        if (heightText.isBlank()) {
            return "La talla es obligatoria."
        }

        val height = parseDecimal(heightText)

        if (height == null) {
            return "Ingresa una talla válida. Ejemplo: 50"
        }

        if (height < 30.0 || height > 120.0) {
            return "La talla debe estar entre 30 y 120 cm."
        }

        return null
    }

    private fun validateBloodType(
        bloodType: String,
        required: Boolean
    ): String? {
        if (bloodType.isBlank()) {
            return if (required) {
                "Selecciona el tipo de sangre."
            } else {
                null
            }
        }

        val normalizedBloodType = bloodType.trim().uppercase(Locale.getDefault())

        if (normalizedBloodType !in validBloodTypes) {
            return "Selecciona un tipo de sangre válido."
        }

        return null
    }

    private fun calculateAgeInMonths(
        birthDate: Calendar,
        today: Calendar
    ): Int {
        var months = (today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)) * 12
        months += today.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH)

        if (today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH)) {
            months--
        }

        return months
    }

    private fun clearTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }
}