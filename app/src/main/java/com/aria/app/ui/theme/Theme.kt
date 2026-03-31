package com.aria.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA78BFA),
    secondary = Color(0xFF38BDF8),
    background = Color(0xFF0B1020)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6D28D9),
    secondary = Color(0xFF0284C7),
    background = Color(0xFFEDE9FE)
)

@Composable
fun AriaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
