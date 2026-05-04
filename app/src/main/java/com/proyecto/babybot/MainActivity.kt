package com.proyecto.babybot

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.proyecto.babybot.chatbot.ChatRepository
import com.proyecto.babybot.navigation.RootNavGraph
import com.proyecto.babybot.navigation.Routes
import com.proyecto.babybot.notifications.BabyBotNotificationHelper
import com.proyecto.babybot.ui.theme.BabyBotTheme
import com.proyecto.babybot.notifications.BabyBotNotificationHelper.DESTINATION_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @javax.inject.Inject lateinit var authDataSource: com.proyecto.babybot.data.firebase.AuthDataSource

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Manejo de permisos para Android 13+
        verificarPermisosNotificaciones()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            BabyBotTheme {
                val navController = rememberNavController()

                // LaunchedEffect mejorado para evitar crashes
                LaunchedEffect(intent) {
                    val destination = intent.getStringExtra(DESTINATION_KEY)
                    if (destination != null) {
                        try {
                            navController.navigate(destination) {
                                // Si el usuario ya está logueado, esto lo llevará directo.
                                // Si no, el Splash/Login se encargará, pero ya no habrá crash.
                                launchSingleTop = true
                                restoreState = true
                            }
                            intent.removeExtra(DESTINATION_KEY)
                        } catch (e: Exception) {
                            Log.e("NAV_ERROR", "Error: ${e.message}")
                        }
                    }
                }

                RootNavGraph(navController = navController)
            }
        }

        // Inicialización de servicios
        BabyBotNotificationHelper.createReminderChannel(this)
        obtenerYGuardarTokenFCM()
    }

    private fun verificarPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun obtenerYGuardarTokenFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "Token actual: $token")
                actualizarTokenEnBaseDeDatos(token)
            } else {
                Log.w("FCM_TOKEN", "Error obteniendo el token", task.exception)
            }
        }
    }

    private fun actualizarTokenEnBaseDeDatos(token: String) {
        lifecycleScope.launch {
            authDataSource.updateFcmToken(token)
        }
    }
}