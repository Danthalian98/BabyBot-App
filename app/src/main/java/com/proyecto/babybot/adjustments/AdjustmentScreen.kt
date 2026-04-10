package com.proyecto.babybot.adjustments

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.ui.theme.*

@Composable
fun AdjustmentScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdjustmentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en ADJUSTMENTS")
    }

    AdjustmentContent(
        state = state,
        onBackClick = onBackClick,
        onNotificationsToggle = { viewModel.toggleNotifications(it) },
        onDarkModeToggle = { viewModel.toggleDarkMode(it) },
        onLogoutClick = {
            viewModel.logout()
            onLogoutSuccess()
        },
        modifier = modifier
    )
}

@Composable
fun AdjustmentContent(
    state: AdjustmentState,
    onBackClick: () -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackPantallas)
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavTopColorLight)
                .padding(top = 24.dp, bottom = 24.dp, start = 20.dp, end = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ajustes",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Sección del perfil
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Avatar o Icono circular
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(NavTopColorLight.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (state.userName.isNotEmpty()) state.userName.take(1).uppercase() else "U",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavTopColorLight
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = state.userName.ifEmpty { "Cargando..." },
                                color = TxtColorDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.userEmail.ifEmpty { "Cargando..." },
                                color = TxtColorContent,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Implementar edición de perfil */ },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BtnTextoColorLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Editar perfil", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección Preferencias
            SectionTitle("Preferencias", TxtColorDark)
            SettingsCard {
                SettingsSwitchItem(
                    icon = Icons.Rounded.NotificationsNone,
                    title = "Notificaciones",
                    isChecked = state.isNotificationsEnabled,
                    onCheckedChange = onNotificationsToggle,
                    primaryColor = NavTopColorLight
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = TxtColorDark.copy(alpha = 0.1f)
                )
                SettingsSwitchItem(
                    icon = Icons.Rounded.DarkMode,
                    title = "Modo Oscuro",
                    isChecked = state.isDarkModeEnabled,
                    onCheckedChange = onDarkModeToggle,
                    primaryColor = NavTopColorLight
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección Cuenta
            SectionTitle("Cuenta", TxtColorDark)
            SettingsCard {
                SettingsClickItem(
                    icon = Icons.Rounded.ExitToApp,
                    title = "Cerrar Sesión",
                    onClick = onLogoutClick,
                    color = Color.Red.copy(alpha = 0.7f)
                )
            }

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, color: Color) {
    Text(
        text = title,
        color = color,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(primaryColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = primaryColor)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = TxtColorDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}

@Composable
fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    color: Color = TxtColorDark
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
