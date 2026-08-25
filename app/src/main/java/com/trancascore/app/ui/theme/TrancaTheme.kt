package com.trancascore.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF18864B),
    onPrimary = Color.White,
    secondary = Color(0xFFB85C00),
    tertiary = Color(0xFF1769AA),
    surface = Color(0xFFFFFBFF),
    background = Color(0xFFF7F5F8),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF64D998),
    secondary = Color(0xFFFFB77A),
    tertiary = Color(0xFF9CCBFF),
)

@Composable
fun TrancaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
