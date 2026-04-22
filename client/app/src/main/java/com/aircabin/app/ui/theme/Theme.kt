package com.aircabin.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors: ColorScheme = lightColorScheme(
    primary = AccentDeep,
    onPrimary = SurfacePrimary,
    secondary = AccentGold,
    background = BackgroundBase,
    onBackground = TextPrimary,
    surface = SurfacePrimary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSecondary,
    outline = SurfaceStroke,
    error = RiskTint,
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = AccentGold,
    onPrimary = AccentDeep,
    secondary = AccentGold,
    background = AccentDeep,
    onBackground = SurfacePrimary,
    surface = ColorScheme().surface,
    onSurface = SurfacePrimary,
)

@Composable
fun AirCabinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) LightColors else LightColors,
        typography = AirCabinTypography,
        content = content,
    )
}
