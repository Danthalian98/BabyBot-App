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
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.babybot.chatbot.ChatbotScreen
import com.proyecto.babybot.dailylog.DailyLogScreen
import com.proyecto.babybot.forum.ForumScreen
import com.proyecto.babybot.home.HomeScreen
import com.proyecto.babybot.forum.PostDetailScreen
import com.proyecto.babybot.forum.CreatePostScreen
import com.proyecto.babybot.settings.SettingsScreen
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    padding: PaddingValues
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo"

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = Modifier.padding(padding)
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                rootNavController = rootNavController
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    rootNavController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
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

        composable(
            route = "post_detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo"

            PostDetailScreen(
                postId = postId,
                userId = currentUserId,
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