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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.LoveEdgeToEdgeSkipAction
import dev.lovetest.core.ui.components.LoveEdgeToEdgeTopBar
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
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
import dev.lovetest.core.ui.theme.LoveTypographyTokens
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
    val isProtocolPage = pagerState.currentPage == 2
    val accentColor = if (isProtocolPage) LoveProtocolPrimary else LovePrimary

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .loveEdgeToEdgeScreenPadding(),
        ) {
            LoveEdgeToEdgeTopBar(
                title = stringResource(R.string.app_name),
                trailing = {
                    LoveEdgeToEdgeSkipAction(
                        label = stringResource(R.string.onboarding_skip),
                        onClick = onSkip,
                        contentColor = accentColor,
                    )
                },
            )
            OnboardingProgressBar(
                progress = (pagerState.currentPage + 1) / pageCount.toFloat(),
                currentPage = pagerState.currentPage + 1,
                pageCount = pageCount,
                accentColor = accentColor,
                modifier = Modifier.padding(top = 8.dp),
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .clipToBounds()
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
                containerColor = accentColor,
                modifier = Modifier.padding(top = 8.dp),
            )
            OnboardingPageDots(
                currentPage = pagerState.currentPage,
                pageCount = pageCount,
                activeColor = accentColor,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp, bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun OnboardingProgressBar(
    progress: Float,
    currentPage: Int,
    pageCount: Int,
    accentColor: Color = LovePrimary,
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
        color = accentColor,
        trackColor = LoveOutlineVariant,
        strokeCap = StrokeCap.Round,
    )
}

@Composable
private fun OnboardingPageDots(
    currentPage: Int,
    pageCount: Int,
    activeColor: Color = LovePrimary,
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
                        if (index == currentPage) activeColor else LoveOutline,
                    ),
            )
        }
    }
}

@Composable
private fun rememberOnboardingPageScrollState(): ScrollState {
    val state = rememberScrollState()
    // Preview / pager can leave content scrolled; always open page from top.
    LaunchedEffect(Unit) { state.scrollTo(0) }
    return state
}

@Composable
private fun OnboardingWelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberOnboardingPageScrollState()),
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
            heroHeight = LoveLayout.OnboardingHeroWelcomeHeight,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_headline),
            style = LoveTypographyTokens.ScreenHeadline,
            color = LoveOnSurface,
            modifier = Modifier
                .padding(top = 16.dp)
                .semantics { heading() },
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_subline1),
            style = LoveTypographyTokens.HeroBody,
            color = LoveOnSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_subline2),
            style = LoveTypographyTokens.HeroBody,
            color = LoveOnSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OnboardingMiniCard(
                title = stringResource(R.string.onboarding_card_love_title),
                captionLine1 = stringResource(R.string.onboarding_card_love_cap1),
                captionLine2 = stringResource(R.string.onboarding_card_love_cap2),
                accent = true,
                icon = { OnboardingHeartIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
            OnboardingMiniCard(
                title = stringResource(R.string.onboarding_card_zodiac_title),
                captionLine1 = stringResource(R.string.onboarding_card_zodiac_cap1),
                captionLine2 = stringResource(R.string.onboarding_card_zodiac_cap2),
                accent = false,
                icon = { OnboardingRingIcon(Modifier.size(32.dp)) },
                modifier = Modifier.weight(1f),
            )
            OnboardingMiniCard(
                title = stringResource(R.string.onboarding_card_wheel_title),
                captionLine1 = stringResource(R.string.onboarding_card_wheel_cap1),
                captionLine2 = stringResource(R.string.onboarding_card_wheel_cap2),
                accent = false,
                icon = { OnboardingWheelIcon(Modifier.size(32.dp)) },
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
            .fillMaxWidth()
            .verticalScroll(rememberOnboardingPageScrollState()),
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
            heroHeight = LoveLayout.OnboardingHeroTestsHeight,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.onboarding_tests_headline),
            style = LoveTypographyTokens.ScreenHeadline,
            color = LoveOnSurface,
            modifier = Modifier
                .padding(top = 16.dp)
                .semantics { heading() },
        )
        Text(
            text = stringResource(R.string.onboarding_tests_subline1),
            style = LoveTypographyTokens.HeroBody,
            color = LoveOnSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.onboarding_tests_subline2),
            style = LoveTypographyTokens.HeroBody,
            color = LoveOnSurfaceVariant,
        )
        OnboardingTestsGrid(modifier = Modifier.padding(top = 12.dp))
        LoveShadowCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = LoveCardShadowElevation.Subtle,
            colors = CardDefaults.cardColors(containerColor = LovePrimaryContainer),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    OnboardingRingIcon(Modifier.size(24.dp))
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
            .fillMaxWidth()
            .verticalScroll(rememberOnboardingPageScrollState()),
    ) {
        OnboardingHeroCard(
            pageLabel = stringResource(R.string.onboarding_page_3_of_4),
            kicker = stringResource(R.string.onboarding_protocol_kicker),
            line1 = stringResource(R.string.onboarding_protocol_hero_line1),
            line2 = stringResource(R.string.onboarding_protocol_hero_line2),
            body1 = stringResource(R.string.onboarding_protocol_hero_body1),
            body2 = stringResource(R.string.onboarding_protocol_hero_body2),
            heroBrush = OnboardingProtocolGradientBrush(),
            heroHeight = LoveLayout.OnboardingHeroProtocolHeight,
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OnboardingGridCard(
                title = stringResource(R.string.onboarding_protocol_hint_love_title),
                caption = stringResource(R.string.onboarding_protocol_hint_love_cap),
                icon = { OnboardingHeartIcon(Modifier.size(28.dp)) },
                modifier = Modifier.weight(1f),
            )
            OnboardingGridCard(
                title = stringResource(R.string.onboarding_protocol_hint_calc_title),
                caption = stringResource(R.string.onboarding_protocol_hint_calc_cap),
                icon = { OnboardingPercentIcon(Modifier.size(28.dp)) },
                modifier = Modifier.weight(1f),
            )
        }
        OnboardingProtocolHubPreviewCard(modifier = Modifier.padding(top = 12.dp))
        OnboardingProtocolAfterBanner(modifier = Modifier.padding(top = 12.dp, bottom = 40.dp))
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
                .padding(vertical = 16.dp, horizontal = 8.dp),
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LoveProtocolContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = LoveProtocolPrimary,
            )
        }
        Text(
            text = label,
            style = LoveTypographyTokens.CardCaption.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                lineHeight = 14.sp,
            ),
            color = LoveOnSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun OnboardingProtocolHubPreviewCard(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        spotTint = LoveProtocolPrimary,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OnboardingProtocolGradientBrush())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_protocol_hub_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = stringResource(R.string.onboarding_protocol_hub_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_protocol_go),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = LoveProtocolPrimary,
                )
            }
        }
    }
}

@Composable
private fun OnboardingProtocolAfterBanner(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveProtocolContainer),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = stringResource(R.string.onboarding_protocol_after_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = LoveProtocolPrimary.copy(alpha = 0.85f),
            )
            Text(
                text = stringResource(R.string.onboarding_protocol_after_body),
                style = MaterialTheme.typography.bodyMedium,
                color = LoveProtocolPrimary,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun OnboardingProtocolDisclaimerStrip(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveProtocolContainer),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White),
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = stringResource(R.string.onboarding_protocol_disclaimer_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = LoveProtocolPrimary.copy(alpha = 0.85f),
                )
                Text(
                    text = stringResource(R.string.onboarding_protocol_disclaimer_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveProtocolPrimary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
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
            .fillMaxWidth()
            .verticalScroll(rememberOnboardingPageScrollState()),
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
            heroHeight = LoveLayout.OnboardingHeroDisclaimerHeight,
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
        OnboardingProtocolDisclaimerStrip(modifier = Modifier.padding(top = 12.dp))
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
    heroHeight: Dp = LoveLayout.OnboardingHeroWelcomeHeight,
) {
    LoveShadowCard(
        modifier = modifier,
        shape = LoveLayout.HeroShape,
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heroHeight)
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
                        style = LoveTypographyTokens.SectionKicker,
                        color = Color.White.copy(0.88f),
                    )
                }
                Text(
                    text = line1,
                    style = LoveTypographyTokens.HeroTitleOnGradient,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = line2,
                    style = LoveTypographyTokens.HeroTitleOnGradient,
                    color = Color.White,
                )
                if (body1.isNotEmpty() || body2.isNotEmpty()) {
                    val body = listOf(body1, body2)
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString(" ")
                    Text(
                        text = body,
                        style = LoveTypographyTokens.HeroBody.copy(lineHeight = 20.sp),
                        color = Color.White.copy(0.92f),
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 3,
                        softWrap = true,
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
    captionLine1: String,
    captionLine2: String,
    accent: Boolean,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val miniCaption = LoveTypographyTokens.CardCaption.copy(
        fontSize = 10.sp,
        lineHeight = 13.sp,
    )
    LoveShadowCard(
        modifier = modifier.heightIn(min = LoveLayout.OnboardingMiniCardMinHeight),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LovePrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = LoveTypographyTokens.CardTitle,
                    color = LoveOnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = captionLine1,
                    style = miniCaption,
                    color = if (accent) LovePrimary else LoveOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = captionLine2,
                    style = miniCaption,
                    color = if (accent) LovePrimary else LoveOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .width(24.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (accent) LovePrimary else LoveOutline),
            )
        }
    }
}

@Composable
private fun OnboardingTestsGrid(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OnboardingGridCard(
                title = stringResource(R.string.hub_test_love_title),
                caption = stringResource(R.string.onboarding_ob_love_cap),
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
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
        modifier = modifier.heightIn(min = LoveLayout.OnboardingTestsGridCellHeight),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LovePrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
            ) {
                Text(
                    text = title,
                    style = LoveTypographyTokens.CardTitle,
                    color = LoveOnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = caption,
                    style = LoveTypographyTokens.CardCaption,
                    color = LoveOnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
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
