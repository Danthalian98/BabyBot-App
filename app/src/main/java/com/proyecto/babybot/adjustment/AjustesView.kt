package com.proyecto.babybot.adjustment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.babybot.ui.theme.BabyBotTheme
import com.proyecto.babybot.ui.theme.BlueSkyeLight
import com.proyecto.babybot.ui.theme.HardBlueText
import com.proyecto.babybot.ui.theme.LightBlueButton

class AjustesView: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabyBotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    vAjustes(
                        name = "",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun vAjustes(name: String, modifier: Modifier = Modifier, onBackClick: () -> Unit = {}){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueSkyeLight)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Encabezado ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueSkyeLight)
                .padding(top = 24.dp, bottom = 24.dp, start = 20.dp, end = 12.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // Botón de Regresar
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Perfil
            Card (
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Info del Usuario
                        Column {
                            Text(text = "Sarah Johnson", color = HardBlueText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "sarah.j@email.com", color = HardBlueText, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Editar Perfil
                    Button (
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LightBlueButton),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Edit Profile", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Seccion del perfil
            SectionTitle("Profile", HardBlueText)
            SettingsCard {
                SettingsItem(Icons.Rounded.Person, "Parent Profile", "Sarah Johnson", HardBlueText)
                HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color(0xFFF0F0F0))
                SettingsItem(Icons.Rounded.ChildCare, "Baby Profile", "Emma Rose", HardBlueText)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seccion de preferencias
            SectionTitle("Preferencias", HardBlueText)
            SettingsCard {
                SettingsSwitchItem(Icons.Rounded.NotificationsNone, "Notifications", true, HardBlueText)
                HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color(0xFFF0F0F0))
                SettingsSwitchItem(Icons.Rounded.DarkMode, "Modo Oscuro", false, HardBlueText)
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Seccion de la cuenta
            SectionTitle("Account", HardBlueText)
            SettingsCard {
                SettingsItem(Icons.Rounded.Lock, "Privacidad y seguridad", null, HardBlueText)
                HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color(0xFFF0F0F0))
                SettingsItem(Icons.AutoMirrored.Rounded.HelpOutline, "Ayuda", null, HardBlueText)
                HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color(0xFFF0F0F0))
                SettingsItem(Icons.Rounded.Info, "Acerca de nosostros", "v1.0.0", HardBlueText)
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
fun SettingsCard(content: @Composable () -> Unit) {
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
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    primaryColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Acción */ }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Iconos de las opciones de ajuste
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF3E5F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Textos
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = LightBlueButton, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(text = subtitle, color = primaryColor.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = "",
            tint = primaryColor.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    initialState: Boolean,
    primaryColor: Color
) {
    var isChecked by remember { mutableStateOf(initialState) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF3E5F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = HardBlueText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}