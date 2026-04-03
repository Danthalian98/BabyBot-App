package com.proyecto.babybot.chatbot

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.ui.theme.*

@Composable
fun ChatbotScreen(
    viewModel: ChatbotViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    // Scroll automático al recibir mensajes
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackPantallas)
    ) {
        // 🔵 HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavTopColorLight)
                .padding(top = 40.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SmartToy, // Icono de Robot más profesional
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "BabyBot AI",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (state.isLoading) "Escribiendo..." else "En línea",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // MENSAJES
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.messages) { message ->
                if (message.isUser) {
                    UserMessageBubble(message)
                } else {
                    AIMessageBubble(message)
                }
            }

            // Indicador visual de que el bot está pensando
            if (state.isLoading) {
                item {
                    LoadingBubble()
                }
            }
        }

        // SUGERENCIAS (Se ocultan si está cargando para evitar spam)
        AnimatedVisibility(visible = !state.isLoading) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.suggestions) { suggestion ->
                    SuggestionChip(suggestion) {
                        viewModel.onMessageChange(suggestion)
                        viewModel.sendMessage() // Envía directamente al tocar
                    }
                }
            }
        }

        // INPUT
        Surface(
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (state.currentMessage.isEmpty()) {
                        Text("Escribe tu duda aquí...", color = Color.Gray, fontSize = 14.sp)
                    }
                    BasicTextField(
                        value = state.currentMessage,
                        onValueChange = { if (!state.isLoading) viewModel.onMessageChange(it) },
                        enabled = !state.isLoading,
                        textStyle = TextStyle(color = TxtColorDark, fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.width(12.dp))

                FloatingActionButton(
                    onClick = { viewModel.sendMessage() },
                    containerColor = if (state.isLoading) Color.Gray else NavTopColorLight,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun AIMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, Color(0xFFEEEEEE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.SmartToy, null, modifier = Modifier.size(18.dp), tint = NavTopColorLight)
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 2.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(message.text, color = TxtColorDark, fontSize = 14.sp)
                Text(
                    text = message.time,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun UserMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp),
            color = NavTopColorLight
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(message.text, color = Color.White, fontSize = 14.sp)
                Text(
                    text = message.time,
                    fontSize = 10.sp,
                    color = Color.White.copy(0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun LoadingBubble() {
    Row(modifier = Modifier.padding(start = 40.dp)) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.5f)
        ) {
            Text(
                "BabyBot está pensando...",
                modifier = Modifier.padding(8.dp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = NavTopColorLight.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, NavTopColorLight.copy(alpha = 0.2f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 12.sp,
            color = NavTopColorLight,
            fontWeight = FontWeight.Medium
        )
    }
}