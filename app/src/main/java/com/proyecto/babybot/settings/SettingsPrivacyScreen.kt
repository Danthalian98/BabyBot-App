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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private enum class PrivacyDialogType {
    BABY_DATA,
    SYNC,
    FORUMS,
    CARE_INFO
}

@Composable
fun SettingsPrivacyScreen(onBack: () -> Unit) {
    var selectedDialog by remember {
        mutableStateOf<PrivacyDialogType?>(null)
    }

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
                onClick = {
                    selectedDialog = PrivacyDialogType.BABY_DATA
                }
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Cloud,
                title = "Sincronización",
                subtitle = "Algunos datos pueden guardarse de forma segura para mantener tu información disponible.",
                iconColor = Color(0xFF77C8B2),
                onClick = {
                    selectedDialog = PrivacyDialogType.SYNC
                }
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Forum,
                title = "Foros",
                subtitle = "Evita publicar datos personales, médicos o sensibles en la comunidad.",
                iconColor = Color(0xFFA98CF2),
                onClick = {
                    selectedDialog = PrivacyDialogType.FORUMS
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSectionCard(title = "Recomendación") {
            SettingsRowItem(
                icon = Icons.Outlined.Security,
                title = "Cuida tu información",
                subtitle = "No compartas contraseñas, diagnósticos, direcciones, teléfonos o documentos personales.",
                iconColor = Color(0xFFF1BE63),
                onClick = {
                    selectedDialog = PrivacyDialogType.CARE_INFO
                }
            )
        }

        Spacer(Modifier.height(24.dp))
    }

    selectedDialog?.let { dialogType ->
        PrivacyInfoDialog(
            dialogType = dialogType,
            onDismiss = {
                selectedDialog = null
            }
        )
    }
}

@Composable
private fun PrivacyInfoDialog(
    dialogType: PrivacyDialogType,
    onDismiss: () -> Unit
) {
    val title: String
    val message: String

    when (dialogType) {
        PrivacyDialogType.BABY_DATA -> {
            title = "Datos del bebé"
            message = "Los datos del bebé se usan para personalizar registros, resúmenes y recomendaciones dentro de BabyBot.\n\nEstos datos ayudan a que la app muestre información más útil según la edad, peso, talla y registros ingresados."
        }

        PrivacyDialogType.SYNC -> {
            title = "Sincronización"
            message = "Los datos principales se guardan localmente en el dispositivo.\n\nAlgunos datos necesarios pueden sincronizarse con Firebase para recuperar tu información si reinstalas la app o cambias de dispositivo."
        }

        PrivacyDialogType.FORUMS -> {
            title = "Foros"
            message = "La información que publiques en los foros puede ser visible para otros usuarios.\n\nEvita compartir datos personales, información médica sensible o detalles que permitan identificarte a ti o a tu bebé."
        }

        PrivacyDialogType.CARE_INFO -> {
            title = "Cuida tu información"
            message = "No publiques teléfonos, direcciones, documentos personales, nombres completos del bebé, diagnósticos médicos ni información sensible en los foros.\n\nBabyBot es una herramienta informativa y no sustituye la orientación de un pediatra."
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