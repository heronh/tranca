package com.trancascore.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF12894F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3F5E1),
    onPrimaryContainer = Color(0xFF00391E),
    secondary = Color(0xFFE8710A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEBDC),
    onSecondaryContainer = Color(0xFF5A2A00),
    tertiary = Color(0xFF1A6FE0),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0EAFD),
    onTertiaryContainer = Color(0xFF0B3A82),
    background = Color(0xFFF3F5F7),
    surface = Color(0xFFF3F5F7),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F9FA),
    surfaceContainer = Color(0xFFEEF1F4),
    outlineVariant = Color(0xFFE1E5EA),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF5BD896),
    onPrimary = Color(0xFF00391E),
    primaryContainer = Color(0xFF0E5C36),
    onPrimaryContainer = Color(0xFFD3F5E1),
    secondary = Color(0xFFFFB77A),
    onSecondary = Color(0xFF4A2300),
    secondaryContainer = Color(0xFF4F2D10),
    onSecondaryContainer = Color(0xFFFFDCC2),
    tertiary = Color(0xFF9CC3FF),
    onTertiary = Color(0xFF002F66),
    tertiaryContainer = Color(0xFF16345E),
    onTertiaryContainer = Color(0xFFD8E5FF),
    background = Color(0xFF111416),
    surface = Color(0xFF111416),
    surfaceContainerLowest = Color(0xFF1A1E21),
    surfaceContainerLow = Color(0xFF1D2124),
    surfaceContainer = Color(0xFF23282B),
    outlineVariant = Color(0xFF30363A),
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
