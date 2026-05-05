package com.proyecto.babybot.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

enum class InputType {
    TEXT,
    EMAIL,
    PASSWORD,
    NUMBER,
    NOTES
}

@Composable
fun CustomInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    inputType: InputType = InputType.TEXT
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val keyboardOptions = when (inputType) {
        InputType.EMAIL -> KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )

        InputType.PASSWORD -> KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        )

        InputType.NUMBER -> KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        )

        InputType.NOTES -> KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default
        )

        InputType.TEXT -> KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        )
    }

    val visualTransformation =
        if (inputType == InputType.PASSWORD && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }

    val singleLine = inputType != InputType.NOTES
    val maxLines = if (inputType == InputType.NOTES) 4 else 1

    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                val filteredValue = when (inputType) {
                    InputType.NUMBER -> {
                        val normalized = newValue.replace(",", ".")
                        buildString {
                            var hasDecimalPoint = false
                            normalized.forEach { char ->
                                when {
                                    char.isDigit() -> append(char)
                                    char == '.' && !hasDecimalPoint -> {
                                        append(char)
                                        hasDecimalPoint = true
                                    }
                                }
                            }
                        }
                    }

                    InputType.NOTES -> {
                        val lines = newValue
                            .replace("\r", "")
                            .lines()
                            .take(4)

                        lines.joinToString("\n")
                    }

                    else -> {
                        newValue.replace("\n", "").replace("\r", "")
                    }
                }

                onValueChange(filteredValue)
            },
            singleLine = singleLine,
            maxLines = maxLines,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            },
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            trailingIcon = {
                if (inputType == InputType.PASSWORD) {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),

                cursorColor = MaterialTheme.colorScheme.primary,

                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,

                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}