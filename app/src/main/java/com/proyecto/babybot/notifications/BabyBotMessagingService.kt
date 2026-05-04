package com.proyecto.babybot.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BabyBotMessagingService : FirebaseMessagingService() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 1. Extraer datos de la notificación
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "BabyBot"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["message"] ?: ""
        val destination = remoteMessage.data["destination"]

        // 2. Verificar permisos antes de mostrar
        if (tienePermisoDeNotificacion()) {
            BabyBotNotificationHelper.showReminder(
                context = applicationContext,
                id = System.currentTimeMillis().toInt(),
                title = title,
                message = body,
                destination = destination
            )
        } else {
            Log.w("FCM_PERMISSION", "No se mostró la notificación por falta de permisos en el dispositivo.")
        }
    }

    private fun tienePermisoDeNotificacion(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // En versiones anteriores a Android 13, el permiso se otorga al instalar
            true
        }
    }

    // En BabyBotMessagingService.kt

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Nuevo token: $token")

        // Obtener el ID del usuario actual
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Guardar directamente en Firestore
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .update("fcmToken", token)
                .addOnFailureListener { e ->
                    Log.e("FCM_UPDATE", "Error al guardar token: ${e.message}")
                }
        }
    }
}