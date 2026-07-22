package dev.lovetest.app.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveTypographyTokens
import kotlin.math.roundToInt

@Composable
fun LoveHeroPercentRing(
    percent: Int,
    label: String,
    high: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier,
    ringSize: Dp = 260.dp,
    ringStroke: Dp? = null,
) {
    val target = percent.coerceIn(0, 100)
    val resolvedStroke = ringStroke ?: Dp(ringSize.value * 20f / 300f)
    val animatedPercent = remember { Animatable(0f) }
    LaunchedEffect(target) {
        animatedPercent.snapTo(0f)
        animatedPercent.animateTo(
            targetValue = target.toFloat(),
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        )
    }
    val displayPercent = animatedPercent.value.roundToInt()
    val sweep = animatedPercent.value / 100f

    Box(
        modifier = modifier
            .size(ringSize)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(ringSize)) {
            val stroke = resolvedStroke.toPx()
            val diameter = size.minDimension - stroke
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f,
            )
            val arcSize = Size(diameter, diameter)
            drawArc(
                color = Color.White.copy(alpha = if (high) 0.35f else 0.5f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = Color.White.copy(alpha = if (high) 1f else 0.7f),
                startAngle = -90f,
                sweepAngle = 360f * sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawCircle(
                color = Color.White.copy(alpha = if (high) 0.2f else 0.15f),
                radius = diameter / 2f - stroke,
                center = center,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$displayPercent%",
                style = LoveTypographyTokens.percentForRing(ringSize),
                color = if (high) Color.White else LoveOnSurfaceVariant,
            )
            Text(
                text = label,
                style = LoveTypographyTokens.PercentLabel,
                color = if (high) {
                    Color.White.copy(0.95f)
                } else {
                    Color(0xFF79747E)
                },
            )
        }
    }
}
