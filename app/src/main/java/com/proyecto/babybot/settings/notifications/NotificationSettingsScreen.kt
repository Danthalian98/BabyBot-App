package com.proyecto.babybot.settings.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.babybot.settings.SettingsDivider
import com.proyecto.babybot.settings.SettingsHeader
import com.proyecto.babybot.settings.SettingsSectionCard
import androidx.hilt.navigation.compose.hiltViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            viewModel.refreshPermissionStatus()
            viewModel.setGeneralNotificationsEnabled(isGranted)
        }

    LaunchedEffect(Unit) {
        viewModel.refreshPermissionStatus()
    }

    NotificationSettingsContent(
        state = state,
        onBack = onBack,
        onGeneralNotificationsChange = { enabled ->
            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    viewModel.setGeneralNotificationsEnabled(true)
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                viewModel.setGeneralNotificationsEnabled(enabled)
            }
        },
        onSessionNotificationsChange = viewModel::setSessionNotificationsEnabled,
        onRemindersChange = viewModel::setRemindersEnabled,
        onForumNotificationsChange = viewModel::setForumNotificationsEnabled
    )
}

@Composable
fun NotificationSettingsContent(
    state: NotificationSettingsState,
    onBack: () -> Unit,
    onGeneralNotificationsChange: (Boolean) -> Unit,
    onSessionNotificationsChange: (Boolean) -> Unit,
    onRemindersChange: (Boolean) -> Unit,
    onForumNotificationsChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(onBack = onBack)

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSectionCard(title = "Notificaciones") {
            NotificationSwitchRow(
                icon = Icons.Outlined.NotificationsActive,
                title = "Notificaciones de BabyBot",
                subtitle = if (state.systemNotificationsAllowed) {
                    "Permite que BabyBot muestre avisos importantes en tu dispositivo."
                } else {
                    "El permiso fue bloqueado desde Android. Actívalo en los ajustes del sistema para usar notificaciones."
                },
                iconColor = Color(0xFF6D8FF2),
                checked = state.generalNotificationsEnabled,
                onCheckedChange = onGeneralNotificationsChange
            )

            SettingsDivider()

            NotificationSwitchRow(
                icon = Icons.Outlined.Timer,
                title = "Sesiones activas",
                subtitle = "Muestra una notificación mientras registras lactancia o sueño.",
                iconColor = Color(0xFF6D8FF2),
                checked = state.sessionNotificationsEnabled,
                enabled = state.canUseNotificationFeatures,
                onCheckedChange = onSessionNotificationsChange
            )

            SettingsDivider()

            NotificationSwitchRow(
                icon = Icons.Outlined.TipsAndUpdates,
                title = "Recordatorios",
                subtitle = "Avisos para comidas, sueño y pañales.",
                iconColor = Color(0xFF77C8B2),
                checked = state.remindersEnabled,
                enabled = state.canUseNotificationFeatures,
                onCheckedChange = onRemindersChange
            )

            SettingsDivider()

            NotificationSwitchRow(
                icon = Icons.Outlined.Forum,
                title = "Foros",
                subtitle = "Respuestas y actividad en tus publicaciones.",
                iconColor = Color(0xFFA98CF2),
                checked = state.forumNotificationsEnabled,
                enabled = state.canUseNotificationFeatures,
                onCheckedChange = onForumNotificationsChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(title = "Estado actual") {
            Text(
                text = "Actualmente BabyBot usa notificaciones para mantener visibles los cronómetros activos y permitir finalizar o cancelar sesiones desde la barra de notificaciones.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(title = "Permisos") {
            Text(
                text = "Si las notificaciones no aparecen, revisa que el permiso de notificaciones esté habilitado en los ajustes del sistema Android.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun NotificationSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(46.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = iconColor.copy(alpha = if (enabled) 0.14f else 0.07f)
        ) {
            Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) iconColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
    }
}