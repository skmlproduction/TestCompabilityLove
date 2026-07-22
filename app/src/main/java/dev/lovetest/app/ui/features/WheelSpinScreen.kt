package dev.lovetest.app.ui.features

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveTypographyTokens
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveWheelBadgeContainer
import dev.lovetest.core.ui.theme.LoveWheelBadgeText
import dev.lovetest.core.ui.theme.LoveWheelHintCardText
import dev.lovetest.core.ui.theme.LoveWheelPointerGold
import dev.lovetest.core.ui.theme.LoveZodiacSlotUnselected
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun WheelSpinScreen(
    onBack: () -> Unit,
    onSpinComplete: (segmentIndex: Int, prize: String) -> Unit,
) {
    val segments = stringArrayResource(R.array.wheel_segments).toList()
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    val spinningCd = stringResource(R.string.wheel_spinning_cd)
    val segmentsCd = stringResource(R.string.wheel_segments_cd, segments.joinToString())

    LaunchedEffect(Unit) {
        if (DebugUiPreview.matches("wheel_spin")) {
            rotation.snapTo(wheelRotationForSegment(segmentIndex = 3, extraSpins = 4))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = LoveWheelPointerGold.copy(0.5f),
                radius = 12.dp.toPx(),
                center = Offset(size.width * 0.15f, size.height * 0.38f),
            )
            drawCircle(
                color = LoveWheelPointerGold.copy(0.4f),
                radius = 10.dp.toPx(),
                center = Offset(size.width * 0.88f, size.height * 0.55f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .loveEdgeToEdgeScreenPadding(includeNavigationBar = false)
                .loveInputContentPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoveFeatureTopBar(
                title = stringResource(R.string.wheel_title),
                onBack = onBack,
                backEnabled = !isSpinning,
                backContentColor = LovePrimary,
            )
            Text(
                text = stringResource(R.string.wheel_hero_body),
                style = LoveTypographyTokens.HeroBody,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(LoveWheelBadgeContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.wheel_test_badge),
                    style = LoveTypographyTokens.HubHeroChip,
                    color = LoveWheelBadgeText,
                )
            }
                WheelPointer(modifier = Modifier.padding(top = 16.dp))
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .semantics {
                            if (isSpinning) contentDescription = spinningCd
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    WheelDisc(
                        segments = segments,
                        rotationDegrees = rotation.value,
                        discSize = 300.dp,
                        contentDescription = segmentsCd,
                    )
                }
                LoveShadowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    shape = RoundedCornerShape(32.dp),
                    shadowElevation = LoveCardShadowElevation.Card,
                    colors = CardDefaults.cardColors(containerColor = LoveSurface),
                ) {
                    Text(
                        text = stringResource(
                            if (isSpinning) R.string.wheel_spinning_hint else R.string.wheel_ready_hint,
                        ),
                        style = LoveTypographyTokens.HeroBody,
                        fontWeight = FontWeight.SemiBold,
                        color = LoveWheelHintCardText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 20.dp),
                    )
                }
                LovePrimaryButton(
                    text = stringResource(R.string.wheel_spin_cta),
                    onClick = {
                        if (isSpinning) return@LovePrimaryButton
                        val targetIndex = Random.nextInt(WHEEL_SEGMENT_COUNT)
                        val prize = segments[targetIndex]
                        isSpinning = true
                        scope.launch {
                            val target = wheelRotationForSegment(targetIndex) +
                                ((rotation.value / 360f).toInt() + 1) * 360f
                            rotation.animateTo(
                                targetValue = target,
                                animationSpec = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
                            )
                            isSpinning = false
                            onSpinComplete(targetIndex, prize)
                        }
                    },
                    enabled = !isSpinning,
                    modifier = Modifier.padding(top = 24.dp),
                )
            Text(
                text = stringResource(R.string.wheel_spin_note1),
                style = LoveTypographyTokens.CardCaption,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.wheel_spin_note2),
                style = LoveTypographyTokens.CardCaption,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            WheelNotBetFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 16.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
            )
        }
    }
}

@Composable
private fun WheelNotBetFooter(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(LoveZodiacSlotUnselected)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.wheel_spin_note3),
            style = LoveTypographyTokens.CardCaption,
            fontWeight = FontWeight.SemiBold,
            color = LoveOnPrimaryContainer,
            textAlign = TextAlign.Center,
        )
    }
}
