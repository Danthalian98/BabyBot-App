package com.proyecto.babybot.settings.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BabyChangingStation
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.settings.SettingsDivider
import com.proyecto.babybot.settings.SettingsHeader
import com.proyecto.babybot.settings.SettingsRowItem
import com.proyecto.babybot.settings.SettingsSectionCard
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun AccountSettingsScreen(
    onBack: () -> Unit,
    onEditInfoClick: (Int) -> Unit,
    viewModel: AccountSettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadAccount()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text("Cuenta") },
            text = { Text(state.errorMessage.orEmpty()) },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) {
                    Text("Entendido")
                }
            }
        )
    }

    AccountSettingsContent(
        state = state,
        onBack = onBack,
        onEditInfoClick = onEditInfoClick
    )
}

@Composable
fun AccountSettingsContent(
    state: AccountSettingsState,
    onBack: () -> Unit,
    onEditInfoClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(onBack = onBack)

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSectionCard(title = "Información de la cuenta") {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                AccountInfoRow(
                    icon = Icons.Outlined.Person,
                    title = "Nombre",
                    value = state.name.ifBlank { "Sin nombre registrado" },
                    iconColor = Color(0xFF6D8FF2)
                )

                SettingsDivider()

                AccountInfoRow(
                    icon = Icons.Outlined.Email,
                    title = "Correo",
                    value = state.email.ifBlank { "Correo no disponible" },
                    iconColor = Color(0xFF77C8B2)
                )

                SettingsDivider()

                AccountInfoRow(
                    icon = Icons.Outlined.VerifiedUser,
                    title = "Estado de cuenta",
                    value = formatAccountStatus(state.accountStatus),
                    iconColor = Color(0xFFA98CF2)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(title = "Perfil del bebé") {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else if (state.babiesCount == 0) {
                AccountInfoRow(
                    icon = Icons.Outlined.ChildCare,
                    title = "Bebés registrados",
                    value = "No hay un bebé registrado",
                    iconColor = Color(0xFFF1BE63)
                )
            } else {
                AccountInfoRow(
                    icon = Icons.Outlined.ChildCare,
                    title = "Bebés registrados",
                    value = state.babiesCount.toString(),
                    iconColor = Color(0xFF6D8FF2)
                )

                SettingsDivider()

                AccountInfoRow(
                    icon = Icons.Outlined.BabyChangingStation,
                    title = "Bebé activo",
                    value = state.babyName.ifBlank { "No disponible" },
                    iconColor = Color(0xFF77C8B2)
                )

                SettingsDivider()

                AccountInfoRow(
                    icon = Icons.Outlined.Badge,
                    title = "Edad",
                    value = state.babyAge.ifBlank { "No disponible" },
                    iconColor = Color(0xFFA98CF2)
                )

                SettingsDivider()

                AccountInfoRow(
                    icon = Icons.Outlined.MonitorWeight,
                    title = "Peso",
                    value = state.babyWeight.ifBlank { "No registrado" },
                    iconColor = Color(0xFFF1BE63)
                )

                SettingsDivider()

                AccountInfoRow(
                    icon = Icons.Outlined.Height,
                    title = "Talla",
                    value = state.babyHeight.ifBlank { "No registrado" },
                    iconColor = Color(0xFF6D8FF2)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSectionCard(title = "Acciones") {
            SettingsRowItem(
                icon = Icons.Outlined.Edit,
                title = "Editar perfil",
                subtitle = "Modifica tu información personal",
                iconColor = Color(0xFF6D8FF2),
                onClick = { onEditInfoClick(0) }
            )

            SettingsDivider()

            SettingsRowItem(
                icon = Icons.Outlined.ChildCare,
                title = "Gestionar información del bebé",
                subtitle = "Consulta o actualiza los datos registrados",
                iconColor = Color(0xFF77C8B2),
                onClick = { onEditInfoClick(1) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AccountInfoRow(
    icon: ImageVector,
    title: String,
    value: String,
    iconColor: Color
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
            color = iconColor.copy(alpha = 0.14f)
        ) {
            Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
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
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatAccountStatus(status: String): String {
    return when (status.uppercase()) {
        "PREMIUM" -> "Premium"
        "TRIAL" -> "Prueba gratuita"
        "VENCIDA" -> "Prueba vencida"
        else -> status.ifBlank { "Sin estado disponible" }
    }
}