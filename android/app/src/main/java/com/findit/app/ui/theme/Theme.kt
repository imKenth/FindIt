package com.findit.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Violet600,
    onPrimary = White,
    primaryContainer = Violet100,
    onPrimaryContainer = Violet900,
    secondary = Violet400,
    onSecondary = White,
    background = White,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray500,
    error = Red500,
    onError = White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Violet400,
    onPrimary = Violet950,
    primaryContainer = Violet800,
    onPrimaryContainer = Violet100,
    secondary = Violet300,
    onSecondary = Gray900,
    background = Gray900,
    onBackground = Gray50,
    surface = Gray800,
    onSurface = Gray50,
    error = Red400,
    onError = Gray900,
)

@Composable
fun FindItTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FindItTypography,
        content = content
    )
}
