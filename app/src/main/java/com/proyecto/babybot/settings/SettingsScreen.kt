package com.proyecto.babybot.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import com.proyecto.babybot.BuildConfig

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onAccountClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onChatHistoryClick: () -> Unit,
    onThemeClick: () -> Unit,
    onAboutClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "Cerrar sesión")
            },
            text = {
                Text(text = "¿Seguro que quieres cerrar sesión?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    }
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        SettingsContent(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            onBack = onBack,
            onLogout = { showLogoutDialog = true },
            onAccountClick = onAccountClick,
            onSecurityClick = onSecurityClick,
            onNotificationsClick = onNotificationsClick,
            onPrivacyClick = onPrivacyClick,
            onChatHistoryClick = onChatHistoryClick,
            onThemeClick = onThemeClick,
            onAboutClick = onAboutClick,
        )
    }
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onAccountClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onChatHistoryClick: () -> Unit,
    onThemeClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(onBack = onBack)

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSectionCard(title = "Cuenta") {
            SettingsRowItem(
                icon = Icons.Outlined.Person,
                title = "Información de la cuenta",
                subtitle = "Edita tu perfil y datos personales",
                iconColor = Color(0xFF6D8FF2),
                onClick = onAccountClick
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Lock,
                title = "Seguridad",
                subtitle = "Cambia tu contraseña y opciones",
                iconColor = Color(0xFF77C8B2),
                onClick = onSecurityClick
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Notifications,
                title = "Notificaciones",
                subtitle = "Personaliza tus preferencias",
                iconColor = Color(0xFFA98CF2),
                onClick = onNotificationsClick
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Chat,
                title = "Historial del chatbot",
                subtitle = "Consulta y elimina conversaciones",
                iconColor = Color(0xFF5DADE2),
                onClick = onChatHistoryClick
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Security,
                title = "Privacidad",
                subtitle = "Controla tu información y privacidad",
                iconColor = Color(0xFFF1BE63),
                onClick = onPrivacyClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(title = "Aplicación") {
            SettingsRowItem(
                icon = Icons.Outlined.DarkMode,
                title = "Tema",
                subtitle = "Claro / Oscuro / Sistema",
                iconColor = Color(0xFF6D8FF2),
                onClick = onThemeClick
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Info,
                title = "Acerca de BabyBot",
                subtitle = BuildConfig.VERSION_NAME,
                iconColor = Color(0xFFA98CF2),
                onClick = onAboutClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LogoutCard(onLogout = onLogout)

        Spacer(modifier = Modifier.height(24.dp))
    }
}