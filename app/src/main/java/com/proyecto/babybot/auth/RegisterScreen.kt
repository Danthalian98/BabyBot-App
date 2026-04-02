package com.proyecto.babybot.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en REGISTRO")
    }

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            onNavigateToLogin()
        }
    }

    RegisterContent(
        state = state,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onAcceptTermsChange = viewModel::onAcceptTermsChange,
        onRegisterClick = viewModel::onRegisterClick,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterContent(
    state: RegisterState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAcceptTermsChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var showTermsDialog by remember { mutableStateOf(false) }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.img_app2),
                contentDescription = "Logo BabyBot",
                modifier = Modifier.size(130.dp)
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
                        append("Únete a ")
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

            Spacer(modifier = Modifier.height(15.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.94f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = "Crear cuenta",
                        style = MaterialTheme.typography.titleLarge,
                        color = BtnTextoColorLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CustomInputField(
                        label = "Nombre de usuario",
                        placeholder = "Ingresa tu nombre",
                        value = state.name,
                        onValueChange = onNameChange,
                        inputType = InputType.TEXT
                    )
                    state.nameError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomInputField(
                        label = "Correo electrónico",
                        placeholder = "ejemplo@correo.com",
                        value = state.email,
                        onValueChange = onEmailChange,
                        inputType = InputType.EMAIL
                    )
                    state.emailError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomInputField(
                        label = "Contraseña",
                        placeholder = "Ingresa tu contraseña",
                        value = state.password,
                        onValueChange = onPasswordChange,
                        inputType = InputType.PASSWORD
                    )
                    state.passwordError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomInputField(
                        label = "Confirmar contraseña",
                        placeholder = "Repite tu contraseña",
                        value = state.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        inputType = InputType.PASSWORD
                    )
                    state.confirmPasswordError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = state.acceptTerms,
                            onCheckedChange = onAcceptTermsChange
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Acepto los Términos y Condiciones",
                            fontSize = 14.sp,
                            color = BtnTextoColorLight,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                showTermsDialog = true
                            }
                        )
                    }

                    state.termsError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Button(
                        onClick = onRegisterClick,
                        enabled = state.isFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnColorsLight
                        )
                    ) {
                        Text(
                            text = "Registrarme",
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "¿Ya tienes cuenta? ",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Inicia sesión",
                            fontSize = 14.sp,
                            color = BtnTextoColorLight,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                onNavigateToLogin()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = {
                    Text(stringResource(R.string.terms_and_conditions_title))
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text(stringResource(R.string.terms_and_conditions_content))
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onAcceptTermsChange(true)
                            showTermsDialog = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showTermsDialog = false }
                    ) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}