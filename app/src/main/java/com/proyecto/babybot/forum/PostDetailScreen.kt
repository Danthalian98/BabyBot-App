package com.proyecto.babybot.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

    // Cargamos los datos al iniciar
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. EL POST PRINCIPAL
                state.post?.let { post ->
                    item {
                        Text(post.titulo, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(post.contenido, fontSize = 16.sp)
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                    }
                }

                // 2. SECCIÓN DE COMENTARIOS
                item {
                    Text("Respuestas", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                }

                items(comments) { comment ->
                    CommentItem(comment)
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun CommentItem(comment: CommentUi) {
    // Si es oficial (IA), usamos un color de fondo especial
    val backgroundColor = if (comment.esOficial) Color(0xFFE3F2FD) else Color.White
    val borderColor = if (comment.esOficial) NavTopColorLight else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icono: Robot para IA, Persona para usuarios
            val icon = if (comment.esOficial) Icons.Rounded.AutoAwesome else Icons.Rounded.Person

            Box(
                modifier = Modifier.size(24.dp).background(if(comment.esOficial) NavTopColorLight else Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = if (comment.esOficial) "BabyBot (Respuesta Oficial)" else comment.autor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (comment.esOficial) NavTopColorLight else Color.Unspecified
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(text = comment.contenido, fontSize = 14.sp)

        Spacer(Modifier.height(4.dp))
        Text(text = comment.fecha, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
    }
}