package com.proyecto.babybot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.proyecto.babybot.app.MainScreen
import com.proyecto.babybot.auth.LoginScreen
import com.proyecto.babybot.auth.RegisterScreen
import com.proyecto.babybot.onboarding.SplashScreen
import com.proyecto.babybot.subscription.TrialInfoScreen

@Composable
fun RootNavGraph(navController: NavHostController) {

    NavHost( //Define el Splash como pantalla principal
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) { //Navegacion dentro del splash
            SplashScreen(
                onNavigateToTrial = {
                    //En caso de estar logeado puede redirigir a ver el estado de subscripcion
                    navController.navigate(Routes.TRIALINFO) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    //En caso de no estarlo va al login
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }


        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToTrial = { //Al logearse va al aviso de prueba gratis
                    navController.navigate(Routes.TRIALINFO) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { //Va a registro en caso de no tener cuenta
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.TRIALINFO) {
            TrialInfoScreen(
                onNavigateToHome = { //Va a la pestaña principal Home
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { //Vuelve al login desde el registro
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            MainScreen()
        }

    }
}
