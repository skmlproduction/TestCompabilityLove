package dev.lovetest.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DefaultSpotTint = Color(0xFFC2185B)

@Composable
fun LoveShadowCard(
    modifier: Modifier = Modifier,
    shape: Shape,
    shadowElevation: Dp = LoveCardShadowElevation.Card,
    spotTint: Color = DefaultSpotTint,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.loveCardShadow(shape, shadowElevation, spotTint),
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content,
    )
}

@Composable
fun LoveShadowClickableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape,
    shadowElevation: Dp = LoveCardShadowElevation.Card,
    spotTint: Color = DefaultSpotTint,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    border: BorderStroke? = null,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.loveCardShadow(shape, shadowElevation, spotTint),
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content,
    )
}
