package dev.lovetest.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = LovePrimary,
    onPrimary = LoveOnPrimary,
    primaryContainer = LovePrimaryContainer,
    onPrimaryContainer = LoveOnPrimaryContainer,
    secondary = LoveSecondary,
    onSecondary = LoveOnPrimary,
    background = LoveSurface,
    onBackground = LoveOnSurface,
    surface = LoveSurface,
    onSurface = LoveOnSurface,
    onSurfaceVariant = LoveOnSurfaceVariant,
    surfaceContainerHigh = LoveOutlineVariant,
    outline = LoveOutline,
    outlineVariant = LoveOutlineVariant,
    errorContainer = LoveErrorContainer,
    onErrorContainer = LoveOnErrorContainer,
)

private val DarkColors = darkColorScheme(
    primary = LoveSecondary,
    onPrimary = LoveOnPrimary,
    primaryContainer = LoveOnPrimaryContainer,
    onPrimaryContainer = LovePrimaryContainer,
    background = LoveOnSurface,
    onBackground = LoveSurface,
    surface = LoveOnSurface,
    onSurface = LoveSurface,
)

@Composable
fun LoveTestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = LoveTypography,
        content = content,
    )
}
