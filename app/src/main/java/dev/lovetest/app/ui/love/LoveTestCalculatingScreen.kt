package dev.lovetest.app.ui.love

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.lovetest.app.R
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.theme.LoveBgGlowTop
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutline
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LoveProtocolContainer
import dev.lovetest.core.ui.theme.LoveProtocolPrimary
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSecondary
import dev.lovetest.core.ui.theme.LoveSurface
import org.koin.androidx.compose.koinViewModel

enum class TestCalculatingFlavor {
    Love,
    Protocol,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoveTestCalculatingScreen(
    flavor: TestCalculatingFlavor = TestCalculatingFlavor.Love,
    viewModel: LoveTestFlowViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val calculating by viewModel.calculating.collectAsStateWithLifecycle()
    val isProtocol = flavor == TestCalculatingFlavor.Protocol
    val accent = if (isProtocol) LoveProtocolPrimary else LovePrimary
    val accentContainer = if (isProtocol) LoveProtocolContainer else LovePrimaryContainer
    val loadingCd = stringResource(
        if (isProtocol) R.string.protocol_calculating_cd else R.string.love_test_calculating_cd,
    )

    BackHandler(enabled = calculating) { /* блокируем выход во время расчёта */ }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = loadingCd },
    ) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveCalculatingFloatingHearts(Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(
                                if (isProtocol) R.string.protocol_title else R.string.love_test_title,
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = LoveSurface.copy(alpha = 0.85f),
                    ),
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            ) {
                LoveCalculatingNamesCard(
                    name1 = uiState.name1.ifBlank { "…" },
                    name2 = uiState.name2.ifBlank { "…" },
                    modifier = Modifier.padding(top = 8.dp),
                )

                LoveShadowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    shape = RoundedCornerShape(48.dp),
                    shadowElevation = LoveCardShadowElevation.Card,
                    colors = CardDefaults.cardColors(containerColor = LoveSurface),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(
                                if (isProtocol) R.string.protocol_calculating_title
                                else R.string.love_test_calculating_title,
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = accent,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(
                                if (isProtocol) R.string.protocol_calculating_subtitle
                                else R.string.love_test_calculating_subtitle,
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            color = LoveOnSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                        )

                        LoveCalculatingProgressRing(
                            progress = uiState.progress,
                            accent = accent,
                            accentContainer = accentContainer,
                            modifier = Modifier.padding(top = 28.dp),
                        )

                        Text(
                            text = stringResource(R.string.love_test_calculating_percent_ghost),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                            ),
                            color = accent.copy(alpha = 0.12f),
                            modifier = Modifier.padding(top = 8.dp),
                        )

                        LinearProgressIndicator(
                            progress = { uiState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = accent,
                            trackColor = LoveOutlineVariant,
                            strokeCap = StrokeCap.Round,
                        )

                        CalculatingStepRow(
                            label = stringResource(
                                if (isProtocol) R.string.protocol_calculating_step1
                                else R.string.love_test_calculating_step1,
                            ),
                            stepIndex = 0,
                            activeStep = uiState.activeStep,
                            accent = accent,
                            modifier = Modifier.padding(top = 28.dp),
                        )
                        CalculatingStepRow(
                            label = stringResource(
                                if (isProtocol) R.string.protocol_calculating_step2
                                else R.string.love_test_calculating_step2,
                            ),
                            stepIndex = 1,
                            activeStep = uiState.activeStep,
                            accent = accent,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                        CalculatingStepRow(
                            label = stringResource(
                                if (isProtocol) R.string.protocol_calculating_step3
                                else R.string.love_test_calculating_step3,
                            ),
                            stepIndex = 2,
                            activeStep = uiState.activeStep,
                            accent = accent,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                        )
                    }
                }

                Text(
                    text = stringResource(
                        if (isProtocol) R.string.protocol_calculating_footer
                        else R.string.love_test_calculating_footer,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 32.dp),
                )
            }
        }
    }
}

@Composable
private fun LoveCalculatingNamesCard(
    name1: String,
    name2: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name1,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            LoveHeartIcon(
                modifier = Modifier.size(40.dp),
                color = LovePrimary,
            )
            Text(
                text = name2,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun LoveCalculatingProgressRing(
    progress: Float,
    accent: Color = LovePrimary,
    accentContainer: Color = LovePrimaryContainer,
    modifier: Modifier = Modifier,
) {
    val pulse by rememberInfiniteTransition(label = "heartPulse").animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse",
    )

    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 14.dp.toPx()
            val diameter = size.minDimension - stroke
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f,
            )
            val arcSize = Size(diameter, diameter)
            drawArc(
                color = accentContainer,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                brush = Brush.linearGradient(listOf(accent, LoveSecondary)),
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawCircle(
                color = LoveBgGlowTop,
                radius = diameter / 2f - stroke,
                center = center,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LoveHeartIcon(
                modifier = Modifier.size((72 * pulse).dp),
                color = accent.copy(alpha = 0.95f),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp),
            ) {
                LoveHeartIcon(
                    modifier = Modifier.size(20.dp),
                    color = LoveSecondary.copy(alpha = 0.5f),
                )
                LoveHeartIcon(
                    modifier = Modifier.size(20.dp),
                    color = LoveSecondary.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Composable
private fun CalculatingStepRow(
    label: String,
    stepIndex: Int,
    activeStep: Int,
    accent: Color = LovePrimary,
    modifier: Modifier = Modifier,
) {
    val reached = activeStep >= stepIndex
    val dotColor = if (reached) accent else LoveOutlineVariant
    val textColor = if (reached) LoveOnSurfaceVariant else LoveOutline
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun LoveCalculatingFloatingHearts(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val specs = listOf(
            Triple(0.19f, 0.17f, 0.08f to LoveSecondary),
            Triple(0.76f, 0.13f, 0.10f to LovePrimary),
            Triple(0.15f, 0.50f, 0.07f to LoveSecondary),
            Triple(0.83f, 0.58f, 0.09f to LovePrimary),
            Triple(0.11f, 0.70f, 0.06f to LoveSecondary),
        )
        specs.forEach { (xR, yR, alphaColor) ->
            val (alpha, color) = alphaColor
            drawHeart(
                center = Offset(size.width * xR, size.height * yR),
                radius = 36.dp.toPx(),
                color = color.copy(alpha = alpha),
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeart(
    center: Offset,
    radius: Float,
    color: Color,
) {
    val path = Path().apply {
        moveTo(center.x, center.y + radius * 0.3f)
        cubicTo(
            center.x - radius, center.y - radius * 0.2f,
            center.x - radius, center.y - radius,
            center.x, center.y - radius * 0.35f,
        )
        cubicTo(
            center.x + radius, center.y - radius,
            center.x + radius, center.y - radius * 0.2f,
            center.x, center.y + radius * 0.3f,
        )
        close()
    }
    drawPath(path, color, style = Fill)
}
