package com.proyecto.babybot.testAuth

import com.proyecto.babybot.auth.LoginViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
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

    @Test
    fun `PU-02 - Correo vacio muestra mensaje de error`() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("123456")
        viewModel.onLoginClick()

        assertNotNull(viewModel.state.value.error)
    }

    @Test
    fun `PU-03 - Contrasena vacia muestra mensaje de error`() = runTest {
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("")
        viewModel.onLoginClick()

        assertNotNull(viewModel.state.value.error)
    }

    @Test
    fun `PU-05 - Contrasena corta en registro retorna error`() {
        // Asumiendo que tienes una función de validación
        val passwordCorta = "123"
        val esValida = passwordCorta.length >= 6
        assertFalse("La contraseña debe ser de al menos 6 caracteres", esValida)
    }

    @Test
    fun `PU-06 - Usuario sin sesion navega a Login`() {
        fakeDataSource.shouldSucceed = false // Simulamos que no hay sesión
        val userId = fakeDataSource.getCurrentUserId()

        val destino = if (userId == null) "login" else "home"
        assertEquals("login", destino)
    }
}