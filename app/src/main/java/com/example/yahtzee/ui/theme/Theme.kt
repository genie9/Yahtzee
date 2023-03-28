package com.example.yahtzee.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


private val LightColorPalette = lightColors(
    background = LightBrown,
    surface = DarkBrown,
    onSurface = MidBrown,
    primary = LightGreen,
    onPrimary = MidBrown,
    secondary = DarkGreen
)

private val DarkColorPalette = darkColors(
    background = DarkBrown,
    surface = MidBrown,
    primary = MidBrown,
    secondary = Grey100
)

@Composable
fun YahtzeeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        LightColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}