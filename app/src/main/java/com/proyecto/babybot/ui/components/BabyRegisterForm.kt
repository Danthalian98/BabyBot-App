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
import com.proyecto.babybot.validation.BabyField
import com.proyecto.babybot.validation.BabyValidation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyRegisterContent(
    title: String = "Sin bebés vinculados",
    subtitle: String = "Registra los datos necesarios del bebé",
    description: String = "Los campos marcados con * son necesarios para crear el perfil del bebé.",
    buttonText: String = "Guardar datos",

    initialName: String = "",
    initialGender: String = "M",
    initialBirthDate: Long? = null,
    initialWeight: String = "",
    initialHeight: String = "",
    initialBloodType: String = "",
    initialPediatrician: String = "",
    initialNotes: String = "",
    initialAllergies: List<String> = emptyList(),

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
    var name by remember(initialName) { mutableStateOf(initialName) }
    var gender by remember(initialGender) { mutableStateOf(initialGender.ifBlank { "M" }) }
    var weight by remember(initialWeight) { mutableStateOf(initialWeight) }
    var height by remember(initialHeight) { mutableStateOf(initialHeight) }
    var bloodType by remember(initialBloodType) { mutableStateOf(initialBloodType) }
    var pediatrician by remember(initialPediatrician) { mutableStateOf(initialPediatrician) }
    var notes by remember(initialNotes) { mutableStateOf(initialNotes) }

    var birthDate by remember(initialBirthDate) { mutableStateOf<Long?>(initialBirthDate) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showAllergyDialog by remember { mutableStateOf(false) }

    val selectedAllergies = remember(initialAllergies) {
        mutableStateListOf<String>().apply {
            addAll(initialAllergies)
        }
    }
    val datePickerState = rememberDatePickerState()

    var showBloodTypeDialog by remember { mutableStateOf(false) }
    var validationErrors by remember {
        mutableStateOf<Map<BabyField, String>>(emptyMap())
    }

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
            title = title,
            subtitle = subtitle,
            variant = HeaderVariant.SIMPLE,
            showNotifications = false,
            showSettings = false
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
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    CustomInputField(
                        label = "Nombre *",
                        placeholder = "Ingresa el nombre del bebé",
                        value = name,
                        onValueChange = {
                            name = it
                            validationErrors = validationErrors - BabyField.NAME
                        },
                        inputType = InputType.TEXT
                    )

                    FieldError(validationErrors[BabyField.NAME])

                    Spacer(modifier = Modifier.height(12.dp))

                    FormFieldLabel("Fecha de nacimiento *")

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
                    FieldError(validationErrors[BabyField.BIRTH_DATE])

                    Spacer(modifier = Modifier.height(12.dp))

                    FormFieldLabel("Género *")
                    FieldError(validationErrors[BabyField.GENDER])

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
                        label = "Peso (kg) *",
                        placeholder = "Ej. 3.200",
                        value = weight,
                        onValueChange = { newValue ->
                            if (isValidWeightInput(newValue)) {
                                weight = newValue
                                validationErrors = validationErrors - BabyField.WEIGHT
                            }
                        },
                        inputType = InputType.NUMBER
                    )

                    FieldError(validationErrors[BabyField.WEIGHT])

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomInputField(
                        label = "Talla (cm) *",
                        placeholder = "Ej. 50",
                        value = height,
                        onValueChange = { newValue ->
                            if (isValidHeightInput(newValue)) {
                                height = newValue
                                validationErrors = validationErrors - BabyField.HEIGHT
                            }
                        },
                        inputType = InputType.NUMBER
                    )

                    FieldError(validationErrors[BabyField.HEIGHT])

                    Spacer(modifier = Modifier.height(12.dp))

                    FormFieldLabel("Tipo de sangre")
                    FieldError(validationErrors[BabyField.BLOOD_TYPE])

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
                            val result = BabyValidation.validateBabyData(
                                name = name,
                                gender = gender,
                                birthDateMillis = birthDate,
                                weightText = weight,
                                heightText = height,
                                bloodType = bloodType,
                                requireBloodType = false
                            )

                            validationErrors = result.errors

                            if (result.isValid) {
                                onSave(
                                    name.trim(),
                                    gender,
                                    birthDate!!,
                                    BabyValidation.parseDecimal(weight) ?: 0.0,
                                    BabyValidation.parseDecimal(height) ?: 0.0,
                                    bloodType.trim(),
                                    pediatrician.trim(),
                                    notes.trim(),
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
                            text = buttonText,
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

private fun isValidWeightInput(value: String): Boolean {
    if (value.isBlank()) return true

    val normalizedValue = value.replace(",", ".")

    // Máximo 2 dígitos antes del punto y máximo 3 después
    val weightRegex = Regex("^\\d{0,2}(\\.\\d{0,3})?$")

    if (!weightRegex.matches(normalizedValue)) return false

    val weight = normalizedValue.toDoubleOrNull()

    return weight == null || weight <= 25.0
}

private fun isValidHeightInput(value: String): Boolean {
    if (value.isBlank()) return true

    // Máximo 3 dígitos
    val heightRegex = Regex("^\\d{0,3}$")

    if (!heightRegex.matches(value)) return false

    val height = value.toIntOrNull()

    return height == null || height <= 120
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

@Composable
fun FieldError(message: String?) {
    if (!message.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}