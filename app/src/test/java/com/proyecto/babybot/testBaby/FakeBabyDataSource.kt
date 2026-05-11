package com.proyecto.babybot.testBaby

import com.proyecto.babybot.data.firebase.Baby
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.google.firebase.Timestamp
import java.util.Calendar

class FakeBabyDataSource {
    fun validar(baby: Baby): Boolean {
        return baby.nombre.isNotBlank() &&
                (baby.fechaNacimiento?.let { it.seconds <= Timestamp.now().seconds } ?: false)
    }
}

class BabyValidationTest {

    private lateinit var fakeDataSource: FakeBabyDataSource

    @Before
    fun setup() {
        fakeDataSource = FakeBabyDataSource()
    }

    @Test
    fun `PU-08 - Nombre del bebe vacio no debe permitirse`() = runTest {
        // Given
        val babyInvalido = Baby(nombre = "", idUsuario = "user123")

        // When
        val esValido = babyInvalido.nombre.isNotBlank()

        // Then
        assertFalse("El nombre no debería estar vacío", esValido)
    }

    @Test
    fun `PU-09 - Fecha de nacimiento futura debe retornar error`() = runTest {
        // Given: Una fecha de mañana
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val fechaFutura = Timestamp(calendar.time)

        // ✅ Corregido: Inicializamos con el Timestamp futuro
        val babyInvalido = Baby(nombre = "Baby Bot", fechaNacimiento = fechaFutura)

        // When
        val esFechaValida = babyInvalido.fechaNacimiento?.let {
            it.seconds <= Timestamp.now().seconds
        } ?: false

        // Then
        assertFalse("La fecha no puede ser futura", esFechaValida)
    }
}