package com.proyecto.babybot.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.R
import com.proyecto.babybot.ui.components.CustomInputField
import com.proyecto.babybot.ui.components.InputType
import com.proyecto.babybot.ui.theme.BtnColorsLight
import com.proyecto.babybot.ui.theme.BtnTextoColorLight

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.message) {
        if (state.message != null) {
            kotlinx.coroutines.delay(2000) // 2 segundos
            onBackToLogin()
        }
    }

    ForgotPasswordContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onSendResetClick = viewModel::onSendResetClick,
        onBackToLogin = onBackToLogin,
        onClearError = viewModel::clearError
    )
}

@Composable
fun ForgotPasswordContent(
    state: ForgotPasswordState,
    onEmailChange: (String) -> Unit,
    onSendResetClick: () -> Unit,
    onBackToLogin: () -> Unit,
    onClearError: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_splash_bg),
            contentDescription = "Fondo BabyBot",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

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

        IconButton(
            onClick = onBackToLogin,
            modifier = Modifier
                .padding(top = 36.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Regresar",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app2),
                contentDescription = "Logo BabyBot",
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Recuperar contraseña",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
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
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.titleLarge,
                        color = BtnTextoColorLight,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ingresa el correo de tu cuenta y te enviaremos un enlace para crear una nueva contraseña.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    CustomInputField(
                        label = "Correo electrónico",
                        placeholder = "ejemplo@correo.com",
                        value = state.email,
                        onValueChange = onEmailChange,
                        inputType = InputType.EMAIL
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = onSendResetClick,
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
                            text = if (state.isLoading) {
                                "Enviando..."
                            } else {
                                "Enviar correo"
                            }
                        )
                    }

                    state.message?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = it,
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Volver a iniciar sesión",
                        color = BtnTextoColorLight,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { onBackToLogin() }
                    )
                }
            }
        }

        state.error?.let {
            AlertDialog(
                onDismissRequest = onClearError,
                confirmButton = {
                    TextButton(onClick = onClearError) {
                        Text("Aceptar")
                    }
                },
                title = {
                    Text("Aviso")
                },
                text = {
                    Text(it)
                }
            )
        }
    }
}