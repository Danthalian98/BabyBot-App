package com.proyecto.babybot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.proyecto.babybot.app.MainScreen
import com.proyecto.babybot.auth.LoginScreen
import com.proyecto.babybot.auth.RegisterScreen
import com.proyecto.babybot.onboarding.SplashScreen
import com.proyecto.babybot.subscription.SubscriptionScreen
import com.proyecto.babybot.subscription.TrialInfoScreen

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
            SubscriptionScreen()
        }
    }
}