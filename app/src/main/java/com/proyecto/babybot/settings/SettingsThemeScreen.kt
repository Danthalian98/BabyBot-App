package com.proyecto.babybot.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsThemeScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(onBack = onBack)

        Spacer(Modifier.height(12.dp))

        SettingsSectionCard(title = "Tema") {
            SettingsRowItem(
                icon = Icons.Outlined.LightMode,
                title = "Modo claro",
                subtitle = "Tema actual de BabyBot",
                iconColor = Color(0xFF6D8FF2),
                onClick = {}
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.DarkMode,
                title = "Modo oscuro",
                subtitle = "Estamos trabajando en esta opción",
                iconColor = Color(0xFFA98CF2),
                onClick = {}
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSectionCard(title = "Nota") {
            Text(
                text = "Por ahora BabyBot usa modo claro para mantener una experiencia visual consistente. El modo oscuro y la opción de usar el tema del sistema se considerarán en futuras versiones.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}