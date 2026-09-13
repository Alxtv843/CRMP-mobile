package com.crmp.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CrMpDarkColors = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    secondary = GreenLight,
    onSecondary = Color.Black,
    tertiary = AccentAmber,
    background = BgDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error = Danger,
)

@Composable
fun CRMPTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CrMpDarkColors,
        content = content,
    )
}
