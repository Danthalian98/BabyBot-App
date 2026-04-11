package com.proyecto.babybot.onboarding

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.R
import com.proyecto.babybot.ui.theme.BtnColorsLight

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTrialInfo: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en SPLASH")
    }

    LaunchedEffect(state.destination) {
        when (state.destination) {
            SplashDestination.LOGIN -> onNavigateToLogin()
            SplashDestination.HOME -> onNavigateToHome()
            SplashDestination.TRIAL_INFO -> onNavigateToTrialInfo()
            SplashDestination.SUBSCRIPTIONS -> onNavigateToSubscriptions()
            SplashDestination.NONE -> Unit
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loading_anim")

    val loadingScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_scale"
    )

    val loadingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1) Fondo
        Image(
            painter = painterResource(id = R.drawable.img_splash_bg),
            contentDescription = "Fondo de BabyBot",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa suave para contraste
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x10000000),
                            Color.Transparent,
                            Color(0x16000000)
                        )
                    )
                )
        )

        // 2) Personaje + 3) Texto
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.7f))

            Image(
                painter = painterResource(id = R.drawable.img_splash_ch2),
                contentDescription = "Personaje principal BabyBot",
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .widthIn(max = 430.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF6EB8FF),
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Baby")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFFA8E48D),
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Bot")
                    }
                },
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Tu asistente para los primeros años",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.96f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(top = 9.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // 4) Loading encima de todo, cerca del tercio inferior
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_loading2),
                contentDescription = "Cargando",
                modifier = Modifier
                    .fillMaxWidth(0.34f)
                    .widthIn(max = 180.dp)
                    .offset(y = (-80).dp)
                    .scale(loadingScale)
                    .alpha(loadingAlpha),
                contentScale = ContentScale.Fit
            )
        }
    }
}