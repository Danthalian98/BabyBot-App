package com.proyecto.babybot.settings.account.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.ui.components.BabyRegisterContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import com.proyecto.babybot.ui.components.AppSectionHeader

@Composable
fun EditAccountInfoScreen(
    mode: Int,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EditAccountInfoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) {
            onSaved()
        }
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        mode == 0 -> {
            EditUserForm(
                name = state.userName,
                email = state.userEmail,
                licenseType = state.licenseType,
                licenseStatus = state.licenseStatus,
                licenseExpirationDateMillis = state.licenseExpirationDateMillis,
                isSaving = state.isSaving,
                onBack = onBack,
                onSave = viewModel::updateUserName
            )
        }

        mode == 1 -> {
            val baby = state.baby

            if (baby == null) {
                EmptyBabyEditState(onBack = onBack)
            } else {
                BabyRegisterContent(
                    title = "Editar bebé",
                    subtitle = "Actualiza los datos del bebé",
                    description = "Corrige la información registrada para mantener recomendaciones y resúmenes más confiables.",
                    buttonText = if (state.isSaving) "Guardando..." else "Guardar cambios",
                    initialName = baby.nombre,
                    initialGender = baby.genero,
                    initialBirthDate = baby.fechaNacimiento,
                    initialWeight = baby.peso.toString(),
                    initialHeight = baby.talla.toString(),
                    initialBloodType = baby.tipoSangre,
                    initialPediatrician = baby.pediatra,
                    initialNotes = baby.notas,
                    initialAllergies = baby.alergias,
                    onNotificationsClick = {},
                    onSettingsClick = onBack,
                    onSave = { name, gender, birthDate, weight, height, bloodType, pediatrician, notes, allergies ->
                        viewModel.updateBaby(
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
            }
        }

        else -> {
            EmptyBabyEditState(onBack = onBack)
        }
    }
}

@Composable
private fun EditUserForm(
    name: String,
    email: String,
    licenseType: String,
    licenseStatus: String,
    licenseExpirationDateMillis: Long,
    isSaving: Boolean,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var userName by remember(name) { mutableStateOf(name) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        AppSectionHeader(
            title = "Perfil",
            subtitle = "Tu cuenta y licencia",
            showSettings = false,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ProfileNameCard(
                userName = userName,
                onUserNameChange = { userName = it },
                email = email
            )

            Spacer(modifier = Modifier.height(16.dp))

            LicenseInfoCard(
                licenseType = licenseType,
                licenseStatus = licenseStatus,
                expirationDateMillis = licenseExpirationDateMillis
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(userName) },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = if (isSaving) "Guardando..." else "Guardar cambios",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
private fun EmptyBabyEditState(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No se encontró información del bebé.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

@Composable
private fun LicenseInfoCard(
    licenseType: String,
    licenseStatus: String,
    expirationDateMillis: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estado de la licencia",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoRow(
                label = "Tipo",
                value = licenseType.ifBlank { "No disponible" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileInfoRow(
                label = "Estado",
                value = formatLicenseStatus(
                    licenseStatus = licenseStatus,
                    licenseType = licenseType
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileInfoRow(
                label = "Vigencia",
                value = formatLicenseDate(expirationDateMillis)
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatLicenseStatus(
    licenseStatus: String,
    licenseType: String
): String {
    return when {
        licenseType.equals("Premium", ignoreCase = true) -> "Activa"
        licenseType.equals("Prueba gratuita", ignoreCase = true) -> "Activa"
        licenseType.equals("Vencida", ignoreCase = true) -> "Vencida"
        licenseStatus.equals("TRIAL", ignoreCase = true) -> "Prueba gratuita"
        licenseStatus.equals("PREMIUM", ignoreCase = true) -> "Premium activa"
        licenseStatus.equals("VENCIDA", ignoreCase = true) -> "Vencida"
        licenseStatus.equals("NO_LICENSE", ignoreCase = true) -> "Sin licencia"
        else -> "No disponible"
    }
}

private fun formatLicenseDate(
    expirationDateMillis: Long
): String {
    if (expirationDateMillis <= 0L) {
        return "No disponible"
    }

    val formatter = java.text.SimpleDateFormat(
        "dd/MM/yyyy",
        java.util.Locale.getDefault()
    )

    return formatter.format(java.util.Date(expirationDateMillis))
}

@Composable
private fun ProfileNameCard(
    userName: String,
    onUserNameChange: (String) -> Unit,
    email: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Información personal",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Este nombre se mostrará dentro de BabyBot.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = userName,
                onValueChange = onUserNameChange,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Correo",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email.ifBlank { "No disponible" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}