package com.proyecto.babybot.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.text.KeyboardOptions
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

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NavTopColorLight)
        }
    } else if (!state.hasBaby) {
        BabyRegisterContent(
            onLogoutClick = {
                viewModel.logout()
                rootNavController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onSave = { name, gender, birthDate, weight, height, bloodType, pediatrician, notes, allergies ->
                viewModel.createBaby(
                    name = name,
                    gender = gender,
                    birthDate = birthDate,
                    weight = weight,
                    height = height,
                    bloodType = bloodType,
                    pediatrician = pediatrician,
                    notes = notes,
                    allergies = allergies
                )
            }
        )
    } else {
        HomeMainContent(
            state = state,
            onLogoutClick = {
                viewModel.logout()
                rootNavController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
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
        HomeHeader(state, onLogoutClick)
        QuickRegisterSection()
        DailySummarySection(state.summary)
        RecentActivitiesSection(state.recentActivities)
    }
}

@Composable
fun HomeHeader(
    state: HomeState,
    onLogoutClick: () -> Unit
) {
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

                Row {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        border = BorderStroke(1.dp, Color(0xFF7EA1FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Registro del día",
                color = BtnTextoColorLight,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            summary.forEach { item ->
                SummaryItem(title = item.title, value = item.value)
                Spacer(modifier = Modifier.height(12.dp))
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
        modifier = Modifier.width(70.dp)
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
    Box {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    color = TxtColorContent
                )
            }
        }

        Text(
            text = title,
            modifier = Modifier
                .padding(start = 14.dp)
                .offset(y = (-10).dp)
                .background(Color(0xFFF8F8F8)),
            fontSize = 12.sp,
            color = TxtColorTitle
        )
    }
}

@Composable
fun ActivityCard(activity: ActivityData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        border = BorderStroke(1.dp, Color(0xFFE2DDEA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = activity.icon,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.title,
                    fontSize = 16.sp,
                    color = TxtColorDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = activity.description,
                    fontSize = 14.sp,
                    color = TxtColorDark.copy(alpha = 0.75f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = activity.time,
                fontSize = 14.sp,
                color = TxtColorDark
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyRegisterContent(
    onLogoutClick: () -> Unit,
    onSave: (
        String,
        String,
        Long,
        Double,
        Double,
        String,
        String,
        String,
        List<String>
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("M") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var pediatrician by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var birthDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAllergyDialog by remember { mutableStateOf(false) }

    val selectedAllergies = remember { mutableStateListOf<String>() }
    val datePickerState = rememberDatePickerState()

    val alergiasBase = listOf(
        "Leche",
        "Huevo",
        "Maní",
        "Nueces",
        "Soya",
        "Trigo",
        "Pescado",
        "Mariscos",
        "Polen",
        "Polvo",
        "Medicamentos",
        "Picaduras de insectos"
    )

    Column(modifier = Modifier.fillMaxSize()) {
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
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onLogoutClick) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                        text = "Registrar bebé",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (birthDate != null) formatDate(birthDate) else "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Fecha de nacimiento") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Género")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GenderOption("Niño", gender == "M") { gender = "M" }
                        GenderOption("Niña", gender == "F") { gender = "F" }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Peso (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Talla (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = bloodType,
                        onValueChange = { bloodType = it },
                        label = { Text("Tipo de sangre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pediatrician,
                        onValueChange = { pediatrician = it },
                        label = { Text("Pediatra") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Alergias",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = { showAllergyDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (selectedAllergies.isEmpty()) {
                                "Seleccionar alergias"
                            } else {
                                selectedAllergies.joinToString(", ")
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notas") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank() && birthDate != null) {
                                onSave(
                                    name,
                                    gender,
                                    birthDate!!,
                                    weight.toDoubleOrNull() ?: 0.0,
                                    height.toDoubleOrNull() ?: 0.0,
                                    bloodType,
                                    pediatrician,
                                    notes,
                                    selectedAllergies.toList()
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

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        birthDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
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

    if (showAllergyDialog) {
        var search by remember { mutableStateOf("") }

        val filteredAllergies = alergiasBase.filter {
            it.contains(search, ignoreCase = true)
        }

        AlertDialog(
            onDismissRequest = { showAllergyDialog = false },
            confirmButton = {
                TextButton(onClick = { showAllergyDialog = false }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAllergyDialog = false }) {
                    Text("Cancelar")
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        label = { Text("Buscar alergia") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(filteredAllergies) { alergia ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (selectedAllergies.contains(alergia)) {
                                            selectedAllergies.remove(alergia)
                                        } else {
                                            selectedAllergies.add(alergia)
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedAllergies.contains(alergia),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            if (!selectedAllergies.contains(alergia)) {
                                                selectedAllergies.add(alergia)
                                            }
                                        } else {
                                            selectedAllergies.remove(alergia)
                                        }
                                    }
                                )
                                Text(text = alergia)
                            }
                        }
                    }
                }
            }
        )
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
                if (selected) NavTopColorLight
                else Color.LightGray.copy(alpha = 0.3f)
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