package com.proyecto.babybot.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.R
import android.util.Log

//Elementos visuales
@Composable
fun SplashScreen( //Todas las pantallas Screen son solo vistas
    onNavigateToTrial: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel() //separa la logica del UI
) {
    //Los state son los elementos que cambian dentro de la pantalla, variables con las que trabajar
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { //Log de navegacion
        Log.d("NAVIGATION", "Estoy en SPLASH")
    }


    LaunchedEffect(state.isLoading) { //Permite la navegacion al terminar de cargar
        if (!state.isLoading) {
            if (state.isLoggedIn) { //Redirige al trial si estas logeado
                onNavigateToTrial()
            } else {                //En caso contrario al login
                onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_babybot_logo),
            contentDescription = "Logo"
        )
    }
}

