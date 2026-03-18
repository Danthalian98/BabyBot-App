package com.proyecto.babybot.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.proyecto.babybot.navigation.Routes
import com.proyecto.babybot.ui.theme.BackPantallas
import com.proyecto.babybot.ui.theme.BtnTextoColorLight
import com.proyecto.babybot.ui.theme.NavTopColorLight
import com.proyecto.babybot.ui.theme.TxtColorContent
import com.proyecto.babybot.ui.theme.TxtColorDark
import com.proyecto.babybot.ui.theme.TxtColorTitle

@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en HOME")
    }

    // 1. Decidimos qué mostrar según el estado
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NavTopColorLight)
        }
    } else if (!state.hasBaby) {
        // Pantalla de registro si no hay bebé
        BabyRegisterContent(
            onLogoutClick = {
                viewModel.logout()// Llamamos a la instancia real del ViewModel
                // Navegamos al Login y limpiamos el historial de pantallas
                rootNavController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onSave = { name, gender, birthDate, weight, height ->
                viewModel.createBaby(name, gender, birthDate, weight, height)
            }
        )
    } else {
        // Pantalla principal si ya tiene bebé
        HomeMainContent(
            state = state,
            onLogoutClick = {
                viewModel.logout()// Llamamos a la instancia real del ViewModel
                // Navegamos al Login y limpiamos el historial de pantallas
                rootNavController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun HomeContent(
    state: HomeState,
    onLogoutClick: () -> Unit, // Recibimos la acción como parámetro
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

                        Spacer(modifier = Modifier.width(4.dp))

                        // BOTÓN DE AJUSTES / LOGOUT PROVISIONAL
                        IconButton(onClick = {
                            Log.d("NAVIGATION", "Click en Ajustes (Ejecutando Logout)")
                            onLogoutClick() // Solo ejecutamos el bloque que nos pasaron desde arriba
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
fun HomeMainContent(
    state: HomeState,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackPantallas)
            .verticalScroll(rememberScrollState())
    ) {
        // --- HEADER ---
        HomeHeader(state, onLogoutClick)

        // --- REGISTRO RÁPIDO ---
        QuickRegisterSection()

        // --- RESUMEN DEL DÍA ---
        DailySummarySection(state.summary)

        // --- ACTIVIDADES RECIENTES ---
        RecentActivitiesSection(state.recentActivities)
    }
}

@Composable
fun HomeHeader(state: HomeState, onLogoutClick: () -> Unit) {
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
                        modifier = Modifier.size(48.dp).background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = state.babyName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = state.babyAge, color = Color.White, fontSize = 12.sp)
                    }
                }
                Row {
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Filled.Settings, contentDescription = null, tint = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Banner de próxima actividad
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Próximo: ${state.nextActivityTitle}  ${state.nextActivityTime}",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun QuickRegisterSection() {
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
}

@Composable
fun DailySummarySection(summary: List<SummaryData>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Registros del día", color = BtnTextoColorLight, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            summary.forEach { item ->
                SummaryItem(title = item.title, value = item.value)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RecentActivitiesSection(activities: List<ActivityData>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Actividades recientes",
            color = BtnTextoColorLight,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Si la lista está vacía, podrías mostrar un texto opcional
        if (activities.isEmpty()) {
            Text(
                text = "No hay actividades aún",
                color = TxtColorDark.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        } else {
            activities.forEach { activity ->
                ActivityCard(activity)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyRegisterContent(
    onLogoutClick: () -> Unit,
    onSave: (String, String, Long, Double, Double) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("M") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    var birthDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    Column(modifier = Modifier.fillMaxSize()) {

        // HEADER (igual al tuyo)
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
                            Icon(Icons.Outlined.Person, contentDescription = null)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Sin bebés vinculados",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.Notifications, null, tint = Color.White)
                        }
                        IconButton(onClick = onLogoutClick) {
                            Icon(Icons.Filled.Settings, null, tint = Color.White)
                        }
                    }
                }
            }
        }

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "Registrar bebé",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fecha
                    // Fecha
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (birthDate != null) formatDate(birthDate) else "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false, // Lo desactivamos para que el clic pase al Box
                            label = { Text("Fecha de nacimiento") },
                            modifier = Modifier.fillMaxWidth(),
                            // Usamos colores de "disabled" pero que parezcan normales si prefieres
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                        // Este Box invisible captura el clic sobre toda el área del input
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Género
                    Text("Género")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GenderOption("Niño", gender == "M") { gender = "M" }
                        GenderOption("Niña", gender == "F") { gender = "F" }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Peso
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Peso (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Altura
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Altura (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Guardar
                    Button(
                        onClick = {
                            if (name.isNotBlank() && birthDate != null) {
                                onSave(
                                    name,
                                    gender,
                                    birthDate!!,
                                    weight.toDoubleOrNull() ?: 0.0,
                                    height.toDoubleOrNull() ?: 0.0
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }

    // DATE PICKER
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun GenderOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) NavTopColorLight else Color.LightGray.copy(alpha = 0.3f)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else TxtColorDark,
            fontWeight = FontWeight.Medium
        )
    }
}
