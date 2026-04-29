package com.proyecto.babybot.settings.security

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.settings.SettingsDivider
import com.proyecto.babybot.settings.SettingsHeader
import com.proyecto.babybot.settings.SettingsSectionCard

@Composable
fun SecuritySettingsScreen(
    onBack: () -> Unit,
    viewModel: SecuritySettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.successMessage != null || state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = {
                Text(
                    text = if (state.successMessage != null) "Correo enviado" else "Algo salió mal"
                )
            },
            text = {
                Text(text = state.successMessage ?: state.errorMessage.orEmpty())
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearMessages() }) {
                    Text("Entendido")
                }
            }
        )
    }

    SecuritySettingsContent(
        state = state,
        onBack = onBack,
        onResetPasswordClick = viewModel::sendPasswordReset
    )
}

@Composable
fun SecuritySettingsContent(
    state: SecuritySettingsState,
    onBack: () -> Unit,
    onResetPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(onBack = onBack)

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSectionCard(title = "Seguridad") {
            SecurityInfoRow(
                icon = Icons.Outlined.Email,
                title = "Correo de la cuenta",
                subtitle = state.email.ifBlank { "Correo no disponible" },
                iconColor = Color(0xFF6D8FF2)
            )

            SettingsDivider()

            SecurityActionRow(
                title = "Restablecer contraseña",
                subtitle = "Recibe un enlace seguro en tu correo",
                iconColor = Color(0xFF77C8B2),
                isLoading = state.isLoading,
                onClick = onResetPasswordClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(title = "Recomendaciones") {
            SecurityInfoRow(
                icon = Icons.Outlined.Security,
                title = "Protege tu cuenta",
                subtitle = "Usa una contraseña única y evita compartir tu acceso.",
                iconColor = Color(0xFFA98CF2)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SecurityInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.14f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SecurityActionRow(
    title: String,
    subtitle: String,
    iconColor: Color,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.14f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.LockReset,
                    contentDescription = null,
                    tint = iconColor
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = onClick,
            enabled = !isLoading,
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Enviar")
            }
        }
    }
}