package com.proyecto.babybot.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsPrivacyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(onBack = onBack)

        Spacer(Modifier.height(12.dp))

        SettingsSectionCard(title = "Privacidad") {
            SettingsRowItem(
                icon = Icons.Outlined.ChildCare,
                title = "Datos del bebé",
                subtitle = "Se usan para personalizar registros y resúmenes dentro de la app.",
                iconColor = Color(0xFF6D8FF2),
                onClick = {}
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Cloud,
                title = "Sincronización",
                subtitle = "Algunos datos pueden guardarse de forma segura para mantener tu información disponible.",
                iconColor = Color(0xFF77C8B2),
                onClick = {}
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Forum,
                title = "Foros",
                subtitle = "Evita publicar datos personales, médicos o sensibles en la comunidad.",
                iconColor = Color(0xFFA98CF2),
                onClick = {}
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSectionCard(title = "Recomendación") {
            SettingsRowItem(
                icon = Icons.Outlined.Security,
                title = "Cuida tu información",
                subtitle = "No compartas contraseñas, diagnósticos, direcciones, teléfonos o documentos personales.",
                iconColor = Color(0xFFF1BE63),
                onClick = {}
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}