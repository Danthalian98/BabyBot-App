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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.proyecto.babybot.BuildConfig

private enum class AboutDialogType {
    APP_INFO,
    VERSION,
    MEDICAL_NOTICE
}

@Composable
fun SettingsAboutScreen(onBack: () -> Unit) {
    var selectedDialog by remember {
        mutableStateOf<AboutDialogType?>(null)
    }

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
                onClick = {
                    selectedDialog = AboutDialogType.APP_INFO
                }
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Info,
                title = "Versión",
                subtitle = BuildConfig.VERSION_NAME,
                iconColor = Color(0xFF77C8B2),
                onClick = {
                    selectedDialog = AboutDialogType.VERSION
                }
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
                onClick = {
                    selectedDialog = AboutDialogType.MEDICAL_NOTICE
                }
            )
        }

        Spacer(Modifier.height(24.dp))
    }

    selectedDialog?.let { dialogType ->
        AboutInfoDialog(
            dialogType = dialogType,
            onDismiss = {
                selectedDialog = null
            }
        )
    }
}

@Composable
private fun AboutInfoDialog(
    dialogType: AboutDialogType,
    onDismiss: () -> Unit
) {
    val title: String
    val message: String

    when (dialogType) {
        AboutDialogType.APP_INFO -> {
            title = "Acerca de BabyBot"
            message = "BabyBot es una aplicación creada como proyecto académico para apoyar a padres, madres y cuidadores en el registro y seguimiento básico del cuidado diario del bebé.\n\nLa app permite organizar registros, consultar información general y acceder a funciones de acompañamiento informativo."
        }

        AboutDialogType.VERSION -> {
            title = "Versión de la aplicación"
            message = "Versión instalada: ${BuildConfig.VERSION_NAME}\n\nCódigo de versión: ${BuildConfig.VERSION_CODE}\n\nEsta información ayuda a identificar la versión actual de BabyBot durante pruebas, revisiones o reportes de errores."
        }

        AboutDialogType.MEDICAL_NOTICE -> {
            title = "Aviso médico"
            message = "BabyBot ofrece información general con fines informativos y educativos.\n\nLa aplicación no sustituye la atención médica, el diagnóstico, tratamiento ni consejo de un profesional de la salud.\n\nAnte síntomas, emergencias o dudas importantes sobre el bienestar del bebé, consulta a un pediatra."
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}