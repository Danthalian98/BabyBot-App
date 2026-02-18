package com.proyecto.babybot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.proyecto.babybot.navigation.RootNavGraph
import com.proyecto.babybot.ui.theme.BabyBotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BabyBotTheme {
                //Inicia el controlador de navegacion y redirige a RootNavGraph
                val navController = rememberNavController()
                RootNavGraph(navController)
            }
        }

    }
}
