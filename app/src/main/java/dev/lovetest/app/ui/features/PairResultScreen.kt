package dev.lovetest.app.ui.features

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.common.LoveHeroPercentRing
import dev.lovetest.app.ui.share.LoveShareResultOverlay
import dev.lovetest.app.ui.share.rememberLoveShareSheet
import kotlinx.coroutines.delay
import dev.lovetest.app.util.buildLoveShareText
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LoveOutlinedButton
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSurface

private val PairResultHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFAD1457),
        Color(0xFFC2185B),
        Color(0xFFE91E63),
        Color(0xFFF48FB1),
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairResultScreen(
    onShare: () -> Unit,
    onTryAnother: () -> Unit,
    onHome: () -> Unit,
) {
    val percent = LoveTestSession.percent
    val name1 = LoveTestSession.name1.ifBlank { "…" }
    val name2 = LoveTestSession.name2.ifBlank { "…" }
    val metrics = LoveTestSession.pairMetrics
    val initial1 = name1.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val initial2 = name2.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val namesLine = "$name1 + $name2"
    val percentCd = stringResource(R.string.pair_percent_cd)
    val harmonyTag = stringResource(R.string.pair_harmony_tag)
    val shareSheet = rememberLoveShareSheet()
    val context = LocalContext.current
    val shareText = remember(context, percent, name1, name2) {
        context.buildLoveShareText(percent, name1, name2)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.pair_result_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
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
                PairResultHeroCard(
                    initial1 = initial1,
                    initial2 = initial2,
                    namesLine = namesLine,
                    percent = percent,
                    percentCd = percentCd,
                    modifier = Modifier.padding(top = 8.dp),
                )
                PairMetricsCard(
                    metrics = metrics,
                    modifier = Modifier.padding(top = 20.dp),
                )
                LovePrimaryButton(
                    text = stringResource(R.string.love_test_share_cta),
                    onClick = shareSheet.open,
                    modifier = Modifier.padding(top = 24.dp),
                )
                LoveOutlinedButton(
                    text = stringResource(R.string.pair_try_another),
                    onClick = onTryAnother,
                    modifier = Modifier.padding(top = 12.dp),
                )
                PairResultHomeButton(
                    text = stringResource(R.string.love_test_back_home),
                    onClick = onHome,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = stringResource(R.string.result_entertainment_only),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                )
                PairSharePreviewCard(
                    percent = percent,
                    namesLine = namesLine,
                    initial1 = initial1,
                    initial2 = initial2,
                    modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
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
private fun PairResultHeroCard(
    initial1: String,
    initial2: String,
    namesLine: String,
    percent: Int,
    percentCd: String,
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
                .background(PairResultHeroBrush)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PairResultAvatar(initial1)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoveHeartIcon(Modifier.size(22.dp), color = LovePrimary)
                    }
                    PairResultAvatar(initial2)
                }
                Text(
                    text = namesLine,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp),
                )
                LoveHeroPercentRing(
                    percent = percent,
                    label = stringResource(R.string.pair_result_percent_label),
                    high = true,
                    contentDescription = percentCd,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.22f))
                        .padding(vertical = 14.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.pair_harmony_tag),
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
private fun PairResultAvatar(letter: String) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
        )
    }
}

@Composable
private fun PairMetricsCard(
    metrics: dev.lovetest.core.domain.PairMetrics?,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
            Text(
                text = stringResource(R.string.pair_metrics_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
            )
            metrics?.let { m ->
                PairMetricBar(
                    label = stringResource(R.string.pair_metric_overall),
                    value = m.connection,
                    color = LovePrimary,
                    modifier = Modifier.padding(top = 20.dp),
                    animationDelayMs = 0,
                )
                PairMetricBar(
                    label = stringResource(R.string.pair_metric_trust),
                    value = m.trust,
                    color = Color(0xFFE91E63),
                    modifier = Modifier.padding(top = 16.dp),
                    animationDelayMs = 120,
                )
                PairMetricBar(
                    label = stringResource(R.string.pair_metric_passion),
                    value = m.passion,
                    color = Color(0xFFF48FB1),
                    modifier = Modifier.padding(top = 16.dp),
                    animationDelayMs = 240,
                )
            }
            Text(
                text = stringResource(R.string.pair_result_body1),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = stringResource(R.string.pair_result_body2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
            )
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LovePrimaryContainer)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.pair_test_badge),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun PairMetricBar(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier,
    animationDelayMs: Int = 0,
) {
    val pct = value.coerceIn(0, 100)
    val targetFraction = pct / 100f
    val animatedFraction = remember { Animatable(0f) }
    LaunchedEffect(value, animationDelayMs) {
        animatedFraction.snapTo(0f)
        if (animationDelayMs > 0) {
            delay(animationDelayMs.toLong())
        }
        animatedFraction.animateTo(
            targetValue = targetFraction,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = LoveOnSurfaceVariant,
            )
            Text(
                text = "$pct%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = LovePrimary,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF3EDF7)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction.value)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun PairSharePreviewCard(
    percent: Int,
    namesLine: String,
    initial1: String,
    initial2: String,
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
            Row(horizontalArrangement = Arrangement.spacedBy((-12).dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LovePrimaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(initial1, fontWeight = FontWeight.ExtraBold, color = LovePrimary)
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF8BBD0)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(initial2, fontWeight = FontWeight.ExtraBold, color = Color(0xFF880E4F))
                }
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "$percent% — $namesLine",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                )
                Text(
                    text = stringResource(R.string.pair_share_preview_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.pair_share_preview_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun PairResultHomeButton(
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
