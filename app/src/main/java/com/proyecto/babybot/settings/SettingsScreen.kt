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
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        SettingsContent(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            onBack = onBack,
            onLogout = { viewModel.logout() }
        )
    }
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onLogout: () -> Unit
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
                onClick = {}
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Lock,
                title = "Seguridad",
                subtitle = "Cambia tu contraseña y opciones",
                iconColor = Color(0xFF77C8B2),
                onClick = {}
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Notifications,
                title = "Notificaciones",
                subtitle = "Personaliza tus preferencias",
                iconColor = Color(0xFFA98CF2),
                onClick = {}
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Security,
                title = "Privacidad",
                subtitle = "Controla tu información y privacidad",
                iconColor = Color(0xFFF1BE63),
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(title = "Aplicación") {
            SettingsRowItem(
                icon = Icons.Outlined.DarkMode,
                title = "Tema",
                subtitle = "Claro / Oscuro / Sistema",
                iconColor = Color(0xFF6D8FF2),
                onClick = {}
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Language,
                title = "Idioma",
                subtitle = "Español",
                iconColor = Color(0xFF77C8B2),
                onClick = {}
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.Info,
                title = "Acerca de BabyBot",
                subtitle = "Versión 1.0.0",
                iconColor = Color(0xFFA98CF2),
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LogoutCard(onLogout = onLogout)

        Spacer(modifier = Modifier.height(24.dp))
    }
}