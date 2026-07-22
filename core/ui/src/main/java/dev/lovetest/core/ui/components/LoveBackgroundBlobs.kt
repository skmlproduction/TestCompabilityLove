package dev.lovetest.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import dev.lovetest.core.ui.theme.LoveBgGlowBottom
import dev.lovetest.core.ui.theme.LoveHeroEnd

@Composable
fun LoveHubBackgroundBlobs(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = LoveHeroEnd.copy(alpha = 0.42f),
            radius = 220.dp.toPx(),
            center = Offset(size.width * 0.83f, 140.dp.toPx()),
        )
        drawCircle(
            color = LoveBgGlowBottom.copy(alpha = 0.45f),
            radius = 170.dp.toPx(),
            center = Offset(120.dp.toPx(), 600.dp.toPx()),
        )
        drawCircle(
            color = LoveBgGlowBottom.copy(alpha = 0.3f),
            radius = 280.dp.toPx(),
            center = Offset(size.width * 0.89f, size.height * 0.88f),
        )
    }
}
