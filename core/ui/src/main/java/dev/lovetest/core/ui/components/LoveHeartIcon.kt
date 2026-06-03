package dev.lovetest.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill

@Composable
fun LoveHeartIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w / 2f, h * 0.88f)
            cubicTo(w * 0.1f, h * 0.55f, w * 0.05f, h * 0.2f, w * 0.28f, h * 0.12f)
            cubicTo(w * 0.42f, h * 0.06f, w * 0.5f, h * 0.18f, w / 2f, h * 0.28f)
            cubicTo(w * 0.5f, h * 0.18f, w * 0.58f, h * 0.06f, w * 0.72f, h * 0.12f)
            cubicTo(w * 0.95f, h * 0.2f, w * 0.9f, h * 0.55f, w / 2f, h * 0.88f)
            close()
        }
        drawPath(path, color, style = Fill)
    }
}
