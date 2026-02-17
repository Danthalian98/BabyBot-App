package com.proyecto.babybot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.babybot.ui.theme.BabyBotTheme
import com.proyecto.babybot.ui.theme.BlueSkyeLight
import com.proyecto.babybot.ui.theme.HardBlueText
import com.proyecto.babybot.ui.theme.LightBlueButton

class ForosLista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabyBotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ComunidadForos(
                        name = "",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class PostForos(
    val userName: String,
    val fecha: String,
    val titulo: String,
    val contenido: String,
    val tags: List<String>,
    val likes: Int,
    val comentarios: Int,
    val avataresColor: Color // Colores para el avatar (Temporal)
)

@Composable
fun ComunidadForos(name: String, modifier: Modifier = Modifier) {
    val posts = listOf(
        PostForos(
            "Sarah M.", "2h ago",
            "First time my baby slept through the night!",
            "After 4 months of sleepless nights, Emma finally slept for 7 hours straight! I feel like a new person. Any tips to keep this going?",
            listOf("Sleep", "Milestone"), 234, 45, Color.Green
        ),
        PostForos(
            "Michael R.", "5h ago",
            "Teething remedies that actually work?",
            "Our little one is going through teething hell. We've tried cold teethers and tylenol. What else has worked for you?",
            listOf("Health", "Tips"), 189, 67, Color.Red
        ),
        PostForos(
            "Jessica L.", "8h ago",
            "Best baby carriers for hiking?",
            "Planning our first family hike! Looking for recommendations on comfortable carriers for a 5-month-old. What do you use?",
            listOf("Gear", "Activities"), 156, 34, Color.Magenta
        ),
        PostForos(
            "David K.", "12h ago",
            "Introducing solid foods - when did you start?",
            "Pediatrician says we can start solids at 6 months. But some friends started earlier. What was your experience?",
            listOf("Feeding", "Development"), 98, 52, Color.Yellow
        )
    )
    Scaffold(
        bottomBar = { ForosBottomNavigation(LightBlueButton) },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            //Apartado del encabezado
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Foros",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = HardBlueText
                )
            }

            //Filtros
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    item { FilterChipItem("Nuevos", true, LightBlueButton) }
                    items(listOf("Nuevos", "Populares", "Sueño", "Alimentacion")) {label ->
                        FilterChipItem(label, false, Color.White)
                    }
                }
            }
            //Lista de los posts
            items(posts) {post ->
                PostItem(post, LightBlueButton, BlueSkyeLight, Color.White)
                HorizontalDivider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun PostItem(post: PostForos,  colorPrimario: Color, tagBgColor: Color, textColor: Color){
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            //Info. del autor del post
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(post.avataresColor, CircleShape),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            //Contenido del Post
            Text(
                text = post.userName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = textColor
            )
            Text(
                text = "• ${post.fecha}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = post.titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = textColor.copy(alpha = 0.8f),
            lineHeight = 20.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        //Etiquetas
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            post.tags.forEach { tag ->
                Box(
                    modifier = Modifier
                        .border(1.dp, colorPrimario.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .background(tagBgColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ){
                    Text(
                        text = tag,
                        fontSize = 11.sp,
                        color = colorPrimario,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        //Botones de interaccion
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionIcon(Icons.Outlined.ArrowUpward, post.likes.toString(), colorPrimario)
            Spacer(modifier = Modifier.width(16.dp))
            ActionIcon(Icons.Outlined.ChatBubbleOutline, post.comentarios.toString(), colorPrimario)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.Star, contentDescription = "Favorito", tint = colorPrimario, modifier = Modifier.size(24.dp))
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
fun FilterChipItem(text: String, isSelected: Boolean, color: Color) {
    Surface(
        color = if (isSelected) color else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(50),
        modifier = Modifier.height(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ){
            Text(
                text = text,
                color = if (isSelected) Color.White else color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ForosBottomNavigation(activeColor: Color) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Inicio")},
            label = { Text("Inicio")},
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Groups, contentDescription = "Foros")},
            label = { Text("Foros")},
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = activeColor,
                selectedTextColor = activeColor,
                indicatorColor = activeColor.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Rounded.Assignment, contentDescription = "Registros")},
            label = { Text("Registros")},
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.ChatBubble, contentDescription = "IA")},
            label = { Text("IA")},
            selected = false,
            onClick = {}
        )
    }
}

