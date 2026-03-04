package com.proyecto.babybot.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import com.proyecto.babybot.R
import com.proyecto.babybot.ui.theme.BtnColorsLight
import com.proyecto.babybot.ui.theme.BtnTextoColorLight

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app),
                    contentDescription = "Logo",
                    modifier = Modifier.size(280.dp)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    CustomInputField(
                        label = "Ingresa tu usuario",
                        placeholder = "Nombre de usuario",
                        value = state.name,
                        onValueChange = onNameChange
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    CustomInputField(
                        label = "Ingresa tu correo",
                        placeholder = "Correo electrónico",
                        value = state.email,
                        onValueChange = onEmailChange
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    CustomInputField(
                        label = "Ingresa tu contraseña",
                        placeholder = "Contraseña",
                        value = state.password,
                        onValueChange = onPasswordChange,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    CustomInputField(
                        label = "Confirmar contraseña",
                        placeholder = "Repite tu contraseña",
                        value = state.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.acceptTerms,
                            onCheckedChange = onAcceptTermsChange
                        )

                        Text(
                            text = "Acepto los T&C",
                            fontSize = 14.sp,
                            color = BtnTextoColorLight,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                onAcceptTermsChange(!state.acceptTerms)
                            }
                        )
                    }

                    Button(
                        onClick = onRegisterClick,
                        enabled = state.acceptTerms,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnColorsLight
                        )
                    ) {
                        Text("Registrar")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "Iniciar sesión",
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
        }
    }
}
