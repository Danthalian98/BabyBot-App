package com.proyecto.babybot.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.proyecto.babybot.navigation.Routes
import com.proyecto.babybot.ui.theme.NavTopColorLight

@Composable
fun BottomNavBar(navController: NavHostController) {

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = NavTopColorLight,
        tonalElevation = 0.dp
    ) {

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Star, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = currentRoute == Routes.HOME,
            onClick = {
                navController.navigate(Routes.HOME) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Star, contentDescription = "Registros") },
            label = { Text("Registros") },
            selected = currentRoute == Routes.DAILYLOG,
            onClick = {
                navController.navigate(Routes.DAILYLOG) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Star, contentDescription = "Foros") },
            label = { Text("Foros") },
            selected = currentRoute == Routes.FORUM,
            onClick = {
                navController.navigate(Routes.FORUM) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Star, contentDescription = "IA Chat") },
            label = { Text("IA Chat") },
            selected = currentRoute == Routes.CHATBOT,
            onClick = {
                navController.navigate(Routes.CHATBOT) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}