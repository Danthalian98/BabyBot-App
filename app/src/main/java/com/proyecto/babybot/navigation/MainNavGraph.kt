package com.proyecto.babybot.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType // IMPORTANTE AGREGAR ESTO
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument // IMPORTANTE AGREGAR ESTO
import com.proyecto.babybot.chatbot.ChatbotScreen
import com.proyecto.babybot.dailylog.DailyLogScreen
import com.proyecto.babybot.forum.ForumScreen
import com.proyecto.babybot.home.HomeScreen
import com.proyecto.babybot.forum.PostDetailScreen // IMPORTANTE AGREGAR ESTO

@Composable
fun MainNavGraph(
    navController: NavHostController,
    padding: PaddingValues
) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = Modifier.padding(padding)
    ) {

        composable(Routes.HOME) {
            HomeScreen()
        }

        composable(Routes.FORUM) {
            ForumScreen(onPostClick = { postId ->
                navController.navigate(Routes.createPostDetailRoute(postId))
            })
        }

        // 🔵 AQUÍ ESTÁ LA MAGIA: Registramos la pantalla de detalle
        composable(
            route = Routes.POST_DETAIL,
            arguments = listOf(
                navArgument("postId") { type = NavType.IntType } // Le decimos que el argumento es un Int
            )
        ) {
            PostDetailScreen(
                onBackClick = {
                    navController.popBackStack() // Esto hace que el botón de regresar funcione
                }
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