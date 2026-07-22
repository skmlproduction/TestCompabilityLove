package dev.lovetest.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import dev.lovetest.core.ui.theme.LoveBgGlowBottom
import dev.lovetest.core.ui.theme.LoveBgGlowTop
import dev.lovetest.core.ui.theme.LoveHeroEnd
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveSecondary
import dev.lovetest.core.ui.theme.LoveSurface

@Composable
fun LoveGradientBackground(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(LoveBgGlowTop, LoveSurface, LoveBgGlowBottom),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 2000f),
                ),
            ),
    )
}

@Composable
fun LoveHeroGradientBrush(): Brush =
    Brush.linearGradient(
        colors = listOf(LovePrimary, LoveSecondary, LoveHeroEnd),
        start = Offset.Zero,
        end = Offset(800f, 600f),
    )
