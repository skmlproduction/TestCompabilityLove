package dev.lovetest.app.ui.features

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.theme.LovePrimary
import kotlin.math.cos
import kotlin.math.sin

internal const val WHEEL_SEGMENT_COUNT = 8

internal val wheelSegmentColors = listOf(
    Color(0xFFC2185B),
    Color(0xFFE91E63),
    Color(0xFFF48FB1),
    Color(0xFFFCE4EC),
    Color(0xFFC2185B),
    Color(0xFFAD1457),
    Color(0xFFF8BBD0),
    Color(0xFFE91E63),
)

internal val wheelSegmentTextColors = listOf(
    Color.White,
    Color.White,
    Color.White,
    Color(0xFF880E4F),
    Color(0xFF880E4F),
    Color.White,
    Color.White,
    Color.White,
)

@Composable
internal fun WheelDisc(
    segments: List<String>,
    rotationDegrees: Float,
    modifier: Modifier = Modifier,
    discSize: Dp = 280.dp,
    showHub: Boolean = true,
) {
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .size(discSize)
            .graphicsLayer { rotationZ = rotationDegrees },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 6.dp.toPx()
            val outer = this.size.minDimension / 2f
            val arcSize = Size(outer * 2f, outer * 2f)
            val topLeft = Offset(center.x - outer, center.y - outer)
            val sweep = 360f / WHEEL_SEGMENT_COUNT
            wheelSegmentColors.forEachIndexed { index, color ->
                drawArc(
                    color = color,
                    startAngle = -90f + index * sweep,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = topLeft,
                    size = arcSize,
                )
            }
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = outer - 12.dp.toPx(),
                center = center,
                style = Stroke(width = 4.dp.toPx()),
            )
            drawCircle(
                color = Color.White,
                radius = outer,
                center = center,
                style = Stroke(width = stroke),
            )
        }
        segments.take(WHEEL_SEGMENT_COUNT).forEachIndexed { index, label ->
            val angleRad = Math.toRadians((index * 45.0 - 67.5))
            val radiusPx = with(density) { (discSize.value / 2f - 52f).dp.toPx() }
            val dx = (cos(angleRad) * radiusPx).toFloat()
            val dy = (sin(angleRad) * radiusPx).toFloat()
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = wheelSegmentTextColors.getOrElse(index) { Color.White },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = with(density) { dx.toDp() }, y = with(density) { dy.toDp() }),
            )
        }
        if (showHub) {
            WheelCenterHub(Modifier.align(Alignment.Center))
        }
    }
}

@Composable
internal fun WheelPointer(modifier: Modifier = Modifier) {
    Canvas(modifier.size(48.dp, 40.dp)) {
        val w = size.width
        val pathTop = Offset(w / 2f, 0f)
        drawLine(Color(0xFF1C1B1F), pathTop, Offset(0f, size.height), strokeWidth = 4f)
        drawLine(Color(0xFF1C1B1F), pathTop, Offset(w, size.height), strokeWidth = 4f)
        drawLine(LovePrimary, Offset(w / 2f, 8f), Offset(12f, size.height - 4f), strokeWidth = 4f)
        drawLine(LovePrimary, Offset(w / 2f, 8f), Offset(w - 12f, size.height - 4f), strokeWidth = 4f)
    }
}

@Composable
internal fun BoxScope.WheelCenterHub(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .align(Alignment.Center),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(color = Color.White, radius = size.minDimension / 2f)
            drawCircle(
                color = LovePrimary,
                radius = size.minDimension / 2f,
                style = Stroke(width = 5.dp.toPx()),
            )
        }
        LoveHeartIcon(Modifier.size(28.dp), color = LovePrimary)
    }
}

internal fun wheelRotationForSegment(segmentIndex: Int, extraSpins: Int = 5): Float {
    val index = segmentIndex.coerceIn(0, WHEEL_SEGMENT_COUNT - 1)
    return 360f * extraSpins + index * 45f
}
