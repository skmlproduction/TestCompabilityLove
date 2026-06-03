package dev.lovetest.app.ui.features

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.share.LoveWheelShareOverlay
import dev.lovetest.app.ui.share.WheelShareCard
import dev.lovetest.app.ui.share.rememberLoveShareSheet
import dev.lovetest.app.util.buildWheelShareText
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

private val WheelResultHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFC2185B),
        Color(0xFFE91E63),
        Color(0xFFF8BBD0),
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelResultScreen(
    onShare: () -> Unit,
    onSpinAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val segments = stringArrayResource(R.array.wheel_segments).toList()
    val adviceLines = stringArrayResource(R.array.wheel_advice).toList()
    val segmentIndex = LoveTestSession.wheelSegmentIndex.coerceIn(0, WHEEL_SEGMENT_COUNT - 1)
    val prize = LoveTestSession.name1.ifBlank {
        segments.getOrElse(segmentIndex) { "…" }
    }
    val advice = remember(segmentIndex) {
        adviceLines.getOrElse(segmentIndex) { "" }
    }
    val stoppedRotation = wheelRotationForSegment(segmentIndex, extraSpins = 0)
    val shareSheet = rememberLoveShareSheet()
    val context = LocalContext.current
    val shareText = remember(context, prize) { context.buildWheelShareText(prize) }

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFFFD54F).copy(0.4f),
                radius = 10.dp.toPx(),
                center = Offset(size.width * 0.18f, size.height * 0.14f),
            )
            drawCircle(
                color = Color(0xFFFFD54F).copy(0.35f),
                radius = 8.dp.toPx(),
                center = Offset(size.width * 0.85f, size.height * 0.18f),
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.wheel_result_screen_title),
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
                WheelResultHeroCard(
                    prize = prize,
                    segments = segments,
                    stoppedRotation = stoppedRotation,
                    modifier = Modifier.padding(top = 8.dp),
                )
                WheelAdviceCard(
                    advice = advice,
                    modifier = Modifier.padding(top = 20.dp),
                )
                LovePrimaryButton(
                    text = stringResource(R.string.wheel_share_cta),
                    onClick = shareSheet.open,
                    modifier = Modifier.padding(top = 24.dp),
                )
                LoveOutlinedButton(
                    text = stringResource(R.string.wheel_spin_again),
                    onClick = onSpinAgain,
                    modifier = Modifier.padding(top = 12.dp),
                )
                WheelResultHomeButton(
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
                WheelShareCard(
                    prize = prize,
                    contentDescription = stringResource(R.string.share_preview_cd),
                    modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                )
            }
        }
        LoveWheelShareOverlay(
            sheet = shareSheet,
            prize = prize,
            shareText = shareText,
            onShare = onShare,
        )
    }
}

@Composable
private fun WheelResultHeroCard(
    prize: String,
    segments: List<String>,
    stoppedRotation: Float,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(48.dp),
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WheelResultHeroBrush)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.TopCenter) {
                WheelDisc(
                    segments = segments,
                    rotationDegrees = stoppedRotation,
                    discSize = 200.dp,
                    showHub = true,
                )
                WheelPointer(modifier = Modifier.align(Alignment.TopCenter))
            }
            Text(
                text = stringResource(R.string.wheel_result_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(0.95f),
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = prize,
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 52.sp),
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.22f))
                    .padding(vertical = 14.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.wheel_prize_tag),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun WheelAdviceCard(
    advice: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFCE4EC)),
                    contentAlignment = Alignment.Center,
                ) {
                    LoveHeartIcon(Modifier.size(28.dp), color = LovePrimary)
                }
                Text(
                    text = stringResource(R.string.wheel_advice_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
            Text(
                text = advice,
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurface,
                modifier = Modifier.padding(top = 16.dp),
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFFCE4EC))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.wheel_test_badge),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF880E4F),
                )
            }
        }
    }
}

@Composable
private fun WheelResultHomeButton(
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
