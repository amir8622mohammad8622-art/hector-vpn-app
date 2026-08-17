package com.example.vpnapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    background = BackgroundDark,
    surface = SurfaceDark,
    primary = AccentGreen,
    secondary = AccentBlue,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Color(0xFFCF6679)
)

@Composable
fun OxeronVpnTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
