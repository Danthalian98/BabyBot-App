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
import com.proyecto.babybot.settings.security.SecuritySettingsScreen
import com.proyecto.babybot.settings.SettingsThemeScreen
import com.proyecto.babybot.settings.SettingsAboutScreen
import com.proyecto.babybot.settings.SettingsPrivacyScreen
import androidx.compose.material3.ExperimentalMaterial3Api
import com.proyecto.babybot.notifications.NotificationsScreen
import com.proyecto.babybot.settings.account.AccountSettingsScreen
import com.proyecto.babybot.settings.notifications.NotificationSettingsScreen
import com.proyecto.babybot.settings.account.edit.EditAccountInfoScreen


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
                },
                onAccountClick = {
                    navController.navigate(Routes.SETTINGS_ACCOUNT)
                },
                onSecurityClick = {
                    navController.navigate(Routes.SETTINGS_SECURITY)
                },
                onNotificationsClick = {
                    navController.navigate(Routes.SETTINGS_NOTIFICATIONS)
                },
                onPrivacyClick = {
                    navController.navigate(Routes.SETTINGS_PRIVACY)
                },
                onThemeClick = {
                    navController.navigate(Routes.SETTINGS_THEME)
                },
                onAboutClick = {
                    navController.navigate(Routes.SETTINGS_ABOUT)
                }
            )
        }

        composable(Routes.SETTINGS_SECURITY) {
            SecuritySettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS_THEME) {
            SettingsThemeScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS_ABOUT) {
            SettingsAboutScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS_PRIVACY) {
            SettingsPrivacyScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS_ACCOUNT) {
            AccountSettingsScreen(
                onBack = { navController.popBackStack() },
                onEditInfoClick = { mode ->
                    navController.navigate(Routes.createEditAccountInfoRoute(mode))
                }
            )
        }

        composable(Routes.SETTINGS_NOTIFICATIONS) {
            NotificationSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FORUM) {
            ForumScreen(
                onPostClick = { postId ->
                    navController.navigate("post_detail/$postId")
                },
                onCreatePostClick = {
                    navController.navigate("create_post")
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNotificationsClick = {
                    navController.navigate(Routes.NOTIFICATIONS)
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
            DailyLogScreen(
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNotificationsClick = {
                    navController.navigate(Routes.NOTIFICATIONS)
                }
            )
        }

        composable(Routes.CHATBOT) {
            ChatbotScreen(
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNotificationsClick = {
                    navController.navigate(Routes.NOTIFICATIONS)
                }
            )
        }

        composable(
            route = Routes.EDIT_ACCOUNT_INFO,
            arguments = listOf(
                navArgument("mode") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getInt("mode") ?: 0

            EditAccountInfoScreen(
                mode = mode,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}