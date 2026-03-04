package com.proyecto.babybot.forum

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
import com.proyecto.babybot.ui.theme.TxtColorDark

@Composable
fun PostDetailScreen(
    onBackClick: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en POSTDT")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackPantallas)
            .verticalScroll(rememberScrollState())
    ) {
        // 🔵 HEADER (Barra superior)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavTopColorLight)
                .padding(top = 24.dp, bottom = 24.dp, start = 8.dp, end = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Hilo del foro",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 🔵 CONTENIDO DEL POST
        if (state.isLoading) {
            // Mostrar un indicador de carga si los datos tardan
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.post != null) {
            val post = state.post!!

            Column(modifier = Modifier.padding(24.dp)) {

                // Título
                Text(
                    text = post.titulo,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TxtColorDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info del usuario
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(post.avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Person, null, tint = Color.White)
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(post.userName, fontWeight = FontWeight.SemiBold, color = TxtColorDark)
                        Text(post.fecha, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // El contenido completo
                Text(
                    text = post.contenido,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = TxtColorDark
                )

                Spacer(modifier = Modifier.height(32.dp))

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(16.dp))

                // Aquí podrías agregar en el futuro la sección de "Comentarios"
                Text(
                    text = "Comentarios (${post.comentarios})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TxtColorDark
                )
            }
        } else {
            // Si el post no se encuentra
            Text(
                text = "No se pudo cargar la información del hilo.",
                modifier = Modifier.padding(24.dp),
                color = Color.Red
            )
        }
    }
}