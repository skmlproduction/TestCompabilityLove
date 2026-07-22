package dev.lovetest.app.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.common.LoveHeroPercentRing
import dev.lovetest.app.ui.share.LoveShareResultOverlay
import dev.lovetest.app.ui.share.rememberLoveShareSheet
import dev.lovetest.app.util.buildLoveShareText
import dev.lovetest.core.domain.LoveScoreCalculator
import dev.lovetest.app.ui.common.FeatureLowTipCard
import dev.lovetest.app.ui.common.FeatureLowWarningCard
import dev.lovetest.app.ui.common.LoveFeatureResultActions
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveResultMutedHeroBrush
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveWheelBadgeContainer
import dev.lovetest.core.ui.theme.LoveWheelBadgeText
import dev.lovetest.core.ui.theme.LoveZodiacAccentPink
import dev.lovetest.core.ui.theme.LoveZodiacResultHeroBrush
import dev.lovetest.core.ui.theme.LoveZodiacShareBorder
import dev.lovetest.core.ui.theme.LoveTypographyTokens
import dev.lovetest.core.ui.theme.LoveZodiacViolet

@Composable
fun ZodiacResultScreen(
    onShare: () -> Unit,
    onTryAnother: () -> Unit,
    onHome: () -> Unit,
) {
    val percent = LoveTestSession.percent
    val high = LoveScoreCalculator.isHighScore(percent)
    val sign1 = LoveTestSession.name1.ifBlank { "…" }
    val sign2 = LoveTestSession.name2.ifBlank { "…" }
    val signsLine = "$sign1 + $sign2"
    val initial1 = sign1.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val initial2 = sign2.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val allSigns = stringArrayResource(R.array.zodiac_signs).toList()
    val element1 = remember(sign1) { zodiacElementForSignName(sign1, allSigns) }
    val element2 = remember(sign2) { zodiacElementForSignName(sign2, allSigns) }
    val percentCd = stringResource(R.string.zodiac_percent_cd)
    val harmonyTag = stringResource(R.string.zodiac_harmony_tag)
    val shareSheet = rememberLoveShareSheet()
    val context = LocalContext.current
    val shareText = remember(context, percent, sign1, sign2) {
        context.buildLoveShareText(percent, sign1, sign2)
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
                title = stringResource(R.string.zodiac_result_title),
            )
            ZodiacResultHeroCard(
                    initial1 = initial1,
                    initial2 = initial2,
                    signsLine = signsLine,
                    percent = percent,
                    percentCd = percentCd,
                    high = high,
                    modifier = Modifier.padding(top = 8.dp),
                )
                if (!high) {
                    FeatureLowWarningCard(modifier = Modifier.padding(top = 20.dp))
                    FeatureLowTipCard(modifier = Modifier.padding(top = 16.dp))
                }
                // CTAs before forecast so Share stays above the fold.
                LoveFeatureResultActions(
                    tryAgainLabel = stringResource(R.string.zodiac_try_another),
                    onShare = shareSheet.open,
                    onTryAgain = onTryAnother,
                    onHome = onHome,
                )
                ZodiacForecastCard(
                    sign1 = sign1,
                    sign2 = sign2,
                    element1 = element1,
                    element2 = element2,
                    modifier = Modifier.padding(top = 20.dp),
                )
                if (high) {
                    ZodiacSharePreviewCard(
                        percent = percent,
                        signsLine = signsLine,
                        initial1 = initial1,
                        initial2 = initial2,
                        modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                    )
                } else {
                    Box(modifier = Modifier.padding(bottom = 32.dp))
                }
        }
        LoveShareResultOverlay(
            sheet = shareSheet,
            percent = percent,
            name1 = sign1,
            name2 = sign2,
            harmonyTag = harmonyTag,
            shareText = shareText,
            high = high,
            onShareFallback = onShare,
        )
    }
}

@Composable
private fun ZodiacResultHeroCard(
    initial1: String,
    initial2: String,
    signsLine: String,
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
                .background(if (high) LoveZodiacResultHeroBrush else LoveResultMutedHeroBrush)
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
                    ZodiacSignBadge(initial1, LoveZodiacViolet)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoveHeartIcon(Modifier.size(22.dp), color = LoveZodiacViolet)
                    }
                    ZodiacSignBadge(initial2, LoveZodiacAccentPink)
                }
                Text(
                    text = signsLine,
                    style = LoveTypographyTokens.CardTitleLight,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                )
                LoveHeroPercentRing(
                    percent = percent,
                    label = stringResource(R.string.zodiac_result_percent_label),
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
                        text = stringResource(R.string.zodiac_harmony_tag),
                        style = LoveTypographyTokens.CardTitleLight,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(0.18f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.zodiac_test_badge),
                        style = LoveTypographyTokens.HubHeroChip,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun ZodiacSignBadge(letter: String, color: Color) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.85f)),
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
private fun ZodiacForecastCard(
    sign1: String,
    sign2: String,
    element1: ZodiacElement,
    element2: ZodiacElement,
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
                text = stringResource(R.string.zodiac_message_title),
                style = LoveTypographyTokens.ScreenHeadline,
                color = LoveOnSurface,
            )
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ZodiacElementChip(element1)
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveZodiacViolet,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                ZodiacElementChip(element2)
            }
            Text(
                text = stringResource(R.string.zodiac_message_body1, sign1, sign2),
                style = LoveTypographyTokens.HeroBody,
                color = LoveOnSurface,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.zodiac_message_body2),
                style = LoveTypographyTokens.HeroBody,
                color = LoveOnSurface,
            )
            Text(
                text = stringResource(R.string.zodiac_message_body3),
                style = LoveTypographyTokens.HeroBody,
                color = LoveOnSurface,
            )
            Text(
                text = stringResource(R.string.zodiac_message_body4),
                style = LoveTypographyTokens.HeroBody,
                color = LoveOnSurface,
            )
        }
    }
}

@Composable
private fun ZodiacElementChip(element: ZodiacElement) {
    val (bg, fg) = when (element) {
        ZodiacElement.Fire -> Color(0xFFFFCCBC) to Color(0xFFBF360C)
        ZodiacElement.Earth -> Color(0xFFDCEDC8) to Color(0xFF33691E)
        ZodiacElement.Air -> Color(0xFFE1BEE7) to Color(0xFF4A148C)
        ZodiacElement.Water -> Color(0xFFB3E5FC) to Color(0xFF01579B)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = zodiacElementLabel(element),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = fg,
        )
    }
}

@Composable
private fun zodiacElementLabel(element: ZodiacElement): String = when (element) {
    ZodiacElement.Fire -> stringResource(R.string.zodiac_element_fire)
    ZodiacElement.Earth -> stringResource(R.string.zodiac_element_earth)
    ZodiacElement.Air -> stringResource(R.string.zodiac_element_air)
    ZodiacElement.Water -> stringResource(R.string.zodiac_element_water)
}

@Composable
private fun ZodiacSharePreviewCard(
    percent: Int,
    signsLine: String,
    initial1: String,
    initial2: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
        border = BorderStroke(2.dp, LoveZodiacShareBorder),
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
                        .background(LoveZodiacViolet.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(initial1, fontWeight = FontWeight.ExtraBold, color = LoveZodiacViolet)
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LoveWheelBadgeContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(initial2, fontWeight = FontWeight.ExtraBold, color = LoveWheelBadgeText)
                }
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "$percent% — $signsLine",
                    style = LoveTypographyTokens.CardTitle,
                    color = LoveOnSurface,
                )
                Text(
                    text = stringResource(R.string.zodiac_share_preview_subtitle),
                    style = LoveTypographyTokens.HeroBody,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.zodiac_share_preview_hint),
                    style = LoveTypographyTokens.CardCaption,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
