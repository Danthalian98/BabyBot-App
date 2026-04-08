package com.proyecto.babybot.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.ui.theme.BackPantallas
import com.proyecto.babybot.ui.theme.NavTopColorLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    userId: String,
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val comments by viewModel.comments.collectAsState()

    // Estados para el menú y el diálogo de reporte
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        viewModel.loadPostDetails(postId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Hilo", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavTopColorLight)
            )
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
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NavTopColorLight)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BackPantallas)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                state.post?.let { post ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                // 🔵 CABECERA DEL POST CON BOTÓN DE OPCIONES
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = post.titulo,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavTopColorLight
                                        )
                                        Text(
                                            text = "Publicado por ${post.userName} • ${post.fecha}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    // Icono de tres puntos para Reportar
                                    Box {
                                        IconButton(onClick = { showMenu = true }) {
                                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.Gray)
                                        }
                                        DropdownMenu(
                                            expanded = showMenu,
                                            onDismissRequest = { showMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Reportar") },
                                                onClick = {
                                                    showMenu = false
                                                    showReportDialog = true
                                                },
                                                leadingIcon = { Icon(Icons.Outlined.Flag, null) }
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = post.contenido,
                                    fontSize = 15.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 20.sp
                                )

                                Spacer(Modifier.height(16.dp))

                                // FILA DE INTERACCIÓN (LIKES/DISLIKES/COMS)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { viewModel.toggleLike(postId, userId) }) {
                                            Icon(
                                                imageVector = if (post.likes.contains(userId)) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                                contentDescription = null,
                                                tint = if (post.likes.contains(userId)) NavTopColorLight else Color.Gray
                                            )
                                        }
                                        Text("${post.likes.size}", fontSize = 14.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { viewModel.toggleDislike(postId, userId) }) {
                                            Icon(
                                                imageVector = if (post.dislikes.contains(userId)) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                                contentDescription = null,
                                                tint = if (post.dislikes.contains(userId)) Color.Red else Color.Gray
                                            )
                                        }
                                        Text("${post.dislikes.size}", fontSize = 14.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("${post.comentarios}", fontSize = 14.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Respuestas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(comments) { comment ->
                    CommentItem(comment)
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }

    // 🟢 DIÁLOGO DE REPORTE
    if (showReportDialog) {
        // Estado local para el texto del detalle
        var detalleTexto by remember { mutableStateOf("") }
        var motivoSeleccionado by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Reportar publicación") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona un motivo:", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    // Lista de motivos
                    val opciones = listOf("Spam", "Contenido ofensivo", "Información falsa", "Otro")
                    opciones.forEach { motivo ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (motivo == motivoSeleccionado),
                                onClick = { motivoSeleccionado = motivo }
                            )
                            Text(text = motivo, modifier = Modifier.clickable { motivoSeleccionado = motivo })
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo para el detalle
                    OutlinedTextField(
                        value = detalleTexto,
                        onValueChange = { detalleTexto = it },
                        label = { Text("Detalles adicionales (opcional)") },
                        placeholder = { Text("Cuéntanos más...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (motivoSeleccionado.isNotEmpty()) {
                            // Enviamos AMBOS: motivo y el texto del detalle
                            viewModel.reportarPost(postId, motivoSeleccionado, detalleTexto)
                            showReportDialog = false
                        }
                    },
                    enabled = motivoSeleccionado.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = NavTopColorLight)
                ) {
                    Text("Enviar Reporte")
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInputBar(onCommentSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxWidth().imePadding(), tonalElevation = 8.dp, color = Color.White) {
        Row(modifier = Modifier.padding(16.dp, 8.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text, onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe una respuesta...", fontSize = 14.sp) },
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavTopColorLight, unfocusedBorderColor = Color.LightGray)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { if (text.isNotBlank()) { onCommentSend(text); text = "" } }, enabled = text.isNotBlank()) {
                Icon(Icons.AutoMirrored.Filled.Send, null, tint = if (text.isNotBlank()) NavTopColorLight else Color.Gray)
            }
        }
    }
}

@Composable
fun CommentItem(comment: CommentUi) {
    val backgroundColor = if (comment.esOficial) Color(0xFFE3F2FD) else Color.White
    val borderColor = if (comment.esOficial) NavTopColorLight.copy(alpha = 0.5f) else Color.Transparent
    Column(modifier = Modifier.fillMaxWidth().background(backgroundColor, RoundedCornerShape(12.dp)).border(1.dp, borderColor, RoundedCornerShape(12.dp)).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = if (comment.esOficial) Icons.Rounded.AutoAwesome else Icons.Rounded.Person
            val iconBg = if (comment.esOficial) NavTopColorLight else Color.LightGray
            Box(modifier = Modifier.size(24.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(if (comment.esOficial) "BabyBot (Oficial)" else comment.autor, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (comment.esOficial) NavTopColorLight else Color.DarkGray)
        }
        Spacer(Modifier.height(8.dp))
        Text(comment.contenido, fontSize = 14.sp, color = Color.Black)
        Text(comment.fecha, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
    }
}