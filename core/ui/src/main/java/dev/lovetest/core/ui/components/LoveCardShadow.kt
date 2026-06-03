package dev.lovetest.core.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val ShadowAmbient = Color(0x141C1B1F)
private val DefaultSpotTint = Color(0xFFC2185B)

/**
 * Approximates SVG `cardShadow`: neutral depth (dy=16, blur≈24) + tinted accent (dy=2, blur≈7).
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
    spotColor = spotTint.copy(alpha = 0.12f),
)

object LoveCardShadowElevation {
    val Hero = 16.dp
    val Card = 8.dp
    val Subtle = 4.dp
}
