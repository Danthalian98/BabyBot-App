package com.proyecto.babybot.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ThumbDown // Importar Dislike
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
    ) { padding -> // Este padding es vital
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NavTopColorLight)
            }
        } else {
            // 🟢 CORRECCIÓN DE LAYOUT: Todo dentro del LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding) // Aplicamos el padding del Scaffold AQUÍ
                    .background(BackPantallas)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                // 1. EL POST PRINCIPAL
                state.post?.let { post ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
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
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = post.contenido,
                                    fontSize = 15.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 20.sp
                                )

                                Spacer(Modifier.height(16.dp))

                                // 🟢 CORRECCIÓN DE UI DE LIKES: Fila de interacción
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Like
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = {
                                            viewModel.toggleLike(postId, userId)
                                        }) {
                                            IconButton(onClick = { viewModel.toggleLike(postId, userId) }) {
                                                Icon(
                                                    imageVector = if (post.likes.contains(userId)) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                                    contentDescription = null,
                                                    tint = if (post.likes.contains(userId)) NavTopColorLight else Color.Gray
                                                )
                                            }
                                        }
                                        // Mostramos el conteo (tamaño de la lista)
                                        Text("${post.likes.size}", fontSize = 14.sp)
                                    }
                                    // Dislike
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { viewModel.toggleDislike(postId, userId) }) {
                                            Icon(
                                                imageVector = if (post.dislikes.contains(userId)) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                                contentDescription = null,
                                                tint = if (post.dislikes.contains(userId)) Color.Red else Color.Gray // Rojo para dislike
                                            )
                                        }
                                        Text("${post.dislikes.size}", fontSize = 14.sp)
                                    }
                                    // Comentarios (Contador)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.ChatBubbleOutline,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("${post.comentarios}", fontSize = 14.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. SECCIÓN DE COMENTARIOS (Ahora DENTRO de LazyColumn)
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
            } // Fin LazyColumn
        }
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