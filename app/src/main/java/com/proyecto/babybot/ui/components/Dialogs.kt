package com.proyecto.babybot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton

@Composable
fun SelectionDialog(
    title: String,
    options: List<String>,
    selectedItems: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit,
    showSearch: Boolean = false,
    searchLabel: String = "Buscar",
    multiSelect: Boolean = true
) {
    var search by remember { mutableStateOf("") }
    val tempSelection = remember(selectedItems) {
        mutableStateListOf<String>().apply {
            addAll(selectedItems)
        }
    }

    val filteredOptions = if (showSearch && search.isNotBlank()) {
        options.filter { it.contains(search, ignoreCase = true) }
    } else {
        options
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        confirmButton = {
            Button(
                onClick = { onSave(tempSelection.toList()) },
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Guardar",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                )
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (showSearch) {
                    Spacer(modifier = Modifier.height(8.dp))

                    CustomInputField(
                        label = searchLabel,
                        placeholder = "Escribe para buscar",
                        value = search,
                        onValueChange = { search = it },
                        inputType = InputType.TEXT
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(filteredOptions) { option ->
                        val selected = tempSelection.contains(option)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (multiSelect) {
                                        if (selected) {
                                            tempSelection.remove(option)
                                        } else {
                                            tempSelection.add(option)
                                        }
                                    } else {
                                        tempSelection.clear()
                                        tempSelection.add(option)
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (multiSelect) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            if (!tempSelection.contains(option)) {
                                                tempSelection.add(option)
                                            }
                                        } else {
                                            tempSelection.remove(option)
                                        }
                                    }
                                )
                            } else {
                                RadioButton(
                                    selected = selected,
                                    onClick = {
                                        tempSelection.clear()
                                        tempSelection.add(option)
                                    }
                                )
                            }

                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}