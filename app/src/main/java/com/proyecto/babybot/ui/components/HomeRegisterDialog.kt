package com.proyecto.babybot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.BorderStroke
import com.proyecto.babybot.validation.RegisterDialogValidation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults

@Composable
fun MealRegisterDialog(
    onDismiss: () -> Unit,
    onSave: (MealEntity) -> Unit,
    activeStartMillis: Long?,
    onStartTimer: (String) -> Unit,
    onFinishTimer: (
        Boolean,
        String?,
        Double?,
        String?,
        String,
        List<String>
    ) -> Unit,
    onCancelTimer: () -> Unit
) {
    var tipo by remember(activeStartMillis) {
        mutableStateOf(if (activeStartMillis != null) "lactancia" else "biberon")
    }

    // Comunes
    var notas by remember { mutableStateOf("") }
    var etiquetas by remember { mutableStateOf(setOf<String>()) }

    // Biberón
    var cantidad by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("ml") }
    var contenidoBiberon by remember { mutableStateOf("formula") }

    // Lactancia
    var lado by remember { mutableStateOf("ambos") }
    var modoLactancia by remember(activeStartMillis) {
        mutableStateOf(if (activeStartMillis != null) "rapido" else "rapido")
    }
    var duracionManual by remember { mutableStateOf("") }

    var huboComplemento by remember { mutableStateOf(false) }
    var tipoComplemento by remember { mutableStateOf("formula") }
    var cantidadComplemento by remember { mutableStateOf("") }
    var unidadComplemento by remember { mutableStateOf("ml") }

    // Complementaria
    var subtipoComplementaria by remember { mutableStateOf("papilla") }
    var alimentoDescripcion by remember { mutableStateOf("") }
    var cantidadComplementaria by remember { mutableStateOf("") }
    var unidadComplementaria by remember { mutableStateOf("g") }
    var reaccion by remember { mutableStateOf("sin_problema") }

    val quickTags = listOf(
        "se_quedo_dormido",
        "regurgito",
        "vomito",
        "pidio_mas"
    )

    val scrollState = rememberScrollState()

    val bottleAmountError =
        if (tipo == "biberon") {
            RegisterDialogValidation.validateBottleAmount(cantidad, unidad)
        } else null

    val breastfeedingDurationError =
        if (tipo == "lactancia" && modoLactancia == "manual") {
            RegisterDialogValidation.validateBreastfeedingDuration(duracionManual)
        } else null

    val complementAmountError =
        if (huboComplemento) {
            RegisterDialogValidation.validateOptionalComplementAmount(
                cantidadComplemento,
                unidadComplemento
            )
        } else null

    val foodDescriptionError =
        if (tipo == "complementaria") {
            RegisterDialogValidation.validateFoodDescription(alimentoDescripcion)
        } else null

    val complementaryAmountError =
        if (tipo == "complementaria") {
            RegisterDialogValidation.validateOptionalComplementaryAmount(
                cantidadComplementaria,
                unidadComplementaria
            )
        } else null

    val notesError = RegisterDialogValidation.validateNotes(notas)

    val isSaveEnabled = when (tipo) {
        "biberon" -> bottleAmountError == null && notesError == null

        "lactancia" -> {
            if (modoLactancia == "manual") {
                breastfeedingDurationError == null &&
                        complementAmountError == null &&
                        notesError == null
            } else {
                complementAmountError == null &&
                        notesError == null
            }
        }

        "complementaria" -> {
            foodDescriptionError == null &&
                    complementaryAmountError == null &&
                    notesError == null
        }

        else -> false
    }

    LaunchedEffect(activeStartMillis) {
        if (activeStartMillis != null) {
            tipo = "lactancia"
            modoLactancia = "rapido"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        title = {
            RegisterDialogTitle("Registrar alimentación")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(scrollState)
            ) {
                RegisterSectionLabel("Tipo de alimentación")

                if (activeStartMillis != null) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Lactancia en curso") },
                        enabled = false
                    )
                } else {
                    SingleChoiceChipRow(
                        options = listOf(
                            "Lactancia" to "lactancia",
                            "Biberón" to "biberon",
                            "Complementaria" to "complementaria"
                        ),
                        selected = tipo,
                        onSelected = { tipo = it }
                    )
                }

                when (tipo) {
                    "lactancia" -> {

                        RegisterSectionLabel("Lado")

                        SingleChoiceChipRow(
                            options = listOf(
                                "Izquierdo" to "izquierdo",
                                "Derecho" to "derecho",
                                "Ambos" to "ambos"
                            ),
                            selected = lado,
                            onSelected = { lado = it }
                        )

                        RegisterSectionLabel("Modo de registro")

                        SingleChoiceChipRow(
                            options = listOf(
                                "Rápido" to "rapido",
                                "Manual" to "manual"
                            ),
                            selected = modoLactancia,
                            onSelected = { modoLactancia = it }
                        )

                        val elapsed = rememberElapsedTime(activeStartMillis)

                        if (modoLactancia == "manual") {
                            OutlinedTextField(
                                value = duracionManual,
                                onValueChange = { duracionManual = it.filter(Char::isDigit) },
                                label = { Text("Duración en minutos") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = registerTextFieldColors(),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                isError = breastfeedingDurationError != null,
                                supportingText = {
                                    breastfeedingDurationError?.let {
                                        Text(it)
                                    }
                                }
                            )
                        } else {
                            if (activeStartMillis == null) {
                                Button(
                                    onClick = { onStartTimer(lado) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Iniciar lactancia")
                                }
                            } else {
                                Text(
                                    text = "Tiempo transcurrido: ${formatElapsed(elapsed)}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            onFinishTimer(
                                                huboComplemento,
                                                tipoComplemento,
                                                cantidadComplemento.toDoubleOrNull(),
                                                unidadComplemento,
                                                notas,
                                                etiquetas.toList()
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text(
                                            text = "Finalizar",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = onDismiss,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                        ),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            text = "Minimizar",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }

                                DialogDangerTextButton(
                                    text = "Cancelar cronómetro",
                                    onClick = onCancelTimer
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = huboComplemento,
                                onCheckedChange = { huboComplemento = it }
                            )
                            Text("Hubo complemento")
                        }

                        if (huboComplemento) {
                            RegisterSectionLabel("Tipo de complemento")

                            SingleChoiceChipRow(
                                options = listOf(
                                    "Fórmula" to "formula",
                                    "Leche extraída" to "leche_extraida"
                                ),
                                selected = tipoComplemento,
                                onSelected = { tipoComplemento = it }
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = cantidadComplemento,
                                    onValueChange = {
                                        cantidadComplemento = it.filterAllowedDecimal()
                                    },
                                    label = { Text("Cantidad") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = registerTextFieldColors(),
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    isError = complementAmountError != null,
                                    supportingText = {
                                        complementAmountError?.let {
                                            Text(it)
                                        }
                                    }
                                )

                                SingleChoiceChipRowCompact(
                                    options = listOf(
                                        "ml" to "ml",
                                        "oz" to "oz"
                                    ),
                                    selected = unidadComplemento,
                                    onSelected = { unidadComplemento = it }
                                )
                            }
                        }
                    }

                    "biberon" -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = cantidad,
                                onValueChange = { cantidad = it.filterAllowedDecimal() },
                                label = { Text("Cantidad") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = registerTextFieldColors(),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                isError = bottleAmountError != null,
                                supportingText = {
                                    bottleAmountError?.let {
                                        Text(it)
                                    }
                                }
                            )

                            SingleChoiceChipRowCompact(
                                options = listOf(
                                    "ml" to "ml",
                                    "oz" to "oz"
                                ),
                                selected = unidad,
                                onSelected = { unidad = it }
                            )
                        }

                        RegisterSectionLabel("Contenido")

                        SingleChoiceChipRow(
                            options = listOf(
                                "Fórmula" to "formula",
                                "Leche extraída" to "leche_extraida"
                            ),
                            selected = contenidoBiberon,
                            onSelected = { contenidoBiberon = it }
                        )
                    }

                    "complementaria" -> {
                        RegisterSectionLabel("Tipo de alimento")

                        SingleChoiceChipRow(
                            options = listOf(
                                "Papilla" to "papilla",
                                "Puré" to "pure",
                                "Fruta" to "fruta",
                                "Cereal" to "cereal",
                                "Otro" to "otro"
                            ),
                            selected = subtipoComplementaria,
                            onSelected = { subtipoComplementaria = it }
                        )

                        OutlinedTextField(
                            value = alimentoDescripcion,
                            onValueChange = { alimentoDescripcion = it.capitalizeWords() },
                            label = { Text("Descripción del alimento") },
                            placeholder = { Text("Ej. Manzana") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = registerTextFieldColors(),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            isError = foodDescriptionError != null,
                            supportingText = {
                                foodDescriptionError?.let {
                                    Text(it)
                                }
                            }
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = cantidadComplementaria,
                                onValueChange = {
                                    cantidadComplementaria = it.filterAllowedDecimal()
                                },
                                label = { Text("Cantidad (opcional)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = registerTextFieldColors(),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                isError = complementaryAmountError != null,
                                supportingText = {
                                    complementaryAmountError?.let {
                                        Text(it)
                                    }
                                }
                            )

                            SingleChoiceChipRowCompact(
                                options = listOf(
                                    "g" to "g",
                                    "ml" to "ml",
                                    "oz" to "oz"
                                ),
                                selected = unidadComplementaria,
                                onSelected = { unidadComplementaria = it }
                            )
                        }

                        RegisterSectionLabel("Reacción")

                        SingleChoiceChipRow(
                            options = listOf(
                                "Le gustó" to "le_gusto",
                                "Comió poco" to "comio_poco",
                                "Rechazó" to "rechazo",
                                "Sin problema" to "sin_problema"
                            ),
                            selected = reaccion,
                            onSelected = { reaccion = it }
                        )
                    }
                }

                RegisterSectionLabel("Etiquetas rápidas")

                MultiChoiceChipRow(
                    options = quickTags,
                    selected = etiquetas,
                    onToggle = { tag ->
                        etiquetas = if (tag in etiquetas) etiquetas - tag else etiquetas + tag
                    }
                )

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it.capitalizeSentences() },
                    label = { Text("Notas") },
                    placeholder = { Text("Observaciones opcionales") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(16.dp),
                    colors = registerTextFieldColors(),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    isError = notesError != null,
                    supportingText = {
                        notesError?.let {
                            Text(it)
                        }
                    }
                )
            }
        },
        confirmButton = {
            DialogPrimaryButton(
                text = "Guardar",
                enabled = isSaveEnabled,
                onClick = {
                    val now = System.currentTimeMillis()

                    val meal = when (tipo) {
                        "lactancia" -> {
                            val duracion = duracionManual.toIntOrNull()
                            val inicioCalculado =
                                if (modoLactancia == "manual" && duracion != null) {
                                    now - (duracion * 60_000L)
                                } else {
                                    null
                                }

                            MealEntity(
                                idBebe = "",
                                timestamp = now,
                                tipo = "lactancia",
                                subtipo = "pecho",
                                inicio = inicioCalculado,
                                fin = if (modoLactancia == "manual" && duracion != null) now else null,
                                duracionMinutos = duracion,
                                lado = lado,
                                huboComplemento = huboComplemento,
                                tipoComplemento = if (huboComplemento) tipoComplemento else null,
                                cantidadComplemento = if (huboComplemento) cantidadComplemento.toDoubleOrNull() else null,
                                unidadComplemento = if (huboComplemento) unidadComplemento else null,
                                notas = notas.ifBlank { null },
                                etiquetas = etiquetas.toList()
                            )
                        }

                        "complementaria" -> {
                            MealEntity(
                                idBebe = "",
                                timestamp = now,
                                tipo = "complementaria",
                                subtipo = subtipoComplementaria,
                                cantidad = cantidadComplementaria.toDoubleOrNull(),
                                unidad = if (cantidadComplementaria.isNotBlank()) unidadComplementaria else null,
                                alimentoDescripcion = alimentoDescripcion.ifBlank { null },
                                reaccion = reaccion,
                                notas = notas.ifBlank { null },
                                etiquetas = etiquetas.toList()
                            )
                        }

                        else -> {
                            MealEntity(
                                idBebe = "",
                                timestamp = now,
                                tipo = "biberon",
                                subtipo = contenidoBiberon,
                                cantidad = cantidad.toDoubleOrNull(),
                                unidad = unidad,
                                terminoTodo = null,
                                notas = notas.ifBlank { null },
                                etiquetas = etiquetas.toList()
                            )
                        }
                    }

                    onSave(meal)
                }
            )
        },
        dismissButton = {
            DialogSecondaryButton(
                text = if (activeStartMillis != null) "Minimizar" else "Cancelar",
                onClick = onDismiss
            )
        }
    )
}

@Composable
private fun SingleChoiceChipRow(
    options: List<Pair<String, String>>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { (label, value) ->
                    FilterChip(
                        selected = selected == value,
                        onClick = { onSelected(value) },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = registerChipColors()
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleChoiceChipRowCompact(
    options: List<Pair<String, String>>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (label, value) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelected(value) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = registerChipColors()
            )
        }
    }
}

@Composable
private fun MultiChoiceChipRow(
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { item ->
                    FilterChip(
                        selected = item in selected,
                        onClick = { onToggle(item) },
                        label = {
                            Text(
                                text = item.replace("_", " "),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = registerChipColors()
                    )
                }
            }
        }
    }
}

private fun String.filterAllowedDecimal(): String {
    val filtered = this.filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    return if (firstDot >= 0) {
        filtered.take(firstDot + 1) + filtered.drop(firstDot + 1).replace(".", "")
    } else {
        filtered
    }
}

private fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }
}

private fun String.capitalizeSentences(): String {
    return replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase() else char.toString()
    }
}

@Composable
fun DiaperRegisterDialog(
    onDismiss: () -> Unit,
    onSave: (DiaperEntity) -> Unit
) {
    var tipo by remember { mutableStateOf("pipi") }
    var mostrarDetalles by remember { mutableStateOf(false) }

    var color by remember { mutableStateOf("") }
    var consistencia by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("normal") }
    var notas by remember { mutableStateOf("") }
    var etiquetas by remember { mutableStateOf(setOf<String>()) }
    val notesError = RegisterDialogValidation.validateNotes(notas)
    val isSaveEnabled = notesError == null

    val scrollState = rememberScrollState()

    val quickTags = listOf(
        "color_raro",
        "olor_fuerte",
        "espumoso"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        title = {
            RegisterDialogTitle("Registrar pañal")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(scrollState)
            ) {
                RegisterSectionLabel("Tipo de cambio")

                SingleChoiceChipRow(
                    options = listOf(
                        "Pipí" to "pipi",
                        "Popó" to "popo",
                        "Ambos" to "ambos"
                    ),
                    selected = tipo,
                    onSelected = { tipo = it }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = mostrarDetalles,
                        onCheckedChange = { mostrarDetalles = it }
                    )
                    Text("Agregar más detalles")
                }

                if (mostrarDetalles) {
                    if (tipo == "popo" || tipo == "ambos") {
                        RegisterSectionLabel("Color")

                        SingleChoiceChipRow(
                            options = listOf(
                                "Amarillo" to "amarillo",
                                "Café" to "cafe",
                                "Verde" to "verde",
                                "Negro" to "negro"
                            ),
                            selected = color,
                            onSelected = { color = it }
                        )

                        RegisterSectionLabel("Consistencia")

                        SingleChoiceChipRow(
                            options = listOf(
                                "Líquida" to "liquida",
                                "Normal" to "normal",
                                "Espesa" to "espesa"
                            ),
                            selected = consistencia,
                            onSelected = { consistencia = it }
                        )
                    }

                    RegisterSectionLabel("Cantidad")

                    SingleChoiceChipRow(
                        options = listOf(
                            "Poca" to "poca",
                            "Normal" to "normal",
                            "Mucha" to "mucha"
                        ),
                        selected = cantidad,
                        onSelected = { cantidad = it }
                    )

                    RegisterSectionLabel("Etiquetas rápidas")

                    MultiChoiceChipRow(
                        options = quickTags,
                        selected = etiquetas,
                        onToggle = { tag ->
                            etiquetas = if (tag in etiquetas) {
                                etiquetas - tag
                            } else {
                                etiquetas + tag
                            }
                        }
                    )
                }

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it.capitalizeSentences() },
                    label = { Text("Notas") },
                    placeholder = { Text("Observaciones opcionales") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(16.dp),
                    colors = registerTextFieldColors(),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    isError = notesError != null,
                    supportingText = {
                        notesError?.let {
                            Text(it)
                        }
                    }
                )
            }
        },
        confirmButton = {
            DialogPrimaryButton(
                text = "Guardar",
                enabled = isSaveEnabled,
                onClick = {
                    onSave(
                        DiaperEntity(
                            idBebe = "",
                            timestamp = System.currentTimeMillis(),
                            tipo = tipo,
                            color = color.ifBlank { null },
                            consistencia = consistencia.ifBlank { null },
                            cantidad = if (mostrarDetalles) cantidad else null,
                            notas = notas.ifBlank { null },
                            etiquetas = etiquetas.toList()
                        )
                    )
                }
            )
        },
        dismissButton = {
            DialogSecondaryButton(
                text = "Cancelar",
                onClick = onDismiss
            )
        }
    )
}

@Composable
fun SleepRegisterDialog(
    onDismiss: () -> Unit,
    onSave: (SleepEntity) -> Unit,
    activeStartMillis: Long?,
    onStartTimer: (String) -> Unit,
    onFinishTimer: (String, String, String, List<String>) -> Unit,
    onCancelTimer: () -> Unit
) {
    var modoRegistro by remember(activeStartMillis) {
        mutableStateOf("rapido")
    }
    var tipo by remember { mutableStateOf("siesta") }
    var duracionMin by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("cuna") }
    var calidad by remember { mutableStateOf("durmio_tranquilo") }
    var notas by remember { mutableStateOf("") }
    var etiquetas by remember { mutableStateOf(setOf<String>()) }

    val scrollState = rememberScrollState()

    val quickTags = listOf(
        "lloro_antes_de_dormir",
        "desperto_antes",
        "necesito_brazos"
    )

    val sleepDurationError =
        if (modoRegistro == "manual") {
            RegisterDialogValidation.validateSleepDuration(duracionMin)
        } else null

    val notesError = RegisterDialogValidation.validateNotes(notas)

    val isSaveEnabled = modoRegistro == "manual" &&
            sleepDurationError == null &&
            notesError == null

    LaunchedEffect(activeStartMillis) {
        if (activeStartMillis != null) {
            modoRegistro = "rapido"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        title = {
            RegisterDialogTitle("Registrar sueño")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(scrollState)
            ) {
                RegisterSectionLabel("Modo de registro")

                SingleChoiceChipRow(
                    options = listOf(
                        "Rápido" to "rapido",
                        "Manual" to "manual"
                    ),
                    selected = modoRegistro,
                    onSelected = { modoRegistro = it }
                )

                RegisterSectionLabel("Tipo de sueño")

                SingleChoiceChipRow(
                    options = listOf(
                        "Siesta" to "siesta",
                        "Nocturno" to "nocturno"
                    ),
                    selected = tipo,
                    onSelected = { tipo = it }
                )

                val elapsed = rememberElapsedTime(activeStartMillis)

                if (modoRegistro == "manual") {
                    OutlinedTextField(
                        value = duracionMin,
                        onValueChange = { duracionMin = it.filter(Char::isDigit) },
                        label = { Text("Duración en minutos") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = registerTextFieldColors(),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        isError = sleepDurationError != null,
                        supportingText = {
                            sleepDurationError?.let {
                                Text(it)
                            }
                        }
                    )
                } else {
                    if (activeStartMillis == null) {
                        Button(
                            onClick = { onStartTimer(tipo) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Iniciar sueño")
                        }
                    } else {
                        Text(
                            text = "Tiempo transcurrido: ${formatElapsed(elapsed)}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    onFinishTimer(
                                        lugar,
                                        calidad,
                                        notas,
                                        etiquetas.toList()
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Finalizar",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = "Minimizar",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        DialogDangerTextButton(
                            text = "Cancelar cronómetro",
                            onClick = onCancelTimer
                        )
                    }
                }

                RegisterSectionLabel("Lugar")

                SingleChoiceChipRow(
                    options = listOf(
                        "Cuna" to "cuna",
                        "Brazos" to "brazos",
                        "Carriola" to "carriola",
                        "Cama" to "cama"
                    ),
                    selected = lugar,
                    onSelected = { lugar = it }
                )

                RegisterSectionLabel("Calidad del sueño")

                SingleChoiceChipRow(
                    options = listOf(
                        "Tranquilo" to "durmio_tranquilo",
                        "Inquieto" to "inquieto",
                        "Despertó mucho" to "desperto_varias_veces",
                        "Costó dormirlo" to "costo_dormirlo"
                    ),
                    selected = calidad,
                    onSelected = { calidad = it }
                )

                RegisterSectionLabel("Etiquetas rápidas")

                MultiChoiceChipRow(
                    options = quickTags,
                    selected = etiquetas,
                    onToggle = { tag ->
                        etiquetas = if (tag in etiquetas) {
                            etiquetas - tag
                        } else {
                            etiquetas + tag
                        }
                    }
                )

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it.capitalizeSentences() },
                    label = { Text("Notas") },
                    placeholder = { Text("Observaciones opcionales") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(16.dp),
                    colors = registerTextFieldColors(),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    isError = notesError != null,
                    supportingText = {
                        notesError?.let {
                            Text(it)
                        }
                    }
                )
            }
        },
        confirmButton = {
            DialogPrimaryButton(
                text = "Guardar",
                enabled = isSaveEnabled,
                onClick = {
                    val now = System.currentTimeMillis()
                    val duration = duracionMin.toIntOrNull() ?: 0
                    val start = now - (duration * 60 * 1000L)

                    onSave(
                        SleepEntity(
                            idBebe = "",
                            inicio = start,
                            fin = now,
                            duracionMinutos = duration,
                            tipo = tipo,
                            calidad = calidad.ifBlank { null },
                            lugar = lugar.ifBlank { null },
                            notas = notas.ifBlank { null },
                            etiquetas = etiquetas.toList()
                        )
                    )
                }
            )
        },
        dismissButton = {
            DialogSecondaryButton(
                text = if (activeStartMillis != null) "Minimizar" else "Cancelar",
                onClick = onDismiss
            )
        }
    )
}

@Composable
private fun rememberElapsedTime(startMillis: Long?): Long {
    var elapsed by remember(startMillis) { mutableStateOf(0L) }

    LaunchedEffect(startMillis) {
        while (startMillis != null) {
            elapsed = System.currentTimeMillis() - startMillis
            kotlinx.coroutines.delay(1000)
        }
    }

    return elapsed
}

private fun formatElapsed(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Composable
private fun RegisterDialogTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun RegisterSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun registerTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,

    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f),

    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f),

    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),

    cursorColor = MaterialTheme.colorScheme.primary
)

@Composable
private fun registerChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = MaterialTheme.colorScheme.surfaceVariant,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,

    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.primary,

    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
)

@Composable
private fun RegisterDialogConfirmButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun RegisterDialogCancelButton(
    text: String = "Cancelar",
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DialogPrimaryButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun DialogSecondaryButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun DialogDangerTextButton(
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}