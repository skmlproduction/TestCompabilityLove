package dev.lovetest.app.ui.features

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.navigationBarsPadding
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.common.LoveFeatureResultActions
import dev.lovetest.app.ui.common.LoveFeatureResultHomeButton
import dev.lovetest.app.ui.common.LoveHeroPercentRing
import dev.lovetest.app.ui.share.LoveShareResultOverlay
import dev.lovetest.app.ui.share.rememberLoveShareSheet
import dev.lovetest.app.util.buildProtocolShareText
import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import dev.lovetest.core.domain.LoveScoreCalculator
import dev.lovetest.core.domain.ProtocolSignals
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveProtocolContainer
import dev.lovetest.core.ui.theme.LoveProtocolHeroGradientColors
import dev.lovetest.core.ui.theme.LoveProtocolMutedHeroBrush
import dev.lovetest.core.ui.theme.LoveProtocolPrimary
import dev.lovetest.core.ui.theme.LoveProtocolPrimaryDark
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val ProtocolHeroBrush = Brush.linearGradient(colors = LoveProtocolHeroGradientColors)

private enum class ProtocolSummaryTone {
    Success,
    Warning,
    Neutral,
}

@Composable
fun ProtocolResultScreen(
    onShare: () -> Unit,
    onTryAnother: () -> Unit,
    onHome: () -> Unit,
) {
    val name1 = LoveTestSession.name1.ifBlank { "…" }
    val name2 = LoveTestSession.name2.ifBlank { "…" }
    val percent = LoveTestSession.percent
    val hasSession = LoveTestSession.hasLoveResult()

    if (!hasSession) {
        ProtocolResultEmptyState(onHome = onHome)
        return
    }

    val signalsFromSession = LoveTestSession.protocolSignals
    val signalsFallback = remember(name1, name2) {
        DefaultLoveScoreCalculator().protocolSignals(name1, name2)
    }
    val signals = signalsFromSession ?: signalsFallback
    val signalsRecovered = signalsFromSession == null

    val high = LoveScoreCalculator.isHighScore(percent)
    val namesLine = "$name1 + $name2"
    val shareSheet = rememberLoveShareSheet()
    val context = LocalContext.current
    val shareText = remember(context, percent, name1, name2) {
        context.buildProtocolShareText(percent, name1, name2)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .loveEdgeToEdgeScreenPadding(includeNavigationBar = false)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            LoveFeatureTopBar(
                title = stringResource(R.string.protocol_result_title),
            )
            ProtocolResultHero(
                    namesLine = namesLine,
                    percent = percent,
                    high = high,
                    modifier = Modifier.padding(top = 8.dp),
                )

                if (signalsRecovered) {
                    Text(
                        text = stringResource(R.string.protocol_signals_fallback_note),
                        style = LoveTypographyTokens.CardCaption,
                        color = LoveOnSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }

                if (!high) {
                    ProtocolLowWarningCard(modifier = Modifier.padding(top = 20.dp))
                    ProtocolLowTipCard(modifier = Modifier.padding(top = 16.dp))
                }

                // CTAs before summary cards so Share stays above the fold.
                LoveFeatureResultActions(
                    tryAgainLabel = stringResource(R.string.protocol_try_again),
                    onShare = shareSheet.open,
                    onTryAgain = onTryAnother,
                    onHome = onHome,
                    primaryContainerColor = if (high) LovePrimary else LoveProtocolPrimary,
                    outlinedContentColor = LoveProtocolPrimary,
                    homeBackgroundColor = LoveProtocolContainer,
                    homeContentColor = LoveProtocolPrimaryDark,
                )

                ProtocolSummaryCard(
                    signals = signals,
                    high = high,
                    modifier = Modifier.padding(top = if (high) 20.dp else 16.dp),
                )

                ProtocolVerdictCard(
                    signals = signals,
                    modifier = Modifier.padding(top = 16.dp),
                )

                if (high) {
                    ProtocolSharePreviewCard(
                        percent = percent,
                        names = namesLine,
                        modifier = Modifier.padding(top = 20.dp, bottom = 32.dp),
                    )
                } else {
                    Box(modifier = Modifier.padding(bottom = 32.dp))
                }
        }
        LoveShareResultOverlay(
            sheet = shareSheet,
            percent = percent,
            name1 = name1,
            name2 = name2,
            harmonyTag = protocolVerdictTitle(signals.verdictBand),
            shareText = shareText,
            high = high,
            onShareFallback = onShare,
        )
    }
}

@Composable
private fun ProtocolResultEmptyState(onHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .loveEdgeToEdgeScreenPadding()
            .padding(top = 120.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.protocol_result_title),
                style = LoveTypographyTokens.FeatureScreenTitle,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.protocol_signals_fallback_note),
                style = LoveTypographyTokens.HeroBody,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp),
            )
            LoveFeatureResultHomeButton(
                text = stringResource(R.string.love_test_back_home),
                onClick = onHome,
                backgroundColor = LoveProtocolContainer,
                contentColor = LoveProtocolPrimaryDark,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}

@Composable
private fun ProtocolResultHero(
    namesLine: String,
    percent: Int,
    high: Boolean,
    modifier: Modifier = Modifier,
) {
    val heroBrush = if (high) ProtocolHeroBrush else LoveProtocolMutedHeroBrush
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = LoveLayout.ResultHeroShape,
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(heroBrush)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = namesLine,
                    style = LoveTypographyTokens.CardTitleLight,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
                LoveHeroPercentRing(
                    percent = percent,
                    label = stringResource(R.string.protocol_percent_label),
                    high = high,
                    contentDescription = stringResource(R.string.protocol_percent_cd),
                    ringSize = LoveLayout.LoveTestResultRingSize,
                    modifier = Modifier.padding(top = 16.dp),
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
                        text = stringResource(
                            if (high) R.string.protocol_hero_complete
                            else R.string.protocol_hero_pause,
                        ),
                        style = LoveTypographyTokens.CardTitleLight,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProtocolLowWarningCard(modifier: Modifier = Modifier) {
    val threshold = LoveScoreCalculator.DEFAULT_HIGH_THRESHOLD
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LoveProtocolContainer),
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
                        color = LoveProtocolPrimary,
                    )
                }
                Text(
                    text = stringResource(R.string.protocol_low_warning_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LoveProtocolPrimaryDark,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
            Text(
                text = stringResource(R.string.protocol_low_warning_body1),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveProtocolPrimaryDark.copy(alpha = 0.88f),
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.protocol_low_warning_body2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveProtocolPrimaryDark.copy(alpha = 0.88f),
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = stringResource(R.string.protocol_low_warning_body3),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveProtocolPrimaryDark.copy(alpha = 0.88f),
                modifier = Modifier.padding(top = 4.dp),
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.75f))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.love_test_result_low_chip, threshold),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveProtocolPrimaryDark,
                )
            }
        }
    }
}

@Composable
private fun ProtocolLowTipCard(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            Text(
                text = stringResource(R.string.protocol_low_tip_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
            )
            Text(
                text = stringResource(R.string.protocol_low_tip_body),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun ProtocolSummaryCard(
    signals: ProtocolSignals,
    high: Boolean,
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
                text = stringResource(R.string.protocol_summary_title),
                style = LoveTypographyTokens.ScreenHeadline,
                color = LoveOnSurface,
            )
            ProtocolSummaryRow(
                text = protocolHarmonySummary(signals.harmonyIndex, high),
                tone = summaryTone(step = 0, high = high),
                modifier = Modifier.padding(top = 16.dp),
            )
            ProtocolSummaryRow(
                text = protocolResonanceSummary(signals.resonanceIndex, high),
                tone = summaryTone(step = 1, high = high),
                modifier = Modifier.padding(top = 12.dp),
            )
            ProtocolSummaryRow(
                text = protocolVerdictSummary(signals.verdictBand, high),
                tone = summaryTone(step = 2, high = high),
                modifier = Modifier.padding(top = 12.dp),
            )
            if (high) {
                Text(
                    text = stringResource(R.string.protocol_summary_footer),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LoveProtocolContainer)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.hub_protocol_test_label),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveProtocolPrimary,
                )
            }
        }
    }
}

@Composable
private fun ProtocolSummaryRow(
    text: String,
    tone: ProtocolSummaryTone,
    modifier: Modifier = Modifier,
) {
    val (bg, fg, icon) = when (tone) {
        ProtocolSummaryTone.Success -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            ProtocolSummaryIcon.Check,
        )
        ProtocolSummaryTone.Warning -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            ProtocolSummaryIcon.Warning,
        )
        ProtocolSummaryTone.Neutral -> Triple(
            Color(0xFFECEFF1),
            Color(0xFF78909C),
            ProtocolSummaryIcon.Neutral,
        )
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            when (icon) {
                ProtocolSummaryIcon.Check -> Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(24.dp),
                )
                ProtocolSummaryIcon.Warning -> Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(22.dp),
                )
                ProtocolSummaryIcon.Neutral -> Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(fg),
                )
            }
        }
        Text(
            text = text,
            style = LoveTypographyTokens.HeroBody,
            fontWeight = FontWeight.SemiBold,
            color = if (tone == ProtocolSummaryTone.Neutral) fg else LoveOnSurface,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

private enum class ProtocolSummaryIcon {
    Check,
    Warning,
    Neutral,
}

@Composable
private fun ProtocolVerdictCard(
    signals: ProtocolSignals,
    modifier: Modifier = Modifier,
) {
    val band = signals.verdictBand.coerceIn(0, 2)
    val (containerColor, titleColor, bodyColor) = when (band) {
        2 -> Triple(
            LoveProtocolContainer,
            LoveProtocolPrimaryDark,
            LoveProtocolPrimary,
        )
        1 -> Triple(
            Color(0xFFB2DFDB),
            LoveProtocolPrimaryDark,
            LoveProtocolPrimaryDark,
        )
        else -> Triple(
            Color(0xFFECEFF1),
            Color(0xFF546E7A),
            Color(0xFF78909C),
        )
    }
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        spotTint = LoveProtocolPrimary,
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = protocolVerdictTitle(band),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = titleColor,
            )
            Text(
                text = protocolVerdictBody(band),
                style = MaterialTheme.typography.bodyLarge,
                color = bodyColor,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun ProtocolSharePreviewCard(
    percent: Int,
    names: String,
    modifier: Modifier = Modifier,
) {
    val previewRingSize = 80.dp
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
        border = BorderStroke(2.dp, LoveProtocolContainer),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(ProtocolHeroBrush),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$percent%",
                    style = LoveTypographyTokens.percentForRing(previewRingSize),
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
                    text = stringResource(R.string.protocol_share_preview_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.protocol_share_preview_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

private fun summaryTone(step: Int, high: Boolean): ProtocolSummaryTone = when {
    high -> ProtocolSummaryTone.Success
    step < 2 -> ProtocolSummaryTone.Warning
    else -> ProtocolSummaryTone.Neutral
}

@Composable
private fun protocolHarmonySummary(index: Int, high: Boolean): String = when {
    !high -> stringResource(R.string.protocol_summary_harmony_low)
    index >= 2 -> stringResource(R.string.protocol_summary_harmony_high)
    index >= 1 -> stringResource(R.string.protocol_summary_harmony_mid)
    else -> stringResource(R.string.protocol_summary_harmony_mid)
}

@Composable
private fun protocolResonanceSummary(index: Int, high: Boolean): String = when {
    !high -> stringResource(R.string.protocol_summary_resonance_low)
    index >= 2 -> stringResource(R.string.protocol_summary_resonance_high)
    index >= 1 -> stringResource(R.string.protocol_summary_resonance_mid)
    else -> stringResource(R.string.protocol_summary_resonance_mid)
}

@Composable
private fun protocolVerdictSummary(band: Int, high: Boolean): String = when {
    !high -> stringResource(R.string.protocol_summary_verdict_low)
    band >= 2 -> stringResource(R.string.protocol_summary_verdict_high)
    band >= 1 -> stringResource(R.string.protocol_summary_verdict_mid)
    else -> stringResource(R.string.protocol_summary_verdict_low)
}

@Composable
private fun protocolVerdictTitle(band: Int): String = when (band.coerceIn(0, 2)) {
    1 -> stringResource(R.string.protocol_verdict_title_1)
    2 -> stringResource(R.string.protocol_verdict_title_2)
    else -> stringResource(R.string.protocol_verdict_title_0)
}

@Composable
private fun protocolVerdictBody(band: Int): String = when (band.coerceIn(0, 2)) {
    1 -> stringResource(R.string.protocol_verdict_body_1)
    2 -> stringResource(R.string.protocol_verdict_body_2)
    else -> stringResource(R.string.protocol_verdict_body_0)
}
