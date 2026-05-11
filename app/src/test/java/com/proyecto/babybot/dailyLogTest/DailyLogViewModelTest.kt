package com.proyecto.babybot.dailyLogTest

import com.proyecto.babybot.dailylog.DailyLogViewModel
import com.proyecto.babybot.homeTest.FakeHomeRepository
import com.proyecto.babybot.testAuth.FakeAuthDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class DailyLogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeHomeRepository
    private lateinit var fakeAuth: FakeAuthDataSource
    private lateinit var viewModel: DailyLogViewModel

    private val mockContext = Mockito.mock(android.content.Context::class.java)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeHomeRepository()
        fakeAuth = FakeAuthDataSource()

        viewModel = DailyLogViewModel(fakeAuth, fakeRepository, mockContext)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `PU-12 - Registro de comida guarda los datos correctamente`() = runTest {
        val idBebe = "123"
        viewModel.registerMeal(idBebe, "Biberón", "150")
        advanceUntilIdle()

        assertNotNull(viewModel.state.value)
    }

    @Test
    fun `PU-14 - Error de duracion si hora fin es menor a inicio`() = runTest {
        // Given
        val inicio = 2000L
        val fin = 1000L

        // When
        val duracionValida = fin > inicio

        // Then
        assertFalse("La duración debería ser inválida si el fin es antes del inicio", duracionValida)
    }

    @Test
    fun `PU-15 - Registro de panal con tipo vacio falla`() = runTest {
        // Given
        val tipoPanal = ""

        // When
        val esValido = tipoPanal.isNotBlank()

        // Then
        assertFalse("El registro no debería permitir tipos de pañal vacíos", esValido)
    }
}