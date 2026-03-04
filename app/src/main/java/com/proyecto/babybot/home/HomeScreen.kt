package com.proyecto.babybot.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.ui.theme.BackPantallas
import com.proyecto.babybot.ui.theme.BlueSkyeLight
import com.proyecto.babybot.ui.theme.BtnColorsLight
import com.proyecto.babybot.ui.theme.BtnTextoColorLight
import com.proyecto.babybot.ui.theme.LightBlueButton
import com.proyecto.babybot.ui.theme.NavTopColorLight
import com.proyecto.babybot.ui.theme.TxtColorContent
import com.proyecto.babybot.ui.theme.TxtColorDark
import com.proyecto.babybot.ui.theme.TxtColorTitle

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en HOME")
    }

    HomeContent(state = state)
}

@Composable
fun HomeContent(
    state: HomeState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackPantallas)
            .verticalScroll(rememberScrollState())
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavTopColorLight)
                .padding(24.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = state.babyName,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.babyAge,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        IconButton(onClick = {

                            Log.d("NAVIGATION", "Click en Notificaciones")
                        }) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Color.White
                            )
                        }

                        // Reduje un poco este Spacer porque IconButton ya agrega un margen táctil
                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(onClick = {

                            Log.d("NAVIGATION", "Click en Ajustes")
                        }) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Ajustes",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "Próximo: ${state.nextActivityTitle}  ${state.nextActivityTime}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                        /*Text(
                            text = state.nextActivityTime,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold
                        )*/
                    }
                }
            }
        }

        // CONTENIDO
        // REGISTRO RÁPIDO
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {

            Text(
                text = "Registro rápido",
                color = BtnTextoColorLight,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                QuickRegisterButton("Comida")
                QuickRegisterButton("Baño")
                QuickRegisterButton("Pañal")
                QuickRegisterButton("Siesta")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Registros del día",
                    color = BtnTextoColorLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                state.summary.forEach { item ->
                    SummaryItem(
                        title = item.title,
                        value = item.value
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
            Spacer(modifier = Modifier.height(4.dp))

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Actividades recientes",
                color = BtnTextoColorLight,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            state.recentActivities.forEach { activity ->
                ActivityCard(activity)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
@Composable
fun QuickRegisterButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .size(60.dp)
                .background(
                    NavTopColorLight,
                    RoundedCornerShape(16.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text.first().toString(),
                color = TxtColorDark,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = TxtColorDark
        )
    }
}

@Composable
fun SummaryItem(
    title: String,
    value: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackPantallas)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, fontSize = 12.sp, color = TxtColorTitle)
                Text(value, fontSize = 16.sp, color = TxtColorContent)
            }
        }
    }
}

@Composable
fun ActivityCard(activity: ActivityData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

                Text(
                    text = activity.title,
                    fontWeight = FontWeight.Bold,
                    color = TxtColorDark
                )
                Text(
                    text = activity.description,
                    fontSize = 12.sp,
                    color = TxtColorDark.copy(alpha = 0.7f)
                )


            Text(
                text = activity.time,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TxtColorDark
            )
        }
    }
}