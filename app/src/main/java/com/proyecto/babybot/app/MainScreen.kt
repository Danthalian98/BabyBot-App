package com.proyecto.babybot.app

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.babybot.navigation.MainNavGraph

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { padding ->

        MainNavGraph(
            navController = navController,
            padding = padding
        )
    }
}