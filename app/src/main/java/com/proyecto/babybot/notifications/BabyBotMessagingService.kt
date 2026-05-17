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
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "BabyBot"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["message"]
            ?: remoteMessage.data["body"]
            ?: "Tienes una nueva actualización"

        val destination = remoteMessage.data["destination"]

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
            message = body,
            destination = destination
        )
    }

    private fun tienePermisoDeNotificacion(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Nuevo token: $token")

        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
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

object ForumNotificationFilter {

    fun isForumNotification(remoteMessage: RemoteMessage): Boolean {
        val destination = remoteMessage.data["destination"]
        val type = remoteMessage.data["type"]
        val category = remoteMessage.data["category"]

        return destination == NotificationDestinations.FORUMS ||
                destination == "forum" ||
                destination == "forums" ||
                type == "forum" ||
                type == "forums" ||
                type == "forum_reply" ||
                type == "post_reply" ||
                category == "forum" ||
                category == "forums"
    }

    fun shouldBlockForumNotification(
        context: android.content.Context,
        remoteMessage: RemoteMessage
    ): Boolean {
        return isForumNotification(remoteMessage) &&
                !NotificationPreferences.areForumNotificationsEnabled(context)
    }
}