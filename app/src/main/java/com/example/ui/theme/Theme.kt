package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    onPrimary = Color.White,
    primaryContainer = CosmicSurfaceCard,
    onPrimaryContainer = TextPrimary,
    secondary = SecondaryTurquoise,
    onSecondary = Color.White,
    tertiary = AccentPurple,
    onTertiary = Color.White,
    background = CosmicBackground,
    onBackground = TextPrimary,
    surface = CosmicSurface,
    onSurface = TextPrimary,
    surfaceVariant = CosmicSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = CosmicBorder
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryCyan,
    onPrimary = Color.White,
    secondary = SecondaryTurquoise,
    onSecondary = Color.White,
    tertiary = AccentPurple,
    onTertiary = Color.White,
    background = CosmicBackground,
    onBackground = TextPrimary,
    surface = CosmicSurface,
    onSurface = TextPrimary,
    surfaceVariant = CosmicSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = CosmicBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
