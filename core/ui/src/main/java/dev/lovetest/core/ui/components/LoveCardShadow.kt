package dev.lovetest.core.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.lovetest.core.ui.theme.VelvetAccentRose

private val ShadowAmbient = Color(0x59000000)
private val DefaultSpotTint = VelvetAccentRose

/**
 * Velvet: deep neutral drop + rose glow (dy=2, blur≈7) — читается на тёмном фоне.
 */
fun Modifier.loveCardShadow(
    shape: Shape,
    elevation: Dp = LoveCardShadowElevation.Hero,
    spotTint: Color = DefaultSpotTint,
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = ShadowAmbient,
    spotColor = spotTint.copy(alpha = 0.35f),
)

object LoveCardShadowElevation {
    val Hero = 18.dp
    val Card = 10.dp
    val Subtle = 4.dp
}
