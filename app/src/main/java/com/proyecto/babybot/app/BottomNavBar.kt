package com.proyecto.babybot.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.InsertChart
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.proyecto.babybot.navigation.Routes

@Composable
fun BottomNavBar(navController: NavHostController) {

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 0.dp
    ) {

        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = currentRoute == Routes.HOME,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
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
            icon = { Icon(Icons.Rounded.InsertChart, contentDescription = "Registros") },
            label = { Text("Registros") },
            selected = currentRoute == Routes.DAILYLOG,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
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
            icon = { Icon(Icons.Rounded.Forum, contentDescription = "Foros") },
            label = { Text("Foros") },
            selected = currentRoute == Routes.FORUM,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
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
            icon = { Icon(Icons.Rounded.SmartToy, contentDescription = "IA Chat") },
            label = { Text("IA Chat") },
            selected = currentRoute == Routes.CHATBOT,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
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