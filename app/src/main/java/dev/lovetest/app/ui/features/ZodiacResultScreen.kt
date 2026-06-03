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
import androidx.compose.ui.res.stringArrayResource
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

private val ZodiacResultHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1A237E),
        Color(0xFF512DA8),
        Color(0xFFC2185B),
        Color(0xFFF48FB1),
    ),
)

private val ZodiacAccent = Color(0xFF512DA8)
private val ZodiacSlot1Color = Color(0xFF512DA8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZodiacResultScreen(
    onShare: () -> Unit,
    onTryAnother: () -> Unit,
    onHome: () -> Unit,
) {
    val percent = LoveTestSession.percent
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

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.zodiac_result_title),
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
                ZodiacResultHeroCard(
                    initial1 = initial1,
                    initial2 = initial2,
                    signsLine = signsLine,
                    percent = percent,
                    percentCd = percentCd,
                    modifier = Modifier.padding(top = 8.dp),
                )
                ZodiacForecastCard(
                    sign1 = sign1,
                    sign2 = sign2,
                    element1 = element1,
                    element2 = element2,
                    modifier = Modifier.padding(top = 20.dp),
                )
                LovePrimaryButton(
                    text = stringResource(R.string.love_test_share_cta),
                    onClick = shareSheet.open,
                    modifier = Modifier.padding(top = 24.dp),
                )
                LoveOutlinedButton(
                    text = stringResource(R.string.zodiac_try_another),
                    onClick = onTryAnother,
                    modifier = Modifier.padding(top = 12.dp),
                )
                ZodiacResultHomeButton(
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
                ZodiacSharePreviewCard(
                    percent = percent,
                    signsLine = signsLine,
                    initial1 = initial1,
                    initial2 = initial2,
                    modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                )
            }
        }
        LoveShareResultOverlay(
            sheet = shareSheet,
            percent = percent,
            name1 = sign1,
            name2 = sign2,
            harmonyTag = harmonyTag,
            shareText = shareText,
            onShare = onShare,
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
                .background(ZodiacResultHeroBrush)
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
                    ZodiacSignBadge(initial1, ZodiacSlot1Color)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoveHeartIcon(Modifier.size(22.dp), color = LovePrimary)
                    }
                    ZodiacSignBadge(initial2, LovePrimary)
                }
                Text(
                    text = signsLine,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp),
                )
                LoveHeroPercentRing(
                    percent = percent,
                    label = stringResource(R.string.zodiac_result_percent_label),
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
                        text = stringResource(R.string.zodiac_harmony_tag),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
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
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
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
                    color = ZodiacAccent,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                ZodiacElementChip(element2)
            }
            Text(
                text = stringResource(R.string.zodiac_message_body1, sign1, sign2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurface,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.zodiac_message_body2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurface,
            )
            Text(
                text = stringResource(R.string.zodiac_message_body3),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurface,
            )
            Text(
                text = stringResource(R.string.zodiac_message_body4),
                style = MaterialTheme.typography.bodyLarge,
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
                        .background(ZodiacSlot1Color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(initial1, fontWeight = FontWeight.ExtraBold, color = ZodiacAccent)
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
                    text = "$percent% — $signsLine",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                )
                Text(
                    text = stringResource(R.string.zodiac_share_preview_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.zodiac_share_preview_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun ZodiacResultHomeButton(
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
