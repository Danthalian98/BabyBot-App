package com.proyecto.babybot.app

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.proyecto.babybot.navigation.MainNavGraph
import com.proyecto.babybot.navigation.Routes

@Composable
fun MainScreen(rootNavController: NavHostController) {

    val navController = rememberNavController()

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    val showBottomBar = currentRoute != Routes.SETTINGS

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController)
            }
        }
    ) { padding ->

        MainNavGraph(
            navController = navController,
            rootNavController = rootNavController,
            padding = padding
        )
    }
}