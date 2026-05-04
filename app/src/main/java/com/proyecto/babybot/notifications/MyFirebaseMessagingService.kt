package com.proyecto.babybot.notifications

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Este método se dispara cuando llega una notificación y la app está abierta
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 1. Extraer datos (aquí vendrá el postId que enviemos desde la nube)
        val destination = remoteMessage.data["destination"]
        val title = remoteMessage.notification?.title ?: "BabyBot"
        val message = remoteMessage.notification?.body ?: "Tienes una nueva actualización"

        // 2. Usar tu Helper existente para mostrar la notificación localmente
        // Esto aprovecha el canal que ya creaste en la MainActivity
        BabyBotNotificationHelper.showReminder(
            context = applicationContext,
            id = System.currentTimeMillis().toInt(),
            title = title,
            message = message,
            destination = destination ?: ""
        )
    }

    // Si el token cambia (por ejemplo, el usuario borra datos de la app), lo actualizamos
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Nuevo token generado: $token")
        // Aquí podrías llamar a tu authDataSource si fuera necesario,
        // pero por ahora con el de la MainActivity estamos cubiertos.
    }
}