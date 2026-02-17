package com.proyecto.babybot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Shower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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

class Historial : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabyBotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HistorialLog(
                        name = "",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

//Datos que se mostraran en el registro de las actividades
data class ActivityItem(
    val type: ActivityType,
    val title: String,
    val information: String,
    val time: String
)

//Diseño de los iconos de las actividades
enum class ActivityType(val color: Color, val icon: ImageVector){
    Comida(Color(0xFF2D9CDB), Icons.Rounded.LocalDrink),
    Pañal(Color(0xFFFFA000), Icons.Rounded.ChildCare),
    Sueño(Color(0xFF6200EE), Icons.Rounded.Bedtime),
    Baño(Color(0xFF00C853), Icons.Rounded.Shower)
}
@Composable
fun HistorialLog(name: String, modifier: Modifier = Modifier){
    //Simulacion de los datos
    val listaDeActividades = listOf(
        ActivityItem(ActivityType.Comida, "Comida", "Papilla", "12:00 AM"),
        ActivityItem(ActivityType.Pañal, "Pañal", "Desechos liquidos", "12:30 AM"),
        ActivityItem(ActivityType.Sueño, "Sueño", "1h 32min", "8:50 AM"),
        ActivityItem(ActivityType.Baño, "Baño", "Ya esta limpio", "9:24 AM")
    )

    Scaffold(
        bottomBar = { HistorialBottomNav(BlueSkyeLight) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlueSkyeLight)
                    .padding(24.dp),
            ){
                Column() {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column() {
                            Text(
                                text = "Registro Actividades",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.padding(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ResumenCard(Icons.Rounded.LocalDrink, "8", "Comidas", Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(12.dp))
                        ResumenCard(Icons.Rounded.Bedtime, "12h", "Sueño", Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(12.dp))
                        ResumenCard(Icons.Rounded.ChildCare, "8", "Pañales", Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }

            //Lista de las actividades
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item { FechaEncabezado("Hoy - 24/Febrero") }
                items(listaDeActividades) { activity ->
                    ActividadCard(activity)
                }

                item { FechaEncabezado("Hoy - 23/Febrero") }
                items(listaDeActividades) { activity ->
                    ActividadCard(activity)
                }
            }
        }
    }
}

@Composable
fun ActividadCard(activity: ActivityItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(activity.type.color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.type.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activity.title,
                        color = HardBlueText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = BlueSkyeLight,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = activity.time,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun FechaEncabezado(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =  Modifier.padding(bottom = 12.dp, top = 8.dp)
    ) {
        Icon(
            Icons.Outlined.CalendarToday,
            contentDescription = null,
            tint = HardBlueText,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color(0xFF4A2B6B),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        // Línea divisoria
        Spacer(modifier = Modifier.width(8.dp))
        HorizontalDivider(color = Color(0xFFE0E0E0), modifier = Modifier.weight(1f))
    }
}

@Composable
fun ResumenCard(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = label, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
    }
}

@Composable
fun HistorialBottomNav(activeColor: Color) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Group, contentDescription = "Community") },
            label = { Text("Community") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Rounded.Assignment, contentDescription = "Log") },
            label = { Text("Activity Log") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = activeColor,
                selectedTextColor = activeColor,
                indicatorColor = activeColor.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.ChatBubble, contentDescription = "AI") },
            label = { Text("AI Chat") },
            selected = false,
            onClick = { }
        )
    }
}