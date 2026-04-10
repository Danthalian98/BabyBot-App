package com.proyecto.babybot.chatbot

import com.google.firebase.Timestamp

// Este objeto representa CÓMO se ve el mensaje en Firestore
data class ChatEntity(
    val autor: String = "",       // "user" o "model"
    val contenido: String = "",
    val fecha: Timestamp? = null, // Usamos Timestamp para orden exacto
    val autorId: String = ""
)