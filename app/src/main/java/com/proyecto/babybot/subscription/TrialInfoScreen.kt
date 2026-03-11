package com.proyecto.babybot.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TrialInfoScreen(
    viewModel: TrialInfoViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    when {

        state.isLoading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        }

        state.error != null -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${state.error}")
            }

        }

        else -> {

            val isTrialActive = state.isTrialActive

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    if (isTrialActive) {

                        Text(
                            text = "Tienes 7 días de prueba gratuita",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Después de este periodo podrás elegir un plan.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                    } else {

                        Text(
                            text = "Tu periodo de prueba ha expirado",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Para seguir usando BabyBot necesitas comprar una licencia.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.onContinueClicked {
                                onNavigateToHome()
                            }
                        }
                    ) {
                        Text(
                            if (isTrialActive) "Continuar"
                            else "Continuar (modo demo)"
                        )
                    }
                }
            }
        }
    }
}