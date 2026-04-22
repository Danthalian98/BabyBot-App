package com.proyecto.babybot.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.R
import com.proyecto.babybot.ui.theme.BtnColorsLight
import com.proyecto.babybot.ui.theme.BtnTextoColorLight
import com.proyecto.babybot.ui.components.CustomInputField
import com.proyecto.babybot.ui.components.InputType

@Composable
fun LoginScreen(
    onNavigateToTrial: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en LOGIN")
    }

    LaunchedEffect(state.nextRoute) {
        when (state.nextRoute) {
            "trial" -> onNavigateToTrial()
            "home" -> onNavigateToHome()
            "subscriptions" -> onNavigateToSubscriptions()
        }
    }

    LoginContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onLoginClick,
        onClearError = viewModel::onClearError,
        onForgotPasswordClick = viewModel::onForgotPasswordClick,
        clearMessage = viewModel::clearMessage,
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun LoginContent(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onClearError: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    clearMessage: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.img_splash_bg),
            contentDescription = "Fondo BabyBot",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Capa para mejorar legibilidad
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x66000000),
                            Color(0x33000000),
                            Color(0x80000000)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo / cabecera
            Image(
                painter = painterResource(id = R.drawable.img_app2),
                contentDescription = "Logo BabyBot",
                modifier = Modifier
                    .size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Bienvenido a ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = BtnColorsLight,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Baby")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFFA8E48D),
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Bot")
                    }
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.94f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.titleLarge,
                        color = BtnTextoColorLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CustomInputField(
                        label = "Correo electrónico",
                        placeholder = "ejemplo@correo.com",
                        value = state.email,
                        onValueChange = onEmailChange,
                        inputType = InputType.EMAIL
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    CustomInputField(
                        label = "Contraseña",
                        placeholder = "Ingresa tu contraseña",
                        value = state.password,
                        onValueChange = onPasswordChange,
                        inputType = InputType.PASSWORD
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onLoginClick,
                        enabled = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnColorsLight
                        )
                    ) {
                        Text(
                            text = if (state.isLoading) "Ingresando..." else "Iniciar sesión",
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = BtnTextoColorLight,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { onForgotPasswordClick() }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "¿No tienes cuenta? ",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Regístrate",
                            fontSize = 14.sp,
                            color = BtnTextoColorLight,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.error != null) {
            AlertDialog(
                onDismissRequest = onClearError,
                confirmButton = {
                    TextButton(onClick = onClearError) {
                        Text("OK")
                    }
                },
                title = { Text("Error de acceso") },
                text = { Text(state.error) }
            )
        }

        state.message?.let { msg ->
            AlertDialog(
                onDismissRequest = { clearMessage() },
                confirmButton = {
                    TextButton(onClick = { clearMessage() }) {
                        Text("OK")
                    }
                },
                title = { Text("Recuperar contraseña") },
                text = { Text(msg) }
            )
        }
    }
}
