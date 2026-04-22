package com.proyecto.babybot.forum

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.ui.components.AppSectionHeader
import com.proyecto.babybot.ui.components.HeaderVariant
import com.proyecto.babybot.ui.theme.NavTopColorLight
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.proyecto.babybot.R

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun PostDetailScreen(
    postId: String,
    userId: String,
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var idComentarioReportar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(postId) {
        viewModel.loadPostDetails(postId)
    }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { texto ->
            Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
            viewModel.clearUiMessage()
        }
    }

    Scaffold(topBar = {
        Box {
            AppSectionHeader(
                title = "          Detalle del hilo",
                subtitle = "                Lee y responde la conversación",
                variant = HeaderVariant.SIMPLE,
                showNotifications = false,
                showSettings = false
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shape = CircleShape
                    )
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Regresar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    },
        bottomBar = {
            CommentInputBar(
                onCommentSend = { texto ->
                    viewModel.enviarComentario(postId, texto)
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.post?.let { post ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.30f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = post.titulo,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Publicado por ${post.userName} • ${post.fecha}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Box {
                                        IconButton(onClick = { showMenu = true }) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = "Opciones",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showMenu,
                                            onDismissRequest = { showMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = "Reportar",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Flag,
                                                        contentDescription = null
                                                    )
                                                },
                                                onClick = {
                                                    showMenu = false
                                                    idComentarioReportar = null
                                                    showReportDialog = true
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = post.contenido,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                                ) {
                                    InteractionStat(
                                        count = post.likes.size.toString(),
                                        active = post.likes.contains(userId),
                                        activeColor = MaterialTheme.colorScheme.primary,
                                        icon = if (post.likes.contains(userId)) {
                                            Icons.Filled.ThumbUp
                                        } else {
                                            Icons.Outlined.ThumbUp
                                        },
                                        onClick = { viewModel.toggleLike(postId, userId) }
                                    )

                                    InteractionStat(
                                        count = post.dislikes.size.toString(),
                                        active = post.dislikes.contains(userId),
                                        activeColor = MaterialTheme.colorScheme.error,
                                        icon = if (post.dislikes.contains(userId)) {
                                            Icons.Filled.ThumbDown
                                        } else {
                                            Icons.Outlined.ThumbDown
                                        },
                                        onClick = { viewModel.toggleDislike(postId, userId) }
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.ChatBubbleOutline,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = post.comentarios.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                        ) {
                            Text(
                                text = "Respuestas",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            )
                        }
                    }
                }

                items(comments) { comment ->
                    CommentItem(
                        comment = comment,
                        onReportClick = { commentId ->
                            idComentarioReportar = commentId
                            showReportDialog = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    if (showReportDialog) {
        var detalleTexto by remember { mutableStateOf("") }
        var motivoSeleccionado by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = {
                showReportDialog = false
            },
            title = {
                Text(
                    text = "Reportar publicación",
                    style = MaterialTheme.typography.titleSmall
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Selecciona un motivo:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val opciones = listOf("Spam", "Contenido ofensivo", "Información falsa", "Otro")
                    opciones.forEach { motivo ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = motivo == motivoSeleccionado,
                                onClick = { motivoSeleccionado = motivo }
                            )
                            Text(
                                text = motivo,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable { motivoSeleccionado = motivo }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = detalleTexto,
                        onValueChange = { detalleTexto = it },
                        label = { Text("Detalles adicionales (opcional)") },
                        placeholder = { Text("Cuéntanos más...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (motivoSeleccionado.isNotEmpty()) {
                            if (idComentarioReportar != null) {
                                viewModel.reportarComentario(
                                    postId,
                                    idComentarioReportar!!,
                                    motivoSeleccionado,
                                    detalleTexto
                                )
                            } else {
                                viewModel.reportarPost(
                                    postId,
                                    motivoSeleccionado,
                                    detalleTexto
                                )
                            }
                            showReportDialog = false
                            idComentarioReportar = null
                        }
                    },
                    enabled = motivoSeleccionado.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Enviar reporte")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun InteractionStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: String,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (active) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = count,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun CommentInputBar(
    onCommentSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Escribe una respuesta...",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onCommentSend(text)
                        text = ""
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar comentario",
                    tint = if (text.isNotBlank()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    }
                )
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentUi,
    onReportClick: (String) -> Unit
) {
    val containerColor = if (comment.esOficial) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val authorIconColor = if (comment.esOficial) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (comment.esOficial) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (comment.esOficial) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_app),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = authorIconColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = comment.autor,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = comment.fecha,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!comment.esOficial) {
                    IconButton(
                        onClick = { onReportClick(comment.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = "Reportar comentario",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.contenido,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}