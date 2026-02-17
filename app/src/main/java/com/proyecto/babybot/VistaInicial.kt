package com.proyecto.babybot

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Shower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

class VistaInicial: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabyBotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VInicial(
                        name = "",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun VInicial(name: String, modifier: Modifier = Modifier){
    Scaffold(
        bottomBar = {BottomNavigation(BlueSkyeLight)},
        containerColor = LightBlueButton
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            //Apartado del encabezado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(BlueSkyeLight)
                    .padding(24.dp)
            ) {
                Column() {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ){
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column() {
                                Text(
                                    text = "Nombre del Bebé",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "3 meses",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    //Actividades proximas (aun es parte del encabezado)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ){
                        Column() {
                            Text(
                                text = "Proximo: Alimentar al bebé",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "12:00 AM",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            //Apartado principal de la vista
            Column(modifier = Modifier.padding(16.dp)) {
                //Registro de las actividades
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton("Comida", Icons.Rounded.LocalDrink, HardBlueText)
                        QuickActionButton("Pañal", Icons.Rounded.ChildCare, HardBlueText)
                        QuickActionButton("Sueño", Icons.Rounded.Bedtime, HardBlueText)
                        QuickActionButton("Baño", Icons.Rounded.Shower, HardBlueText)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                //Apartado de los registros del dia
                Text(
                    text = "Regostros del dia",
                    color = HardBlueText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SummaryItem("¿Cuanto comio hoy?", "3 veces", BlueSkyeLight)
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryItem("¿Cuanto durmio hoy?", "4 horas", HardBlueText)
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryItem("¿Cuanto se cambio el pañal hoy?", "2 veces", HardBlueText)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                //Apartado del registro de todas las actividades
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Actividades recientes",
                        color = HardBlueText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(LightBlueButton, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.LocalDrink, contentDescription = null, tint = Color.White)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.width(1.dp)) {
                            Text(
                                text = "Comida",
                                color = HardBlueText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "120ml",
                                fontSize = 12.sp,
                                color = HardBlueText
                            )
                        }
                        Text(
                            text = "10:30AM",
                            fontSize = 12.sp,
                            color = HardBlueText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(text: String, icon: ImageVector, color: Color){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, fontSize = 12.sp, color = BlueSkyeLight)
    }
}

@Composable
fun SummaryItem(title: String, value: String, bgColor: Color){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Text(text = title, fontSize = 12.sp, color = HardBlueText)
            Text(text = value, fontSize = 16.sp, color = LightBlueButton)
        }
    }
}

@Composable
fun BottomNavigation(activeColor: Color){
    NavigationBar(
        containerColor = BlueSkyeLight,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {Icon(Icons.Rounded.Home, contentDescription = "Inicio")},
            label = {Text("Inicio")},
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(selectedIconColor = activeColor, indicatorColor = activeColor.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            icon = {Icon(Icons.Rounded.Groups, contentDescription = "Foros")},
            label = {Text("Foros")},
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = {Icon(Icons.AutoMirrored.Rounded.Assignment, contentDescription = "Registros")},
            label = {Text("Registros")},
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = {Icon(Icons.Rounded.ChatBubble, contentDescription = "IA Chat")},
            label = {Text("IA Chat")},
            selected = false,
            onClick = {}
        )
    }
}