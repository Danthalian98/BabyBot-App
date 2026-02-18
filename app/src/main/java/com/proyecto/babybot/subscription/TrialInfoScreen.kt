package com.proyecto.babybot.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TrialInfoScreen(
    viewModel: TrialInfoViewModel = viewModel(),
    onNavigateToHome: () -> Unit //,
    //onNavigateToSubscription: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Tienes 7 días de prueba gratuita",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Después de este periodo podrás elegir un plan.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.onContinueClicked { canAccess ->
                        if (canAccess) {
                            onNavigateToHome()
                        } else {
                            //onNavigateToSubscription()
                        }
                    }
                }
            ) {
                Text("Continuar")
            }
        }
    }
}
