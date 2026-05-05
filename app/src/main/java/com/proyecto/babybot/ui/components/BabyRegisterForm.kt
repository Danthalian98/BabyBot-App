package com.proyecto.babybot.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.proyecto.babybot.home.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyRegisterContent(
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppSectionHeader(
            title = "Sin bebés vinculados",
            subtitle = "Registra los datos básicos del bebe",
            variant = HeaderVariant.SIMPLE,
            onNotificationsClick = onNotificationsClick,
            onSettingsClick = onSettingsClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Datos del bebé",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Esta información ayuda a personalizar los registros y resúmenes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

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
                                Text(
                                    text = "Selecciona la fecha de nacimiento",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = babyTextFieldColors()
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FormFieldLabel("Género")

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GenderOption(
                            label = "Niño",
                            selected = gender == "M",
                            modifier = Modifier.weight(1f)
                        ) { gender = "M" }

                        GenderOption(
                            label = "Niña",
                            selected = gender == "F",
                            modifier = Modifier.weight(1f)
                        ) { gender = "F" }
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

                    FormSelectionButton(
                        text = if (bloodType.isBlank()) {
                            "Seleccionar tipo de sangre"
                        } else {
                            bloodType
                        },
                        isPlaceholder = bloodType.isBlank(),
                        onClick = { showBloodTypeDialog = true }
                    )

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

                    FormSelectionButton(
                        text = if (selectedAllergies.isEmpty()) {
                            "Seleccionar alergias"
                        } else {
                            selectedAllergies.joinToString(", ")
                        },
                        isPlaceholder = selectedAllergies.isEmpty(),
                        onClick = { showAllergyDialog = true }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomInputField(
                        label = "Notas",
                        placeholder = "Escribe alguna observación",
                        value = notes,
                        onValueChange = { notes = it },
                        inputType = InputType.NOTES
                    )

                    Spacer(modifier = Modifier.height(22.dp))

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "Guardar datos",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
fun FormFieldLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
fun FormSelectionButton(
    text: String,
    isPlaceholder: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (isPlaceholder) {
                MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
            } else {
                MaterialTheme.colorScheme.primary
            }
        ),
        border = BorderStroke(
            1.dp,
            if (isPlaceholder) {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            }
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun babyTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurface,

    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = MaterialTheme.colorScheme.surface,

    cursorColor = MaterialTheme.colorScheme.primary,

    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),

    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),

    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
)