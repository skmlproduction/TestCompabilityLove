package dev.lovetest.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import dev.lovetest.core.ui.theme.VelvetAccentCoral
import dev.lovetest.core.ui.theme.VelvetAccentRose
import dev.lovetest.core.ui.theme.VelvetBgBrush

/** Velvet: full-screen deep-plum vertical gradient. */
@Composable
fun LoveGradientBackground(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(VelvetBgBrush))
}

/** Velvet hero/CTA gradient — rose → coral, diagonal. */
@Composable
fun LoveHeroGradientBrush(): Brush =
    Brush.linearGradient(
        colors = listOf(VelvetAccentRose, VelvetAccentCoral),
        start = Offset.Zero,
        end = Offset(800f, 600f),
    )
