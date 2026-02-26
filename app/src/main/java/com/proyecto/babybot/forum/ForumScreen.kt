package com.proyecto.babybot.forum

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
// NOTA: Eliminé un import incorrecto de SegmentedButtonDefaults.Icon que tenías
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.ui.theme.BackPantallas
import com.proyecto.babybot.ui.theme.NavTopColorLight
import com.proyecto.babybot.ui.theme.TxtColorDark

@Composable
fun ForumScreen(
    onPostClick: (Int) -> Unit,
    onCreatePostClick: () -> Unit = {},
    viewModel: ForumViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en FORUM")
    }

    // CONTENEDOR PRINCIPAL: Ya no lleva verticalScroll
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackPantallas)
    ) {

        // 🔵 HEADER ESTILO HOME
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavTopColorLight)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Foros",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { onCreatePostClick() },
                        shape = RoundedCornerShape(50),
                        // Hacemos que el fondo del botón sea un poco más oscuro o claro para que resalte
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Crear",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Crear hilo",
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = {
                        Log.d("NAVIGATION", "Click en Ajustes Forum")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Ajustes",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // 🔵 CONTENIDO SCROLLEABLE (Lista de posts y filtros)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp) // Padding a los lados de la lista
        ) {

            // Título de sección
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Explorar hilos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TxtColorDark
                )
                Spacer(Modifier.height(16.dp))
            }

            // Filtros horizontales
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("Nuevos", "Populares", "Sueño", "Alimentación")) { filter ->
                        FilterChipItem(
                            text = filter,
                            isSelected = state.selectedFilter == filter
                        ) {
                            viewModel.onFilterSelected(filter)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // Lista de Posts
            items(state.posts) { post ->
                PostItem(
                    post = post,
                    onClick = { onPostClick(post.id) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.LightGray.copy(alpha = 0.5f) // Suavizamos el divisor
                )
            }

            // Espacio extra al final para que no quede pegado al fondo
            item {
                Spacer(Modifier.height(80.dp)) // Ajusta esto si tienes una barra de navegación inferior
            }
        }
    }
}

@Composable
fun PostItem(
    post: PostUi,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(post.avatarColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(post.userName, fontWeight = FontWeight.SemiBold)
            Text(
                text = " • ${post.fecha}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = post.titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(12.dp))

        // 🔵 NUEVA FILA INFERIOR CON ETIQUETAS
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Iconos de interacciones
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionIcon(Icons.Outlined.ArrowUpward, post.likes.toString(), tint = Color.DarkGray)
                ActionIcon(Icons.Outlined.ChatBubbleOutline, post.comentarios.toString(), tint = Color.DarkGray)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Etiquetas en óvalos
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                post.tags.forEach { tag ->
                    SmallTagChip(tag = tag)
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(32.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
        border = if (!isSelected)
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.3f))
        else null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ActionIcon(icon: ImageVector, count: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = count, fontSize = 13.sp, color = tint, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SmallTagChip(tag: String) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), // Fondo transparente del color primario
                shape = RoundedCornerShape(50) // Forma de óvalo perfecto
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tag,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}