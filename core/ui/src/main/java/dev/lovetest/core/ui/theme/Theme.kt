package dev.lovetest.core.ui.theme

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
    /** v2 editorial romance ships light-only; pass true only for experiments. */
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = LoveTypography,
        content = content,
    )
}
