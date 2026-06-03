package dev.lovetest.app.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveHeroGradientBrush
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveErrorContainer
import dev.lovetest.core.ui.theme.LoveOnErrorContainer
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutline
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveProtocolContainer
import dev.lovetest.core.ui.theme.LoveProtocolHeroGradientColors
import dev.lovetest.core.ui.theme.LoveProtocolPrimary
import dev.lovetest.core.ui.theme.LoveSurface
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit,
) {
    val pageCount = 4
    val initialPage = DebugUiPreview.onboardingInitialPage().coerceIn(0, pageCount - 1)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { pageCount })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == pageCount - 1

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            OnboardingTopBar(onSkip = onSkip)
            OnboardingProgressBar(
                progress = (pagerState.currentPage + 1) / pageCount.toFloat(),
                currentPage = pagerState.currentPage + 1,
                pageCount = pageCount,
                modifier = Modifier.padding(top = 8.dp),
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 12.dp),
            ) { page ->
                when (page) {
                    0 -> OnboardingWelcomePage()
                    1 -> OnboardingTestsPage()
                    2 -> OnboardingProtocolPage()
                    else -> OnboardingDisclaimerPage()
                }
            }

            LovePrimaryButton(
                text = stringResource(
                    if (isLast) R.string.onboarding_start else R.string.onboarding_next,
                ),
                onClick = {
                    if (isLast) {
                        onComplete()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.padding(top = 8.dp),
            )
            OnboardingPageDots(
                currentPage = pagerState.currentPage,
                pageCount = pageCount,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp, bottom = 24.dp),
            )
        }
    }
}

@Composable
private fun OnboardingTopBar(onSkip: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = LoveOnSurface,
        )
        TextButton(onClick = onSkip) {
            Text(
                text = stringResource(R.string.onboarding_skip),
                color = LovePrimary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun OnboardingProgressBar(
    progress: Float,
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    val progressCd = stringResource(R.string.onboarding_progress_cd, currentPage, pageCount)
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .semantics { contentDescription = progressCd },
        color = LovePrimary,
        trackColor = LoveOutlineVariant,
        strokeCap = StrokeCap.Round,
    )
}

@Composable
private fun OnboardingPageDots(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .semantics(mergeDescendants = true) {}
            .decorativeForAccessibility(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) LovePrimary else LoveOutline,
                    ),
            )
        }
    }
}

@Composable
private fun OnboardingWelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        OnboardingHeroCard(
            pageLabel = stringResource(R.string.onboarding_page_1_of_4),
            kicker = stringResource(R.string.onboarding_welcome_kicker),
            line1 = stringResource(R.string.onboarding_welcome_hero_line1),
            line2 = stringResource(R.string.onboarding_welcome_hero_line2),
            body1 = stringResource(R.string.onboarding_welcome_hero_body1),
            body2 = stringResource(R.string.onboarding_welcome_hero_body2),
            showHeart = true,
            showExclamation = false,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_headline),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = LoveOnSurface,
            modifier = Modifier
                .padding(top = 16.dp)
                .semantics { heading() },
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_subline1),
            style = MaterialTheme.typography.bodyLarge,
            color = LoveOnSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_subline2),
            style = MaterialTheme.typography.bodyLarge,
            color = LoveOnSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OnboardingMiniCard(
                title = stringResource(R.string.onboarding_card_love_title),
                caption1 = stringResource(R.string.onboarding_card_love_cap1),
                caption2 = stringResource(R.string.onboarding_card_love_cap2),
                accent = true,
                icon = { OnboardingHeartIcon(Modifier.size(36.dp)) },
                modifier = Modifier.weight(1f),
            )
            OnboardingMiniCard(
                title = stringResource(R.string.onboarding_card_zodiac_title),
                caption1 = stringResource(R.string.onboarding_card_zodiac_cap1),
                caption2 = stringResource(R.string.onboarding_card_zodiac_cap2),
                accent = false,
                icon = { OnboardingRingIcon(Modifier.size(36.dp)) },
                modifier = Modifier.weight(1f),
            )
            OnboardingMiniCard(
                title = stringResource(R.string.onboarding_card_wheel_title),
                caption1 = stringResource(R.string.onboarding_card_wheel_cap1),
                caption2 = stringResource(R.string.onboarding_card_wheel_cap2),
                accent = false,
                icon = { OnboardingWheelIcon(Modifier.size(36.dp)) },
                modifier = Modifier.weight(1f),
            )
        }
        OnboardingPinkBanner(
            kicker = stringResource(R.string.onboarding_no_reg_kicker),
            title = stringResource(R.string.onboarding_no_reg_title),
            body = stringResource(R.string.onboarding_no_reg_body),
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
    }
}

@Composable
private fun OnboardingTestsPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        OnboardingHeroCard(
            pageLabel = stringResource(R.string.onboarding_page_2_of_4),
            kicker = stringResource(R.string.onboarding_tests_kicker),
            line1 = stringResource(R.string.onboarding_tests_hero_line1),
            line2 = stringResource(R.string.onboarding_tests_hero_line2),
            body1 = stringResource(R.string.onboarding_tests_hero_body1),
            body2 = stringResource(R.string.onboarding_tests_hero_body2),
            showHeart = false,
            showExclamation = false,
            compact = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.onboarding_tests_headline),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = LoveOnSurface,
            modifier = Modifier
                .padding(top = 16.dp)
                .semantics { heading() },
        )
        Text(
            text = stringResource(R.string.onboarding_tests_subline1),
            style = MaterialTheme.typography.bodyLarge,
            color = LoveOnSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.onboarding_tests_subline2),
            style = MaterialTheme.typography.bodyLarge,
            color = LoveOnSurfaceVariant,
        )
        OnboardingTestsGrid(modifier = Modifier.padding(top = 10.dp))
        LoveShadowCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = LoveCardShadowElevation.Subtle,
            colors = CardDefaults.cardColors(containerColor = LovePrimaryContainer),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    OnboardingRingIcon(Modifier.size(32.dp))
                }
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = stringResource(R.string.hub_test_zodiac_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LoveOnPrimaryContainer,
                    )
                    Text(
                        text = stringResource(R.string.onboarding_zodiac_banner_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LoveOnPrimaryContainer,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingProtocolPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        OnboardingHeroCard(
            pageLabel = stringResource(R.string.onboarding_page_3_of_4),
            kicker = stringResource(R.string.onboarding_protocol_kicker),
            line1 = stringResource(R.string.onboarding_protocol_hero_line1),
            line2 = stringResource(R.string.onboarding_protocol_hero_line2),
            body1 = stringResource(R.string.onboarding_protocol_hero_body1),
            body2 = stringResource(R.string.onboarding_protocol_hero_body2),
            heroBrush = OnboardingProtocolGradientBrush(),
            topBadge = stringResource(R.string.onboarding_protocol_badge_novo),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.onboarding_protocol_headline),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = LoveOnSurface,
            modifier = Modifier
                .padding(top = 12.dp)
                .semantics { heading() },
        )
        Text(
            text = stringResource(R.string.onboarding_protocol_subline1),
            style = MaterialTheme.typography.bodyLarge,
            color = LoveOnSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = stringResource(R.string.onboarding_protocol_subline2),
            style = MaterialTheme.typography.bodyLarge,
            color = LoveOnSurfaceVariant,
        )
        OnboardingProtocolStepsRow(modifier = Modifier.padding(top = 12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OnboardingMiniCard(
                title = stringResource(R.string.onboarding_protocol_hint_love_title),
                caption1 = stringResource(R.string.onboarding_protocol_hint_love_cap),
                caption2 = "",
                accent = true,
                icon = { OnboardingHeartIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
            OnboardingMiniCard(
                title = stringResource(R.string.onboarding_protocol_hint_calc_title),
                caption1 = stringResource(R.string.onboarding_protocol_hint_calc_cap),
                caption2 = "",
                accent = false,
                icon = { OnboardingPercentIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
        }
        LoveShadowCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            shape = RoundedCornerShape(38.dp),
            shadowElevation = LoveCardShadowElevation.Card,
            spotTint = LoveProtocolPrimary,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OnboardingProtocolGradientBrush())
                    .padding(20.dp),
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.onboarding_protocol_hub_kicker),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color.White.copy(0.88f),
                    )
                    Text(
                        text = stringResource(R.string.onboarding_protocol_hub_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Text(
                        text = stringResource(R.string.onboarding_protocol_hub_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(0.9f),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingProtocolStepsRow(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OnboardingProtocolStep(1, stringResource(R.string.onboarding_protocol_step_1))
            OnboardingProtocolStep(2, stringResource(R.string.onboarding_protocol_step_2))
            OnboardingProtocolStep(3, stringResource(R.string.onboarding_protocol_step_3))
        }
    }
}

@Composable
private fun OnboardingProtocolStep(number: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(LoveProtocolContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = LoveProtocolPrimary,
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = LoveOnSurface,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

private fun OnboardingProtocolGradientBrush(): Brush =
    Brush.linearGradient(
        colors = LoveProtocolHeroGradientColors,
        start = Offset(0f, 0f),
        end = Offset(900f, 700f),
    )

@Composable
private fun OnboardingDisclaimerPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        OnboardingHeroCard(
            pageLabel = stringResource(R.string.onboarding_page_4_of_4),
            kicker = stringResource(R.string.onboarding_disclaimer_kicker),
            line1 = stringResource(R.string.onboarding_disclaimer_hero_line1),
            line2 = stringResource(R.string.onboarding_disclaimer_hero_line2),
            body1 = "",
            body2 = "",
            showHeart = false,
            showExclamation = true,
            compact = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.onboarding_disclaimer_headline),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = LoveOnSurface,
            modifier = Modifier
                .padding(top = 12.dp)
                .semantics { heading() },
        )
        LoveShadowCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            shape = RoundedCornerShape(38.dp),
            shadowElevation = LoveCardShadowElevation.Card,
            colors = CardDefaults.cardColors(containerColor = LoveErrorContainer),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = LovePrimary)
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = stringResource(R.string.onboarding_warn_title1),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LoveOnErrorContainer,
                        )
                        Text(
                            text = stringResource(R.string.onboarding_warn_title2),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = LoveOnErrorContainer,
                        )
                    }
                }
                listOf(
                    R.string.onboarding_warn_body1,
                    R.string.onboarding_warn_body2,
                    R.string.onboarding_warn_body3,
                    R.string.onboarding_warn_body4,
                ).forEach { res ->
                    Text(
                        text = stringResource(res),
                        style = MaterialTheme.typography.bodyLarge,
                        color = LoveOnErrorContainer.copy(0.88f),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OnboardingDoDontCard(
                title = stringResource(R.string.onboarding_can_title),
                items = listOf(
                    stringResource(R.string.onboarding_can_1),
                    stringResource(R.string.onboarding_can_2),
                    stringResource(R.string.onboarding_can_3),
                ),
                positive = true,
                modifier = Modifier.weight(1f),
            )
            OnboardingDoDontCard(
                title = stringResource(R.string.onboarding_cannot_title),
                items = listOf(
                    stringResource(R.string.onboarding_cannot_1),
                    stringResource(R.string.onboarding_cannot_2),
                    stringResource(R.string.onboarding_cannot_3),
                ),
                positive = false,
                modifier = Modifier.weight(1f),
            )
        }
        OnboardingPinkBanner(
            kicker = stringResource(R.string.onboarding_agreement_kicker),
            title = stringResource(R.string.onboarding_agreement_title),
            body = stringResource(R.string.onboarding_agreement_body),
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
        )
    }
}

@Composable
private fun OnboardingHeroCard(
    pageLabel: String,
    kicker: String,
    line1: String,
    line2: String,
    body1: String,
    body2: String,
    showHeart: Boolean = false,
    showExclamation: Boolean = false,
    heroBrush: Brush = LoveHeroGradientBrush(),
    topBadge: String? = null,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    LoveShadowCard(
        modifier = modifier,
        shape = RoundedCornerShape(54.dp),
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 200.dp else 280.dp)
                .background(heroBrush)
                .padding(20.dp),
        ) {
            topBadge?.let { badge ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = LoveProtocolPrimary,
                    )
                }
            }
            if (showHeart) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                ) {
                    LoveHeartIcon(Modifier.size(100.dp), color = Color.White.copy(0.95f))
                }
            }
            if (showExclamation) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .size(72.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(0.22f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("!", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
            ) {
                if (kicker.isNotEmpty()) {
                    Text(
                        text = kicker,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color.White.copy(0.88f),
                    )
                }
                Text(
                    text = line1,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = line2,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                if (body1.isNotEmpty()) {
                    Text(
                        text = body1,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(0.92f),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        text = body2,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(0.92f),
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(0.2f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = pageLabel,
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
private fun OnboardingMiniCard(
    title: String,
    caption1: String,
    caption2: String,
    accent: Boolean,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.height(150.dp),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LovePrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                )
                Text(text = caption1, style = MaterialTheme.typography.bodySmall, color = LoveOnSurfaceVariant)
                Text(text = caption2, style = MaterialTheme.typography.bodySmall, color = LoveOnSurfaceVariant)
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .width(48.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (accent) LovePrimary else LoveOutline),
                )
            }
        }
    }
}

@Composable
private fun OnboardingTestsGrid(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OnboardingGridCard(
                title = stringResource(R.string.hub_test_love_title),
                caption = stringResource(R.string.onboarding_card_love_cap1),
                icon = { OnboardingHeartIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
            OnboardingGridCard(
                title = stringResource(R.string.hub_test_calculator_title),
                caption = stringResource(R.string.onboarding_ob_calc_cap),
                icon = { OnboardingPercentIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OnboardingGridCard(
                title = stringResource(R.string.hub_test_pair_title),
                caption = stringResource(R.string.onboarding_ob_pair_cap),
                icon = { OnboardingRingIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
            OnboardingGridCard(
                title = stringResource(R.string.hub_test_victory_title),
                caption = stringResource(R.string.onboarding_ob_victory_cap),
                icon = { OnboardingDiamondIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OnboardingGridCard(
                title = stringResource(R.string.hub_test_letters_title),
                caption = stringResource(R.string.onboarding_ob_letters_cap),
                icon = { OnboardingAaIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
            OnboardingGridCard(
                title = stringResource(R.string.hub_test_wheel_title),
                caption = stringResource(R.string.onboarding_ob_wheel_cap),
                icon = { OnboardingWheelIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OnboardingGridCard(
    title: String,
    caption: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.height(88.dp),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LovePrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                )
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OnboardingPinkBanner(
    kicker: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LovePrimaryContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = kicker,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = LoveOnPrimaryContainer,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LoveOnPrimaryContainer,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = LoveOnPrimaryContainer.copy(0.84f),
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun OnboardingDoDontCard(
    title: String,
    items: List<String>,
    positive: Boolean,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier,
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (positive) LovePrimary else LoveOutline),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (positive) "✓" else "✕",
                        color = if (positive) Color.White else LoveOnSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            items.forEach { item ->
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun OnboardingHeartIcon(modifier: Modifier = Modifier) {
    LoveHeartIcon(modifier = modifier, color = LovePrimary)
}

@Composable
private fun OnboardingRingIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawCircle(
            color = LovePrimary,
            radius = size.minDimension / 2f - 4.dp.toPx(),
            style = Stroke(width = 4.dp.toPx()),
        )
    }
}

@Composable
private fun OnboardingWheelIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension / 2f - 4.dp.toPx()
        drawCircle(color = LovePrimary, radius = r, style = Stroke(4.dp.toPx()))
        listOf(0f, 90f, 180f, 270f).forEach { angle ->
            val rad = Math.toRadians(angle.toDouble())
            drawLine(
                color = LovePrimary,
                start = Offset(cx, cy),
                end = Offset(
                    cx + (r * kotlin.math.cos(rad)).toFloat(),
                    cy + (r * kotlin.math.sin(rad)).toFloat(),
                ),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun OnboardingPercentIcon(modifier: Modifier = Modifier) {
    Text("%", modifier = modifier, fontWeight = FontWeight.Bold, color = LovePrimary, fontSize = 22.sp)
}

@Composable
private fun OnboardingDiamondIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val path = Path().apply {
            moveTo(size.width / 2f, 4.dp.toPx())
            lineTo(size.width - 4.dp.toPx(), size.height / 2f)
            lineTo(size.width / 2f, size.height - 4.dp.toPx())
            lineTo(4.dp.toPx(), size.height / 2f)
            close()
        }
        drawPath(path, LovePrimary, style = Fill)
    }
}

@Composable
private fun OnboardingAaIcon(modifier: Modifier = Modifier) {
    Text("Aa", modifier = modifier, fontWeight = FontWeight.ExtraBold, color = LovePrimary, fontSize = 20.sp)
}
