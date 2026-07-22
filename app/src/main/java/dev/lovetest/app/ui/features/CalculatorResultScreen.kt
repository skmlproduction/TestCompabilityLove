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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.navigationBarsPadding
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.common.FeatureLowTipCard
import dev.lovetest.app.ui.common.FeatureLowWarningCard
import dev.lovetest.app.ui.common.LoveFeatureResultActions
import dev.lovetest.app.ui.common.LoveHeroPercentRing
import dev.lovetest.app.ui.share.LoveShareResultOverlay
import dev.lovetest.app.ui.share.rememberLoveShareSheet
import dev.lovetest.app.util.buildLoveShareText
import dev.lovetest.core.domain.LoveScoreCalculator
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveHeroEnd
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveResultMutedHeroBrush
import dev.lovetest.core.ui.theme.LoveSecondary
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val CalculatorResultHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF5C1228),
        LovePrimary,
        LoveSecondary,
        LoveHeroEnd,
    ),
)

@Composable
fun CalculatorResultScreen(
    onShare: () -> Unit,
    onTryAnother: () -> Unit,
    onHome: () -> Unit,
) {
    val percent = LoveTestSession.percent
    val high = LoveScoreCalculator.isHighScore(percent)
    val name1 = LoveTestSession.name1.ifBlank { "…" }
    val name2 = LoveTestSession.name2.ifBlank { "…" }
    val namesLine = "$name1 + $name2"
    val percentCd = stringResource(R.string.calculator_percent_cd)
    val letterChips = remember(name1, name2) { calculatorLetterChips(name1, name2) }
    val commonLetters = remember(name1, name2) { calculatorCommonLetters(name1, name2) }
    val commonLettersLabel = commonLetters.joinToString(", ") { it.toString() }.ifBlank { "—" }
    val harmonyTag = stringResource(R.string.calculator_harmony_tag)
    val shareSheet = rememberLoveShareSheet()
    val context = LocalContext.current
    val shareText = remember(context, percent, name1, name2) {
        context.buildLoveShareText(percent, name1, name2)
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
                title = stringResource(R.string.calculator_result_title),
            )
            CalculatorResultHeroCard(
                    namesLine = namesLine,
                    percent = percent,
                    percentCd = percentCd,
                    high = high,
                    modifier = Modifier.padding(top = 8.dp),
                )
                if (!high) {
                    FeatureLowWarningCard(modifier = Modifier.padding(top = 20.dp))
                    FeatureLowTipCard(modifier = Modifier.padding(top = 16.dp))
                }
                // CTAs before breakdown so Share stays above the fold.
                LoveFeatureResultActions(
                    tryAgainLabel = stringResource(R.string.calculator_try_another),
                    onShare = shareSheet.open,
                    onTryAgain = onTryAnother,
                    onHome = onHome,
                )
                CalculatorBreakdownCard(
                    letterChips = letterChips,
                    percent = percent,
                    commonLettersLabel = commonLettersLabel,
                    modifier = Modifier.padding(top = 20.dp),
                )
                if (high) {
                    CalculatorSharePreviewCard(
                        percent = percent,
                        namesLine = namesLine,
                        modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
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
            harmonyTag = harmonyTag,
            shareText = shareText,
            high = high,
            onShareFallback = onShare,
        )
    }
}

@Composable
private fun CalculatorResultHeroCard(
    namesLine: String,
    percent: Int,
    percentCd: String,
    high: Boolean,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = LoveLayout.ResultHeroShape,
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (high) CalculatorResultHeroBrush else LoveResultMutedHeroBrush)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Text(
                text = "%",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = if (high) 0.2f else 0.12f),
                modifier = Modifier.align(Alignment.TopEnd),
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = namesLine,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                LoveHeroPercentRing(
                    percent = percent,
                    label = stringResource(R.string.calculator_result_percent_label),
                    high = high,
                    contentDescription = percentCd,
                    ringSize = LoveLayout.LoveTestResultRingSize,
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
                        text = stringResource(R.string.calculator_harmony_tag),
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
private fun CalculatorBreakdownCard(
    letterChips: List<Char>,
    percent: Int,
    commonLettersLabel: String,
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
                text = stringResource(R.string.calculator_breakdown_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
            )
            Text(
                text = stringResource(R.string.calculator_breakdown_body1),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = stringResource(R.string.calculator_breakdown_body2, commonLettersLabel),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.calculator_breakdown_body3),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                letterChips.forEachIndexed { index, letter ->
                    CalculatorLetterChip(
                        text = letter.toString(),
                        primaryStyle = index < 2,
                    )
                }
                CalculatorLetterChip(text = "+", isOperator = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(LovePrimaryContainer)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.calculator_percent_equals, percent.coerceIn(0, 100)),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = LoveOnPrimaryContainer,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LovePrimaryContainer)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.calculator_test_badge),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun CalculatorLetterChip(
    text: String,
    primaryStyle: Boolean = false,
    isOperator: Boolean = false,
) {
    val bg = when {
        isOperator -> LoveOutlineVariant
        primaryStyle -> LovePrimaryContainer
        else -> LoveHeroEnd.copy(alpha = 0.55f)
    }
    val fg = when {
        isOperator -> LoveOnSurfaceVariant
        primaryStyle -> LovePrimary
        else -> LoveOnPrimaryContainer
    }
    Box(
        modifier = Modifier
            .size(width = if (text.length > 1) 72.dp else 56.dp, height = 48.dp)
            .clip(RoundedCornerShape(if (text.length > 1) 14.dp else 14.dp))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = fg,
        )
    }
}

@Composable
private fun CalculatorSharePreviewCard(
    percent: Int,
    namesLine: String,
    modifier: Modifier = Modifier,
) {
    val previewRingSize = 80.dp
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
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CalculatorResultHeroBrush),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${percent.coerceIn(0, 100)}%",
                    style = LoveTypographyTokens.percentForRing(previewRingSize),
                    color = Color.White,
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = namesLine,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                )
                Text(
                    text = stringResource(R.string.calculator_share_preview_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.calculator_share_preview_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
