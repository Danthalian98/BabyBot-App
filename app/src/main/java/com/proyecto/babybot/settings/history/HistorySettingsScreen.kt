package com.proyecto.babybot.settings.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun HistorySettingsScreen(
    onBack: () -> Unit,
    viewModel: HistorySettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::hideDeleteDialog,
            title = {
                Text(text = "Eliminar historial")
            },
            text = {
                Text(text = "¿Seguro que quieres eliminar todas las conversaciones guardadas del chatbot?")
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::deleteHistory
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = viewModel::hideDeleteDialog
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    state.selectedConversation?.let { conversation ->
        ConversationDetailDialog(
            conversation = conversation,
            onClose = viewModel::hideConversation,
            onDelete = viewModel::deleteSelectedConversation,
            onCopy = {
                clipboardManager.setText(
                    AnnotatedString(conversation.copyText)
                )

                Toast
                    .makeText(
                        context,
                        "Conversación copiada",
                        Toast.LENGTH_SHORT
                    )
                    .show()

                viewModel.hideConversation()
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        HistorySettingsContent(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            state = state,
            onBack = onBack,
            onDeleteHistory = viewModel::showDeleteDialog,
            onConversationClick = viewModel::showConversation
        )
    }
}

@Composable
private fun HistorySettingsContent(
    modifier: Modifier = Modifier,
    state: HistorySettingsState,
    onBack: () -> Unit,
    onDeleteHistory: () -> Unit,
    onConversationClick: (ChatHistoryItemUi) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HistoryHeader(onBack = onBack)

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    HistoryInfoCard()
                    Spacer(modifier = Modifier.height(2.dp))
                }

                if (state.conversations.isEmpty()) {
                    item {
                        EmptyHistoryCard()
                    }
                } else {
                    items(
                        items = state.conversations,
                        key = { it.id }
                    ) { conversation ->
                        ChatHistoryCard(
                            conversation = conversation,
                            onClick = {
                                onConversationClick(conversation)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onDeleteHistory
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            Text(text = "Eliminar todo el historial")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryHeader(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowLeft,
                contentDescription = "Regresar",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Historial del chatbot",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "Revisa, copia o elimina conversaciones anteriores",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
            )
        }
    }
}

@Composable
private fun HistoryInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SoftIconBox(
                icon = Icons.Outlined.History,
                tint = Color(0xFF5DADE2)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                Text(
                    text = "Conversaciones guardadas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Toca una conversación para verla completa y copiarla si quieres retomarla en el chatbot.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                )
            }
        }
    }
}

@Composable
private fun ChatHistoryCard(
    conversation: ChatHistoryItemUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            SoftIconBox(
                icon = Icons.Outlined.Chat,
                tint = Color(0xFF6D8FF2)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = conversation.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = conversation.date,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = conversation.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                AssistChip(
                    onClick = onClick,
                    label = {
                        Text(text = "${conversation.messageCount} mensajes")
                    },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SoftIconBox(
                icon = Icons.Outlined.Chat,
                tint = Color(0xFF5DADE2),
                size = 56
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Aún no hay historial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Cuando converses con BabyBot, tus consultas aparecerán aquí.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConversationDetailDialog(
    conversation: ChatHistoryItemUi,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.86f),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SoftIconBox(
                        icon = Icons.Outlined.Chat,
                        tint = Color(0xFF6D8FF2)
                    )

                    Spacer(modifier = Modifier.size(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Conversación",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${conversation.date} · ${conversation.messageCount} mensajes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = conversation.messages,
                        key = { it.id }
                    ) { message ->
                        ConversationMessageBubble(message = message)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDelete
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            Text("Eliminar")
                        }

                        FilledTonalButton(
                            onClick = onCopy
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            Text("Copiar")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onClose
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationMessageBubble(
    message: ChatMessageUi
) {
    val isUser = message.isUser

    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val icon = if (isUser) {
        Icons.Outlined.Person
    } else {
        Icons.Outlined.SmartToy
    }

    val sender = if (isUser) {
        "Tú"
    } else {
        "BabyBot"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.88f),
            shape = MaterialTheme.shapes.large,
            color = bubbleColor
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.size(6.dp))

                    Text(
                        text = sender,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = message.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SoftIconBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    size: Int = 44
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(
                color = tint.copy(alpha = 0.16f),
                shape = MaterialTheme.shapes.large
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint
        )
    }
}