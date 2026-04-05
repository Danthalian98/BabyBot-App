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
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val comments by viewModel.comments.collectAsState()

    // Sincronización con el ViewModel
    LaunchedEffect(postId) {
        viewModel.loadPostDetails(postId)
    }

    Scaffold(
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
            // Barra para escribir comentarios
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

                // 1. EL POST PRINCIPAL (Estilo Card)
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
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Por ${post.userName} • ${post.fecha}",
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
                            }
                        }
                    }
                }

                // 2. SECCIÓN DE COMENTARIOS
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInputBar(onCommentSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(), // Sube la barra cuando el teclado aparece
        tonalElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe una respuesta...", fontSize = 14.sp) },
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavTopColorLight,
                    unfocusedBorderColor = Color.LightGray,
                )
            )

            Spacer(Modifier.width(8.dp))

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
                    contentDescription = "Enviar",
                    tint = if (text.isNotBlank()) NavTopColorLight else Color.Gray
                )
            }
        }
    }
}

@Composable
fun CommentItem(comment: CommentUi) {
    val backgroundColor = if (comment.esOficial) Color(0xFFE3F2FD) else Color.White
    val borderColor = if (comment.esOficial) NavTopColorLight.copy(alpha = 0.5f) else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = if (comment.esOficial) Icons.Rounded.AutoAwesome else Icons.Rounded.Person
            val iconBg = if (comment.esOficial) NavTopColorLight else Color.LightGray

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = if (comment.esOficial) "BabyBot (Oficial)" else comment.autor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (comment.esOficial) NavTopColorLight else Color.DarkGray
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(text = comment.contenido, fontSize = 14.sp, color = Color.Black)

        Spacer(Modifier.height(4.dp))
        Text(
            text = comment.fecha,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}