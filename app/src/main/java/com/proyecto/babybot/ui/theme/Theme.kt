package com.proyecto.babybot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BabyBotColorScheme = lightColorScheme(
    primary = ActionPrimary,
    onPrimary = TextOnPrimary,

    secondary = BabyBlue,
    onSecondary = TextOnPrimary,

    tertiary = BabyLavender,
    onTertiary = TextTitle,

    background = AppBackground,
    onBackground = TextTitle,

    surface = SurfaceCard,
    onSurface = TextBody,

    surfaceVariant = SurfaceSoft,
    onSurfaceVariant = TextBody,

    primaryContainer = HeaderBlueSoft,
    onPrimaryContainer = TextTitle,

    secondaryContainer = ActionSoft,
    onSecondaryContainer = TextTitle,

    tertiaryContainer = AccentLavender,
    onTertiaryContainer = TextTitle,

    outline = BorderSoft,
    outlineVariant = BorderLight,

    scrim = TextTitle
)

@Composable
fun BabyBotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BabyBotColorScheme,
        typography = Typography,
        content = content
    )
}