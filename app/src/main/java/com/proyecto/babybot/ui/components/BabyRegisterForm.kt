package com.proyecto.babybot.ui.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.babybot.home.formatDate
import com.proyecto.babybot.ui.theme.BtnTextoColorLight
import com.proyecto.babybot.ui.theme.NavTopColorLight
import com.proyecto.babybot.ui.theme.TxtColorDark

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

    var showBloodTypeDialog by remember { mutableStateOf(false) }

    val bloodTypes = listOf(
        "O+",
        "O-",
        "A+",
        "A-",
        "B+",
        "B-",
        "AB+",
        "AB-"
    )

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
        AppSectionHeader(
            title = "Sin bebés vinculados",
            variant = HeaderVariant.HOME,
            onNotificationsClick = { },
            onSettingsClick = onLogoutClick
        )

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

                    CustomInputField(
                        label = "Nombre",
                        placeholder = "Ingresa el nombre del bebé",
                        value = name,
                        onValueChange = { name = it },
                        inputType = InputType.TEXT
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormFieldLabel("Fecha de nacimiento")

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (birthDate != null) formatDate(birthDate) else "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            placeholder = {
                                Text("Selecciona la fecha de nacimiento")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = Color(0xFFB8C3D1),
                                disabledPlaceholderColor = Color(0xFF9E9E9E),
                                disabledContainerColor = Color.White
                            )
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FormFieldLabel("Género")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GenderOption("Niño", gender == "M") { gender = "M" }
                        GenderOption("Niña", gender == "F") { gender = "F" }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomInputField(
                        label = "Peso (kg)",
                        placeholder = "Ej. 3.200",
                        value = weight,
                        onValueChange = { newValue ->
                            weight = newValue
                        },
                        inputType = InputType.NUMBER
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomInputField(
                        label = "Talla (cm)",
                        placeholder = "Ej. 50",
                        value = height,
                        onValueChange = { newValue ->
                            height = newValue
                        },
                        inputType = InputType.NUMBER
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormFieldLabel("Tipo de sangre")

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = { showBloodTypeDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (bloodType.isBlank()) {
                                "Seleccionar tipo de sangre"
                            } else {
                                bloodType
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomInputField(
                        label = "Pediatra",
                        placeholder = "Nombre del pediatra",
                        value = pediatrician,
                        onValueChange = { pediatrician = it },
                        inputType = InputType.TEXT
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormFieldLabel("Alergias")

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

                    CustomInputField(
                        label = "Notas",
                        placeholder = "Escribe alguna observación",
                        value = notes,
                        onValueChange = { notes = it },
                        inputType = InputType.NOTES
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
        SelectionDialog(
            title = "Seleccionar alergias",
            options = alergiasBase,
            selectedItems = selectedAllergies.toList(),
            onDismiss = { showAllergyDialog = false },
            onSave = { updatedSelection ->
                selectedAllergies.clear()
                selectedAllergies.addAll(updatedSelection)
                showAllergyDialog = false
            },
            showSearch = true,
            searchLabel = "Buscar alergia",
            multiSelect = true
        )
    }

    if (showBloodTypeDialog) {
        SelectionDialog(
            title = "Seleccionar tipo de sangre",
            options = bloodTypes,
            selectedItems = if (bloodType.isBlank()) emptyList() else listOf(bloodType),
            onDismiss = { showBloodTypeDialog = false },
            onSave = { updatedSelection ->
                bloodType = updatedSelection.firstOrNull().orEmpty()
                showBloodTypeDialog = false
            },
            showSearch = false,
            multiSelect = false
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

@Composable
fun FormFieldLabel(text: String) {
    Text(
        text = text,
        color = BtnTextoColorLight,
        style = MaterialTheme.typography.labelLarge
    )
}