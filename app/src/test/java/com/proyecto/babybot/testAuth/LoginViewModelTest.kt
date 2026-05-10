package com.proyecto.babybot.testAuth

import com.proyecto.babybot.auth.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDataSource: FakeAuthDataSource
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        // Obligamos a que las corrutinas se ejecuten en un entorno controlado de test
        Dispatchers.setMain(testDispatcher)
        fakeDataSource = FakeAuthDataSource()
        viewModel = LoginViewModel(fakeDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `PU-01 - Login exitoso actualiza el estado a logueado`(): Unit = runTest {
        // 1. Entrada (Datos válidos)
        viewModel.onEmailChange("usuario@test.com")
        viewModel.onPasswordChange("123456")
        fakeDataSource.shouldSucceed = true

        // 2. Procedimiento (Ejecutar login)
        viewModel.onLoginClick()
        testDispatcher.scheduler.advanceUntilIdle() // Esperamos a que termine la tarea

        // 3. Resultado esperado (Validación)
        assert(viewModel.state.value.isLoggedIn)
        assert(viewModel.state.value.error == null)
    }
}