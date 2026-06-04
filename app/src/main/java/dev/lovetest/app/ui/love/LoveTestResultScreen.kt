package dev.lovetest.app.ui.love

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.share.LoveShareResultOverlay
import dev.lovetest.app.ui.share.rememberLoveShareSheet
import dev.lovetest.app.util.buildLoveShareText
import dev.lovetest.app.ui.common.LoveHeroPercentRing
import dev.lovetest.core.domain.LoveScoreCalculator
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveHeroGradientBrush
import dev.lovetest.core.ui.components.LoveOutlinedButton
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveBgGlowTop
import dev.lovetest.core.ui.theme.LoveErrorContainer
import dev.lovetest.core.ui.theme.LoveOnErrorContainer
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveResultMutedHeroBrush
import dev.lovetest.core.ui.theme.LoveSecondary
import dev.lovetest.core.ui.theme.LoveSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoveTestResultScreen(
    onShare: () -> Unit,
    onTryAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val percent = LoveTestSession.percent
    val high = percent >= LoveScoreCalculator.DEFAULT_HIGH_THRESHOLD
    val name1 = LoveTestSession.name1.ifBlank { "…" }
    val name2 = LoveTestSession.name2.ifBlank { "…" }
    val percentCd = stringResource(R.string.love_test_percent_cd)
    val harmonyTag = stringResource(
        if (high) R.string.love_test_result_high_tag else R.string.love_test_result_low_tag,
    )
    val shareSheet = rememberLoveShareSheet(debugAutoOpenId = "share_result_card")
    val context = LocalContext.current
    val shareText = remember(context, percent, name1, name2) {
        context.buildLoveShareText(percent, name1, name2)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        if (high) {
            LoveResultFloatingHearts(Modifier.fillMaxSize())
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.love_test_result_title),
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
                LoveResultHeroCard(
                    high = high,
                    name1 = name1,
                    name2 = name2,
                    percent = percent,
                    percentLabel = stringResource(
                        if (high) R.string.love_test_result_percent_label_love
                        else R.string.compatibility_label,
                    ),
                    heroTag = stringResource(
                        if (high) R.string.love_test_result_high_tag
                        else R.string.love_test_result_low_tag,
                    ),
                    percentContentDescription = percentCd,
                    modifier = Modifier.padding(top = 8.dp),
                )

                if (high) {
                    LoveResultHighMessageCard(modifier = Modifier.padding(top = 20.dp))
                    LoveSharePreviewCard(
                        percent = percent,
                        names = "$name1 + $name2",
                        modifier = Modifier.padding(top = 20.dp),
                    )
                } else {
                    LoveResultLowMessageCard(modifier = Modifier.padding(top = 20.dp))
                    LoveResultTipCard(modifier = Modifier.padding(top = 16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        BrokenHeartDecor(modifier = Modifier.size(80.dp))
                    }
                }

                LovePrimaryButton(
                    text = stringResource(R.string.love_test_share_cta),
                    onClick = shareSheet.open,
                    modifier = Modifier.padding(top = 20.dp),
                )
                LoveOutlinedButton(
                    text = stringResource(R.string.love_test_try_again),
                    onClick = onTryAgain,
                    modifier = Modifier.padding(top = 12.dp),
                )
                LoveResultHomeButton(
                    text = stringResource(R.string.love_test_back_home),
                    onClick = onHome,
                    modifier = Modifier.padding(top = 12.dp),
                )

                Text(
                    text = stringResource(
                        if (high) R.string.love_test_result_disclaimer_long
                        else R.string.result_entertainment_only,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 32.dp),
                )
            }
        }
        LoveShareResultOverlay(
            sheet = shareSheet,
            percent = percent,
            name1 = name1,
            name2 = name2,
            harmonyTag = harmonyTag,
            shareText = shareText,
            onShare = onShare,
        )
    }
}

@Composable
private fun LoveResultHeroCard(
    high: Boolean,
    name1: String,
    name2: String,
    percent: Int,
    percentLabel: String,
    heroTag: String,
    percentContentDescription: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(48.dp),
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (high) LoveHeroGradientBrush() else LoveResultMutedHeroBrush)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            if (high) {
                Canvas(Modifier.matchParentSize()) {
                    drawCircle(
                        Color.White.copy(0.1f),
                        100.dp.toPx(),
                        Offset(size.width * 0.88f, 80.dp.toPx()),
                    )
                    drawCircle(
                        Color.White.copy(0.08f),
                        80.dp.toPx(),
                        Offset(60.dp.toPx(), size.height * 0.85f),
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = name1,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    LoveHeartIcon(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .size(28.dp),
                        color = Color.White,
                    )
                    Text(
                        text = name2,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                LoveHeroPercentRing(
                    percent = percent,
                    label = percentLabel,
                    high = high,
                    contentDescription = percentContentDescription,
                    modifier = Modifier.padding(top = 20.dp),
                )

                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = if (high) 0.22f else 0.25f))
                        .padding(vertical = 14.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = heroTag,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoveResultHighMessageCard(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
            Text(
                text = stringResource(R.string.love_test_result_high_card_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
            )
            Text(
                text = stringResource(R.string.love_test_result_high_body1),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = stringResource(R.string.love_test_result_high_body2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = stringResource(R.string.love_test_result_high_body3),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LovePrimaryContainer)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.love_test_result_high_chip),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun LoveResultLowMessageCard(modifier: Modifier = Modifier) {
    val threshold = LoveScoreCalculator.DEFAULT_HIGH_THRESHOLD
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LoveErrorContainer),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = LovePrimary,
                    )
                }
                Text(
                    text = stringResource(R.string.love_test_result_low_message),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnErrorContainer,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
            Text(
                text = stringResource(R.string.love_test_result_low_body1),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnErrorContainer.copy(alpha = 0.88f),
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.love_test_result_low_body2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnErrorContainer.copy(alpha = 0.88f),
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = stringResource(R.string.love_test_result_low_body3),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnErrorContainer.copy(alpha = 0.88f),
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = stringResource(R.string.love_test_result_low_body4),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnErrorContainer.copy(alpha = 0.88f),
                modifier = Modifier.padding(top = 4.dp),
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.7f))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.love_test_result_low_chip, threshold),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnErrorContainer,
                )
            }
        }
    }
}

@Composable
private fun LoveResultTipCard(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            Text(
                text = stringResource(R.string.love_test_result_low_tip_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
            )
            Text(
                text = stringResource(R.string.love_test_result_low_tip_body),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun LoveSharePreviewCard(
    percent: Int,
    names: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
        border = BorderStroke(2.dp, LovePrimaryContainer),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LoveHeroGradientBrush()),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = names,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                )
                Text(
                    text = stringResource(R.string.love_test_share_preview_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.love_test_share_preview_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun LoveResultHomeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(44.dp))
            .background(LovePrimaryContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = LoveOnPrimaryContainer,
        )
    }
}

@Composable
private fun LoveResultFloatingHearts(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val specs = listOf(
            Triple(0.17f, 0.13f, 0.10f to LoveSecondary),
            Triple(0.80f, 0.10f, 0.12f to LovePrimary),
            Triple(0.09f, 0.63f, 0.08f to LoveSecondary),
        )
        specs.forEach { (xR, yR, alphaColor) ->
            val (alpha, color) = alphaColor
            drawResultHeart(
                center = Offset(size.width * xR, size.height * yR),
                radius = 48.dp.toPx(),
                color = color.copy(alpha = alpha),
            )
        }
    }
}

@Composable
private fun BrokenHeartDecor(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val color = LoveOnSurfaceVariant.copy(alpha = 0.15f)
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.35f
        val path = Path().apply {
            moveTo(cx, cy + r * 0.3f)
            cubicTo(cx - r, cy - r * 0.2f, cx - r, cy - r, cx, cy - r * 0.35f)
            cubicTo(cx + r, cy - r, cx + r, cy - r * 0.2f, cx, cy + r * 0.3f)
            close()
        }
        drawPath(path, color, style = Fill)
        drawLine(
            color = color,
            start = Offset(cx, cy - r * 0.2f),
            end = Offset(cx, cy + r * 0.55f),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawResultHeart(
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
