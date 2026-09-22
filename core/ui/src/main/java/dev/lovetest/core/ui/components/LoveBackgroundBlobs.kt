package dev.lovetest.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import dev.lovetest.core.ui.theme.VelvetGlowCoral
import dev.lovetest.core.ui.theme.VelvetGlowRose

/** Velvet: ambient rose/coral glows on the dark background. */
@Composable
fun LoveHubBackgroundBlobs(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = VelvetGlowRose,
            radius = 240.dp.toPx(),
            center = Offset(size.width * 0.86f, 130.dp.toPx()),
        )
        drawCircle(
            color = VelvetGlowCoral,
            radius = 190.dp.toPx(),
            center = Offset(100.dp.toPx(), size.height * 0.72f),
        )
        drawCircle(
            color = VelvetGlowRose.copy(alpha = 0.10f),
            radius = 280.dp.toPx(),
            center = Offset(size.width * 0.88f, size.height * 0.92f),
        )
    }
}
