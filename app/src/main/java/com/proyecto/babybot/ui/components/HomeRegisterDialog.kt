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

    val isSaveEnabled = when (tipo) {
        "biberon" -> cantidad.isNotBlank()
        "lactancia" -> modoLactancia == "manual" && duracionManual.isNotBlank()
        "complementaria" -> alimentoDescripcion.isNotBlank()
        else -> true
    }

    LaunchedEffect(activeStartMillis) {
        if (activeStartMillis != null) {
            tipo = "lactancia"
            modoLactancia = "rapido"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Registrar alimentación")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Tipo de alimentación",
                    style = MaterialTheme.typography.labelLarge
                )

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
                        Text(
                            text = "Lado",
                            style = MaterialTheme.typography.labelLarge
                        )

                        SingleChoiceChipRow(
                            options = listOf(
                                "Izquierdo" to "izquierdo",
                                "Derecho" to "derecho",
                                "Ambos" to "ambos"
                            ),
                            selected = lado,
                            onSelected = { lado = it }
                        )

                        Text(
                            text = "Modo de registro",
                            style = MaterialTheme.typography.labelLarge
                        )

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
                                modifier = Modifier.fillMaxWidth()
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
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Finalizar")
                                    }

                                    OutlinedButton(
                                        onClick = onCancelTimer,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
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
                            Text(
                                text = "Tipo de complemento",
                                style = MaterialTheme.typography.labelLarge
                            )

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
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal
                                    ),
                                    modifier = Modifier.weight(1f)
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
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                modifier = Modifier.weight(1f)
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

                        Text(
                            text = "Contenido",
                            style = MaterialTheme.typography.labelLarge
                        )

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
                        Text(
                            text = "Tipo de alimento",
                            style = MaterialTheme.typography.labelLarge
                        )

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
                            modifier = Modifier.fillMaxWidth()
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
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                modifier = Modifier.weight(1f)
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

                        Text(
                            text = "Reacción",
                            style = MaterialTheme.typography.labelLarge
                        )

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

                Text(
                    text = "Etiquetas rápidas",
                    style = MaterialTheme.typography.labelLarge
                )

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
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
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
                },
                enabled = isSaveEnabled
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
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
                        label = { Text(label) }
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
                label = { Text(label) }
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
                        label = { Text(item.replace("_", " ")) }
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

    val scrollState = rememberScrollState()

    val quickTags = listOf(
        "color_raro",
        "olor_fuerte",
        "moco",
        "espumoso"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Registrar pañal")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Tipo de cambio",
                    style = MaterialTheme.typography.labelLarge
                )

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
                        Text(
                            text = "Color",
                            style = MaterialTheme.typography.labelLarge
                        )

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

                        Text(
                            text = "Consistencia",
                            style = MaterialTheme.typography.labelLarge
                        )

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

                    Text(
                        text = "Cantidad",
                        style = MaterialTheme.typography.labelLarge
                    )

                    SingleChoiceChipRow(
                        options = listOf(
                            "Poca" to "poca",
                            "Normal" to "normal",
                            "Mucha" to "mucha"
                        ),
                        selected = cantidad,
                        onSelected = { cantidad = it }
                    )

                    Text(
                        text = "Etiquetas rápidas",
                        style = MaterialTheme.typography.labelLarge
                    )

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
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
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
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
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

    val isSaveEnabled = if (modoRegistro == "manual") {
        duracionMin.isNotBlank()
    } else {
        false
    }

    LaunchedEffect(activeStartMillis) {
        if (activeStartMillis != null) {
            modoRegistro = "rapido"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Registrar sueño")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Modo de registro",
                    style = MaterialTheme.typography.labelLarge
                )

                SingleChoiceChipRow(
                    options = listOf(
                        "Rápido" to "rapido",
                        "Manual" to "manual"
                    ),
                    selected = modoRegistro,
                    onSelected = { modoRegistro = it }
                )

                Text(
                    text = "Tipo de sueño",
                    style = MaterialTheme.typography.labelLarge
                )

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
                        modifier = Modifier.fillMaxWidth()
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
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Finalizar")
                            }

                            OutlinedButton(
                                onClick = onCancelTimer,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                        }
                    }
                }

                Text(
                    text = "Lugar",
                    style = MaterialTheme.typography.labelLarge
                )

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

                Text(
                    text = "Calidad del sueño",
                    style = MaterialTheme.typography.labelLarge
                )

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

                Text(
                    text = "Etiquetas rápidas",
                    style = MaterialTheme.typography.labelLarge
                )

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
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
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
                },
                enabled = isSaveEnabled
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
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