package com.proyecto.babybot.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.proyecto.babybot.app.MainScreen
import com.proyecto.babybot.auth.ForgotPasswordScreen
import com.proyecto.babybot.auth.LoginScreen
import com.proyecto.babybot.auth.RegisterScreen
import com.proyecto.babybot.forum.PostDetailScreen
import com.proyecto.babybot.onboarding.SplashScreen
import com.proyecto.babybot.subscription.SubscriptionScreen
import com.proyecto.babybot.subscription.TrialInfoScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToTrialInfo = {
                    navController.navigate(Routes.TRIALINFO) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToSubscriptions = {
                    navController.navigate(Routes.SUBSCRIPTIONS) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToTrial = {
                    navController.navigate(Routes.TRIALINFO) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSubscriptions = {
                    navController.navigate(Routes.SUBSCRIPTIONS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.TRIALINFO) {
            TrialInfoScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.TRIALINFO) { inclusive = true }
                    }
                },
                onNavigateToSubscriptions = {
                    navController.navigate(Routes.SUBSCRIPTIONS) {
                        popUpTo(Routes.TRIALINFO) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            MainScreen(navController)
        }

        composable(Routes.SUBSCRIPTIONS) {
            SubscriptionScreen(
                onPurchaseCompleted = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SUBSCRIPTIONS) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "post_detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            // IMPORTANTE: Aquí necesitamos el ID del usuario actual.
            // Como es nivel raíz, lo obtenemos de Firebase directamente.
            val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo"

            PostDetailScreen(
                postId = postId,
                userId = currentUserId,
                onBack = {
                    // Si venimos de una notificación, al dar atrás
                    // lo ideal es que nos mande al Home.
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}