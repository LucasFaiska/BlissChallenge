package com.lucasoliveira.bliss.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BlissColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = White,
    primaryContainer = IndigoDark,
    onPrimaryContainer = White,
    secondary = Pink,
    onSecondary = White,
    background = Indigo,
    onBackground = White,
    surface = Indigo,
    onSurface = White,
    surfaceVariant = ButtonGray,
    onSurfaceVariant = Black,
    error = ErrorRed,
    onError = White,
)

@Composable
fun BlissTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlissColorScheme,
        content = content,
    )
}
