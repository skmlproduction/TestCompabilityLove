package dev.lovetest.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

/**
 * Velvet dark — default product theme.
 * Glass surfaces are applied by components; here surface = solid base for scrims/dialogs.
 */
private val VelvetColors = darkColorScheme(
    primary = VelvetAccentRose,
    onPrimary = VelvetText,
    primaryContainer = VelvetCardStrong,
    onPrimaryContainer = VelvetPinkSoft,
    secondary = VelvetAccentCoral,
    onSecondary = VelvetBgBottom,
    tertiary = VelvetGold,
    background = VelvetBgMid,
    onBackground = VelvetText,
    surface = VelvetBgMid,
    onSurface = VelvetText,
    surfaceVariant = VelvetCardStrong,
    onSurfaceVariant = VelvetTextSecondary,
    surfaceContainerHigh = VelvetCardStrong,
    outline = VelvetCardBorderStrong,
    outlineVariant = VelvetCardBorder,
    errorContainer = Color(0xFF5C1228),
    onErrorContainer = VelvetText,
)

@Composable
fun LoveTestTheme(
    /** Legacy light editorial theme — только для экспериментов/старого preview. */
    legacyLight: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (legacyLight) LightColors else VelvetColors,
        typography = LoveTypography,
        content = content,
    )
}
