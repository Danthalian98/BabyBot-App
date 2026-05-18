package com.proyecto.babybot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proyecto.babybot.data.local.model.ActivityRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.ColumnScope
import androidx.room.util.TableInfo

@Composable
fun ActivityDetailDialog(
    record: ActivityRecord,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        title = {
            ActivityDetailHeader(record = record)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (record) {
                    is ActivityRecord.Meal -> MealDetail(record)
                    is ActivityRecord.Diaper -> DiaperDetail(record)
                    is ActivityRecord.Sleep -> SleepDetail(record)
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Eliminar",
                            color = MaterialTheme.colorScheme.error,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Editar",
                            maxLines = 1
                        )
                    }
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Cerrar",
                        maxLines = 1
                    )
                }
            }
        },
        dismissButton = {}
    )
}

@Composable
private fun ActivityDetailHeader(record: ActivityRecord) {
    val header = when (record) {
        is ActivityRecord.Meal -> {
            val meal = record.meal
            ActivityHeaderInfo(
                emoji = "🍼",
                title = readableMealType(meal.tipo),
                subtitle = formatDateTime(meal.timestamp),
                badge = meal.duracionMinutos?.let { "$it min" }
                    ?: meal.cantidad?.let { "${it} ${meal.unidad.orEmpty()}".trim() }
            )
        }

        is ActivityRecord.Diaper -> {
            val diaper = record.diaper
            ActivityHeaderInfo(
                emoji = "🧷",
                title = readableDiaperType(diaper.tipo),
                subtitle = formatDateTime(diaper.timestamp),
                badge = diaper.cantidad
            )
        }

        is ActivityRecord.Sleep -> {
            val sleep = record.sleep
            ActivityHeaderInfo(
                emoji = "🌙",
                title = readableSleepType(sleep.tipo),
                subtitle = "${formatTime(sleep.inicio)} - ${formatTime(sleep.fin)}",
                badge = sleep.duracionMinutos?.let { "$it min" }
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = header.emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = header.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = header.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        header.badge?.takeIf { it.isNotBlank() }?.let {
            DetailBadge(text = it)
        }
    }
}

@Composable
private fun MealDetail(record: ActivityRecord.Meal) {
    val meal = record.meal

    DetailSection(title = "Información de comida") {
        DetailItem("Tipo", readableMealType(meal.tipo))
        DetailItem("Hora", formatDateTime(meal.timestamp))

        meal.subtipo?.takeIf { it.isNotBlank() }?.let {
            DetailItem("Subtipo", it)
        }

        meal.cantidad?.let {
            DetailItem("Cantidad", "$it ${meal.unidad.orEmpty()}".trim())
        }

        meal.duracionMinutos?.let {
            DetailItem("Duración", "$it min")
        }

        meal.lado?.takeIf { it.isNotBlank() }?.let {
            DetailItem("Lado", it)
        }

        meal.alimentoDescripcion?.takeIf { it.isNotBlank() }?.let {
            DetailItem("Alimento", it)
        }

        meal.reaccion?.takeIf { it.isNotBlank() }?.let {
            DetailItem("Reacción", it)
        }
    }

    if (meal.huboComplemento) {
        val complemento = listOfNotNull(
            meal.tipoComplemento?.takeIf { it.isNotBlank() },
            meal.cantidadComplemento?.toString(),
            meal.unidadComplemento?.takeIf { it.isNotBlank() }
        ).joinToString(" ")

        if (complemento.isNotBlank()) {
            DetailSection(title = "Complemento") {
                DetailItem("Detalle", complemento)
            }
        }
    }

    NotesSection(notes = meal.notas)
}

@Composable
private fun DiaperDetail(record: ActivityRecord.Diaper) {
    val diaper = record.diaper

    DetailSection(title = "Información del pañal") {
        DetailItem("Tipo", readableDiaperType(diaper.tipo))
        DetailItem("Hora", formatDateTime(diaper.timestamp))

        diaper.color?.takeIf { it.isNotBlank() }?.let {
            DetailItem("Color", it)
        }

        diaper.consistencia?.takeIf { it.isNotBlank() }?.let {
            DetailItem("Consistencia", it)
        }

        diaper.cantidad?.takeIf { it.isNotBlank() }?.let {
            DetailItem("Cantidad", it)
        }
    }

    NotesSection(notes = diaper.notas)
}

@Composable
private fun SleepDetail(record: ActivityRecord.Sleep) {
    val sleep = record.sleep

    DetailSection(title = "Información del sueño") {
        DetailItem("Tipo", readableSleepType(sleep.tipo))
        DetailItem("Inicio", formatDateTime(sleep.inicio))
        DetailItem("Fin", formatDateTime(sleep.fin))

        sleep.duracionMinutos?.let {
            DetailItem("Duración", "$it min")
        }

        sleep.lugar?.takeIf { it.isNotBlank() }?.let {
            DetailItem("Lugar", it)
        }

        sleep.calidad?.takeIf { it.isNotBlank() }?.let {
            DetailItem("Calidad", it)
        }
    }

    NotesSection(notes = sleep.notas)
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
            )

            content()
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.42f),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value.ifBlank { "No especificado" },
            modifier = Modifier.weight(0.58f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun NotesSection(notes: String?) {
    val cleanNotes = notes?.trim().orEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Notas",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = cleanNotes.ifBlank { "Sin notas adicionales." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class ActivityHeaderInfo(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val badge: String?
)

private fun formatDateTime(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        .format(Date(timestamp))
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault())
        .format(Date(timestamp))
}

private fun readableMealType(type: String): String {
    return when (type) {
        "lactancia" -> "Lactancia"
        "biberon" -> "Biberón"
        "complementaria" -> "Complementaria"
        else -> type.replaceFirstChar { it.uppercase() }
    }
}

private fun readableDiaperType(type: String): String {
    return when (type) {
        "pipi" -> "Pipí"
        "popo" -> "Popó"
        "ambos" -> "Pipí y popó"
        else -> type.replaceFirstChar { it.uppercase() }
    }
}

private fun readableSleepType(type: String): String {
    return when (type) {
        "siesta" -> "Siesta"
        "nocturno" -> "Sueño nocturno"
        else -> type.replaceFirstChar { it.uppercase() }
    }
}