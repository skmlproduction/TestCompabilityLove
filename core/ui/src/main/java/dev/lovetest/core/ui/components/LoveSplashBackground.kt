package dev.lovetest.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoveSplashBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(Color(0xFFF8BBD0).copy(0.45f), 220.dp.toPx(), Offset(size.width * 0.85f, 200.dp.toPx()))
        drawCircle(Color(0xFFFCE4EC).copy(0.55f), 180.dp.toPx(), Offset(140.dp.toPx(), 520.dp.toPx()))
        drawCircle(Color(0xFFF8BBD0).copy(0.35f), 280.dp.toPx(), Offset(size.width * 0.91f, size.height * 0.82f))
        drawCircle(Color(0xFFFCE4EC).copy(0.4f), 160.dp.toPx(), Offset(80.dp.toPx(), size.height * 0.73f))
        // Decorative small hearts (simplified)
        listOf(
            Triple(0.22f, 0.18f, 0.14f),
            Triple(0.78f, 0.15f, 0.12f),
            Triple(0.12f, 0.66f, 0.1f),
        ).forEach { (fx, fy, alpha) ->
            val cx = size.width * fx
            val cy = size.height * fy
            val r = 28.dp.toPx()
            drawCircle(Color(0xFFE91E63).copy(alpha), r, Offset(cx, cy))
        }
    }
}
