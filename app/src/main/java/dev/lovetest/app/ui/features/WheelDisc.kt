package dev.lovetest.app.ui.features

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveWheelPointerGold
import dev.lovetest.core.ui.theme.LoveWheelSegmentColors
import dev.lovetest.core.ui.theme.LoveWheelSegmentTextColors
import kotlin.math.cos
import kotlin.math.sin

internal const val WHEEL_SEGMENT_COUNT = 8

@Composable
internal fun WheelDisc(
    segments: List<String>,
    rotationDegrees: Float,
    modifier: Modifier = Modifier,
    discSize: Dp = 280.dp,
    showHub: Boolean = true,
    showSegmentLabels: Boolean = true,
    contentDescription: String? = null,
) {
    val density = LocalDensity.current
    val sweep = 360f / WHEEL_SEGMENT_COUNT
    Box(
        modifier = modifier
            .size(discSize)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            )
            .graphicsLayer { rotationZ = rotationDegrees },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 6.dp.toPx()
            val outer = this.size.minDimension / 2f
            val arcSize = Size(outer * 2f, outer * 2f)
            val topLeft = Offset(center.x - outer, center.y - outer)
            LoveWheelSegmentColors.forEachIndexed { index, color ->
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
        if (showSegmentLabels) {
            segments.take(WHEEL_SEGMENT_COUNT).forEachIndexed { index, label ->
                val centerAngleDeg = -90f + index * sweep + sweep / 2f
                val angleRad = Math.toRadians(centerAngleDeg.toDouble())
                val labelRadiusPx = with(density) { (discSize.value / 2f - labelRadiusInset(discSize)).dp.toPx() }
                val dx = (cos(angleRad) * labelRadiusPx).toFloat()
                val dy = (sin(angleRad) * labelRadiusPx).toFloat()
                val fontSize = if (label.length > 9) 10.sp else 12.sp
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = fontSize),
                    fontWeight = FontWeight.ExtraBold,
                    color = LoveWheelSegmentTextColors.getOrElse(index) { Color.White },
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .widthIn(max = 72.dp)
                        .offset(x = with(density) { dx.toDp() }, y = with(density) { dy.toDp() }),
                )
            }
        }
        if (showHub) {
            WheelCenterHub(Modifier.align(Alignment.Center))
        }
    }
}

/** Inset from disc edge to segment label center — scales with disc size (screen22 SVG). */
private fun labelRadiusInset(discSize: Dp): Float = when {
    discSize >= 280.dp -> 52f
    discSize >= 200.dp -> 36f
    else -> 28f
}

@Composable
internal fun WheelPointer(modifier: Modifier = Modifier) {
    Canvas(modifier.size(40.dp, 36.dp)) {
        val w = size.width
        val h = size.height
        val outer = Path().apply {
            moveTo(w / 2f, 0f)
            lineTo(0f, h)
            lineTo(w, h)
            close()
        }
        drawPath(outer, Color(0xFF1C1B1F))
        val insetX = 12.dp.toPx()
        val insetTop = 6.dp.toPx()
        val insetBottom = 4.dp.toPx()
        val inner = Path().apply {
            moveTo(w / 2f, insetTop)
            lineTo(insetX, h - insetBottom)
            lineTo(w - insetX, h - insetBottom)
            close()
        }
        drawPath(inner, LoveWheelPointerGold)
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
