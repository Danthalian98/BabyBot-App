package com.proyecto.babybot.homeTest

import com.proyecto.babybot.home.HomeViewModel
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeHomeRepository
    private lateinit var fakeAuth: FakeAuthDataSource
    private lateinit var viewModel: HomeViewModel

    // Mock del contexto para el constructor
    private val mockContext = Mockito.mock(android.content.Context::class.java)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeHomeRepository()
        fakeAuth = FakeAuthDataSource()
        // El orden debe ser: Context, Auth, Repo
        viewModel = HomeViewModel(mockContext, fakeAuth, fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `PU-10 - Existe bebe registrado muestra nombre y edad`() = runTest {
        // Given
        fakeRepository.simularBebeNulo = false
        fakeAuth.shouldSucceed = true // Para que getCurrentUserId() no sea null

        // When
        viewModel.loadHomeData()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue("Debe detectar que tiene un bebé", viewModel.state.value.hasBaby)
        assertEquals("Bebé de Prueba", viewModel.state.value.babyName)
        assertNotNull(viewModel.state.value.babyAge)
    }

    @Test
    fun `PU-11 - No existe bebe registrado muestra estado vacio`() = runTest {
        // Given
        fakeRepository.simularBebeNulo = true
        fakeAuth.shouldSucceed = true

        // When
        viewModel.loadHomeData()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse("hasBaby debe ser falso", viewModel.state.value.hasBaby)
        assertEquals("", viewModel.state.value.babyName)
        assertTrue("La lista de resumen debe estar vacía", viewModel.state.value.summary.isEmpty())
    }
}