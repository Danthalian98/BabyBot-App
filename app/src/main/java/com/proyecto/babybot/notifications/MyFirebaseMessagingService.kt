package com.proyecto.babybot.notifications

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val destination = remoteMessage.data["destination"]

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "BabyBot"

        val message = remoteMessage.notification?.body
            ?: remoteMessage.data["message"]
            ?: remoteMessage.data["body"]
            ?: "Tienes una nueva actualización"

        if (ForumNotificationFilter.shouldBlockForumNotification(
                context = applicationContext,
                remoteMessage = remoteMessage
            )
        ) {
            Log.d(
                "FCM_FORUM",
                "Notificación de foro bloqueada porque el switch de Foros está apagado."
            )
            return
        }

        if (!NotificationPreferences.areNotificationsAllowed(applicationContext)) {
            Log.w(
                "FCM_PERMISSION",
                "No se mostró la notificación porque BabyBot o el sistema tienen las notificaciones apagadas."
            )
            return
        }

        BabyBotNotificationHelper.showReminder(
            context = applicationContext,
            id = System.currentTimeMillis().toInt(),
            title = title,
            message = message,
            destination = destination ?: ""
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Nuevo token generado: $token")
    }
}