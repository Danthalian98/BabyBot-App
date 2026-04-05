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
import com.proyecto.babybot.chatbot.ChatbotScreen
import com.proyecto.babybot.dailylog.DailyLogScreen
import com.proyecto.babybot.forum.ForumScreen
import com.proyecto.babybot.home.HomeScreen
import com.proyecto.babybot.forum.PostDetailScreen
import com.proyecto.babybot.forum.CreatePostScreen // Asegúrate de importar tu nueva pantalla

@Composable
fun MainNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    padding: PaddingValues
) {
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
                    // Navegamos usando el ID (String) de Firestore
                    navController.navigate("post_detail/$postId")
                },
                onCreatePostClick = {
                    navController.navigate("create_post")
                }
            )
        }

        // 🔵 DETALLE DEL POST (Corregido a String)
        composable(
            route = "post_detail/{postId}",
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType } // <-- CAMBIO A STRING
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(
                postId = postId, // Le pasamos el ID a la pantalla
                onBack = { navController.popBackStack() }
            )
        }

        // 🔵 CREAR POST (Nueva ruta)
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