package com.proyecto.babybot.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
// Importa Firebase Auth para obtener el ID real
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.babybot.chatbot.ChatbotScreen
import com.proyecto.babybot.dailylog.DailyLogScreen
import com.proyecto.babybot.forum.ForumScreen
import com.proyecto.babybot.home.HomeScreen
import com.proyecto.babybot.forum.PostDetailScreen
import com.proyecto.babybot.forum.CreatePostScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    padding: PaddingValues
) {
    // Obtenemos el ID del usuario actual para pasarlo a las pantallas que lo necesiten
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo"

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = Modifier.padding(padding)
    ) {
        composable(Routes.HOME) {
            HomeScreen(rootNavController)
        }

        composable(Routes.FORUM) {
            ForumScreen(
                onPostClick = { postId ->
                    navController.navigate("post_detail/$postId")
                },
                onCreatePostClick = {
                    navController.navigate("create_post")
                }
            )
        }

        // 🔵 DETALLE DEL POST (Ahora con userId)
        composable(
            route = "post_detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            // Obtenemos el ID real del usuario actual
            val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo"

            PostDetailScreen(
                postId = postId,
                userId = currentUserId, // Pasamos el ID real
                onBack = { navController.popBackStack() }
            )
        }

        composable("create_post") {
            CreatePostScreen(
                onBack = { navController.popBackStack() },
                onPostCreated = { navController.popBackStack() }
            )
        }

        composable(Routes.DAILYLOG) {
            DailyLogScreen()
        }

        composable(Routes.CHATBOT) {
            ChatbotScreen()
        }
    }
}