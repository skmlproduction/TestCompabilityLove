package dev.lovetest.app.ui.features

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import dev.lovetest.app.util.buildProtocolShareText
import dev.lovetest.core.domain.LoveScoreCalculator
import dev.lovetest.core.domain.ProtocolSignals
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LoveOutlinedButton
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveProtocolContainer
import dev.lovetest.core.ui.theme.LoveProtocolHeroGradientColors
import dev.lovetest.core.ui.theme.LoveProtocolPrimary
import dev.lovetest.core.ui.theme.LoveProtocolPrimaryDark
import dev.lovetest.core.ui.theme.LoveResultMutedHeroBrush
import dev.lovetest.core.ui.theme.LoveSurface

private val ProtocolHeroBrush = Brush.linearGradient(colors = LoveProtocolHeroGradientColors)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolResultScreen(
    onShare: () -> Unit,
    onTryAnother: () -> Unit,
    onHome: () -> Unit,
) {
    val percent = LoveTestSession.percent
    val high = LoveScoreCalculator.isHighScore(percent)
    val name1 = LoveTestSession.name1.ifBlank { "…" }
    val name2 = LoveTestSession.name2.ifBlank { "…" }
    val signals = LoveTestSession.protocolSignals ?: return
    val namesLine = "$name1 + $name2"
    val percentCd = stringResource(R.string.protocol_percent_cd)
    val shareSheet = rememberLoveShareSheet()
    val context = LocalContext.current
    val shareText = remember(context, percent, name1, name2) {
        context.buildProtocolShareText(percent, name1, name2)
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
                            text = stringResource(R.string.protocol_result_title),
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
                ProtocolResultHero(
                    namesLine = namesLine,
                    percent = percent,
                    percentCd = percentCd,
                    high = high,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text = stringResource(R.string.protocol_signals_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                    modifier = Modifier.padding(top = 20.dp),
                )
                ProtocolSignalRow(
                    label = stringResource(R.string.protocol_calculating_step1),
                    body = protocolHarmonyText(signals.harmonyIndex),
                    modifier = Modifier.padding(top = 10.dp),
                )
                ProtocolSignalRow(
                    label = stringResource(R.string.protocol_calculating_step2),
                    body = protocolResonanceText(signals.resonanceIndex),
                    modifier = Modifier.padding(top = 8.dp),
                )
                ProtocolSignalRow(
                    label = stringResource(R.string.protocol_calculating_step3),
                    body = protocolSparkText(signals.sparkIndex),
                    modifier = Modifier.padding(top = 8.dp),
                )
                ProtocolVerdictCard(
                    signals = signals,
                    modifier = Modifier.padding(top = 16.dp),
                )
                LovePrimaryButton(
                    text = stringResource(R.string.love_test_share_cta),
                    onClick = shareSheet.open,
                    modifier = Modifier.padding(top = 24.dp),
                )
                LoveOutlinedButton(
                    text = stringResource(R.string.protocol_try_again),
                    onClick = onTryAnother,
                    modifier = Modifier.padding(top = 12.dp),
                )
                LoveOutlinedButton(
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
                        .padding(top = 16.dp, bottom = 32.dp),
                )
            }
        }
        LoveShareResultOverlay(
            sheet = shareSheet,
            percent = percent,
            name1 = name1,
            name2 = name2,
            harmonyTag = protocolVerdictTitle(signals.verdictBand),
            shareText = shareText,
            onShare = onShare,
        )
    }
}

@Composable
private fun ProtocolResultHero(
    namesLine: String,
    percent: Int,
    percentCd: String,
    high: Boolean,
    modifier: Modifier = Modifier,
) {
    val heroBrush = if (high) ProtocolHeroBrush else LoveResultMutedHeroBrush
    val percentColor = if (high) Color.White else LoveOnSurfaceVariant
    val namesColor = if (high) Color.White.copy(0.9f) else Color.White
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(48.dp),
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(heroBrush)
                .padding(24.dp)
                .semantics { contentDescription = percentCd },
        ) {
            Column {
                Text(
                    text = namesLine,
                    style = MaterialTheme.typography.titleMedium,
                    color = namesColor,
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 72.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = percentColor,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun ProtocolSignalRow(
    label: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LoveProtocolContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text("✓", fontWeight = FontWeight.Bold, color = LoveProtocolPrimary)
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveProtocolPrimary,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun ProtocolVerdictCard(
    signals: ProtocolSignals,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        spotTint = LoveProtocolPrimary,
        colors = CardDefaults.cardColors(containerColor = LoveProtocolContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = protocolVerdictTitle(signals.verdictBand),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LoveProtocolPrimaryDark,
            )
            Text(
                text = protocolVerdictBody(signals.verdictBand),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveProtocolPrimary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun protocolHarmonyText(index: Int): String = when (index.coerceIn(0, 2)) {
    1 -> stringResource(R.string.protocol_signal_harmony_1)
    2 -> stringResource(R.string.protocol_signal_harmony_2)
    else -> stringResource(R.string.protocol_signal_harmony_0)
}

@Composable
private fun protocolResonanceText(index: Int): String = when (index.coerceIn(0, 2)) {
    1 -> stringResource(R.string.protocol_signal_resonance_1)
    2 -> stringResource(R.string.protocol_signal_resonance_2)
    else -> stringResource(R.string.protocol_signal_resonance_0)
}

@Composable
private fun protocolSparkText(index: Int): String = when (index.coerceIn(0, 2)) {
    1 -> stringResource(R.string.protocol_signal_spark_1)
    2 -> stringResource(R.string.protocol_signal_spark_2)
    else -> stringResource(R.string.protocol_signal_spark_0)
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
