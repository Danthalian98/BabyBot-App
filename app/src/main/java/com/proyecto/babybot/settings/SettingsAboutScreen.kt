package com.proyecto.babybot.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsAboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(onBack = onBack)

        Spacer(Modifier.height(12.dp))

        SettingsSectionCard(title = "Acerca de BabyBot") {
            SettingsRowItem(
                icon = Icons.Outlined.SmartToy,
                title = "BabyBot",
                subtitle = "Asistente para padres primerizos",
                iconColor = Color(0xFF6D8FF2),
                onClick = {}
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Info,
                title = "Versión",
                subtitle = "1.0.0",
                iconColor = Color(0xFF77C8B2),
                onClick = {}
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSectionCard(title = "Propósito") {
            Text(
                text = "BabyBot ayuda a registrar actividades del bebé, consultar información general y organizar datos importantes del cuidado diario.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSectionCard(title = "Aviso importante") {
            SettingsRowItem(
                icon = Icons.Outlined.MedicalInformation,
                title = "No reemplaza atención médica",
                subtitle = "La información mostrada es orientativa. Ante síntomas, emergencias o dudas importantes, consulta a un pediatra.",
                iconColor = Color(0xFFF1BE63),
                onClick = {}
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}