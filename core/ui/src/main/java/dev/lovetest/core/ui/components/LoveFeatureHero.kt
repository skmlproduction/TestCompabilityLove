package dev.lovetest.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * Gradient hero shell for feature input screens (DESIGN_SYSTEM hero pattern).
 * Use [content] for title, decor, and badges — shared 54dp radius + min height.
 */
@Composable
fun LoveFeatureHero(
    modifier: Modifier = Modifier,
    brush: Brush = LoveHeroGradientBrush(),
    minHeight: Dp = LoveLayout.FeatureHeroMinHeight,
    shape: Shape = LoveLayout.HeroShape,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Hero)
            .clip(shape)
            .background(brush),
        content = content,
    )
}
