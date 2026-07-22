package dev.lovetest.app.ui.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.R
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.app.util.findActivity
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.monetization.AdMobInterstitialManager
import dev.lovetest.app.monetization.AdsConsentManager
import dev.lovetest.app.monetization.AdsInterstitialController
import dev.lovetest.app.monetization.InterstitialLoadState
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.ui.share.AdInterstitialPlaceholder
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveHeroGradientBrush
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.loveCardShadow
import dev.lovetest.core.ui.components.loveScreenHorizontalPadding
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveProtocolHeroGradientColors
import dev.lovetest.core.ui.theme.LoveProtocolPrimary
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HubScreen(
    onOpenLoveTest: () -> Unit,
    onOpenCalculator: () -> Unit,
    onOpenPair: () -> Unit,
    onOpenVictory: () -> Unit,
    onOpenLetters: () -> Unit,
    onOpenZodiac: () -> Unit,
    onOpenWheel: () -> Unit,
    onOpenProtocol: () -> Unit,
    onOpenPremium: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HubViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val preferences: AppPreferences = koinInject()
    val adMobManager: AdMobInterstitialManager = koinInject()
    val adsConsentManager: AdsConsentManager = koinInject()
    val context = LocalContext.current
    val isPremium by preferences.isPremiumFlow.collectAsStateWithLifecycle(initialValue = false)
    val adPending by AdsInterstitialController.pendingOnHub.collectAsStateWithLifecycle()
    var debugAdDismissed by remember { mutableStateOf(false) }
    val showDebugAdPlaceholder = BuildConfig.DEBUG &&
        DebugUiPreview.matches("ad_interstitial_placeholder") &&
        !debugAdDismissed

    LaunchedEffect(isPremium) {
        if (isPremium) {
            AdsInterstitialController.consume()
            adMobManager.discard()
        }
    }

    val adLoadState by adMobManager.loadState.collectAsStateWithLifecycle()

    LaunchedEffect(adPending, isPremium, adLoadState) {
        if (!adPending || isPremium || !BuildConfig.ADS_ENABLED || !adsConsentManager.canRequestAds()) {
            return@LaunchedEffect
        }
        val activity = context.findActivity() ?: return@LaunchedEffect
        when (adLoadState) {
            InterstitialLoadState.Ready -> {
                if (adMobManager.show(activity) { AdsInterstitialController.consume() }) {
                    AdsInterstitialController.consume()
                }
            }
            InterstitialLoadState.Failed -> {
                // No production placeholder — drop pending and continue hub.
                AdsInterstitialController.consume()
                adMobManager.preload()
            }
            InterstitialLoadState.Idle -> adMobManager.preload()
            InterstitialLoadState.Loading -> Unit
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            HubBottomNav(
                onTests = { /* already on hub */ },
                onPremium = onOpenPremium,
                onSettings = onOpenSettings,
                isPremium = isPremium,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .alpha(
                    when (state) {
                        HubDisplayState.Loading -> 0.55f
                        HubDisplayState.ErrorNetwork -> 0.4f
                        HubDisplayState.Main -> 1f
                    },
                ),
        ) {
            LoveGradientBackground(Modifier.fillMaxSize())
            LoveHubBackgroundBlobs(Modifier.fillMaxSize())

            if (BuildConfig.DEBUG && state == HubDisplayState.ErrorNetwork) {
                HubErrorTopBanner(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding(),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .loveScreenHorizontalPadding(),
            ) {
                HubTopBar()

                HubWelcomeHero(modifier = Modifier.padding(top = 8.dp))

                PremiumStrip(
                    onClick = onOpenPremium,
                    isPremium = isPremium,
                    modifier = Modifier.padding(top = 16.dp),
                )

                Text(
                    text = stringResource(R.string.hub_section_tests),
                    style = LoveTypographyTokens.HubSectionTitle,
                    modifier = Modifier.padding(top = 24.dp),
                )
                Text(
                    text = stringResource(R.string.hub_section_subtitle),
                    style = LoveTypographyTokens.HubSectionSubtitle,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                )

                FeaturedLoveTestCard(
                    title = stringResource(R.string.hub_test_love_title),
                    subtitle = stringResource(R.string.hub_test_love_subtitle),
                    onClick = onOpenLoveTest,
                )

                HubTestGridRow(
                    left = HubTestItem(
                        title = stringResource(R.string.hub_test_calculator_title),
                        subtitle = stringResource(R.string.hub_test_calculator_subtitle),
                        badge = { PercentBadge() },
                        onClick = onOpenCalculator,
                    ),
                    right = HubTestItem(
                        title = stringResource(R.string.hub_test_pair_title),
                        subtitle = stringResource(R.string.hub_test_pair_subtitle),
                        badge = { RingBadge() },
                        onClick = onOpenPair,
                    ),
                    modifier = Modifier.padding(top = LoveLayout.HubGridRowSpacing),
                )
                HubTestGridRow(
                    left = HubTestItem(
                        title = stringResource(R.string.hub_test_victory_title),
                        subtitle = stringResource(R.string.hub_test_victory_subtitle),
                        badge = { DiamondBadge() },
                        onClick = onOpenVictory,
                    ),
                    right = HubTestItem(
                        title = stringResource(R.string.hub_test_letters_title),
                        subtitle = stringResource(R.string.hub_test_letters_subtitle),
                        badge = { LettersBadge() },
                        onClick = onOpenLetters,
                    ),
                    modifier = Modifier.padding(top = LoveLayout.HubGridRowSpacing),
                )
                HubTestGridRow(
                    left = HubTestItem(
                        title = stringResource(R.string.hub_test_wheel_title),
                        subtitle = stringResource(R.string.hub_test_wheel_subtitle),
                        badge = { WheelBadge() },
                        onClick = onOpenWheel,
                    ),
                    right = HubTestItem(
                        title = stringResource(R.string.hub_test_zodiac_title),
                        subtitle = stringResource(R.string.hub_test_zodiac_subtitle),
                        badge = { RingBadge() },
                        onClick = onOpenZodiac,
                    ),
                    modifier = Modifier.padding(top = LoveLayout.HubGridRowSpacing),
                )

                HubFeaturedProtocolCard(
                    onClick = onOpenProtocol,
                    modifier = Modifier.padding(top = 12.dp),
                )

                ShareHintCard(
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                )
            }

            if (BuildConfig.DEBUG && state == HubDisplayState.Loading) {
                HubLoadingOverlay()
            }
            if (BuildConfig.DEBUG && state == HubDisplayState.ErrorNetwork) {
                HubErrorNetworkOverlay(
                    onRetry = viewModel::retryFromError,
                    onContinueOffline = viewModel::retryFromError,
                )
            }
            if (showDebugAdPlaceholder) {
                AdInterstitialPlaceholder(
                    onClose = {
                        debugAdDismissed = true
                        AdsInterstitialController.consume()
                        adMobManager.preload()
                    },
                    onPremium = {
                        debugAdDismissed = true
                        AdsInterstitialController.consume()
                        adMobManager.discard()
                        onOpenPremium()
                    },
                )
            }
        }
    }
}

@Composable
private fun HubTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = LoveTypographyTokens.AppTitle,
        )
    }
}

@Composable
private fun HubWelcomeHero(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = LoveLayout.HubHeroHeight)
            .loveCardShadow(LoveLayout.HubHeroShape, elevation = LoveCardShadowElevation.Hero)
            .clip(LoveLayout.HubHeroShape)
            .background(LoveHeroGradientBrush())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = stringResource(R.string.hub_hero_title),
                style = LoveTypographyTokens.HeroTitle,
                color = Color.White,
            )
            Text(
                text = stringResource(R.string.hub_hero_body_line1),
                style = LoveTypographyTokens.HeroBody,
                color = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = stringResource(R.string.hub_hero_body_line2),
                style = LoveTypographyTokens.HeroBody,
                color = Color.White.copy(alpha = 0.92f),
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(LoveLayout.HubHeroChipHeight)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.22f))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.hub_hero_chip),
                    style = LoveTypographyTokens.HubHeroChip,
                    color = Color.White,
                )
            }
        }
        LoveHeartIcon(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopEnd)
                .padding(top = 4.dp),
            color = Color.White.copy(alpha = 0.9f),
        )
    }
}

private fun HubProtocolGradientBrush(): Brush =
    Brush.linearGradient(
        colors = LoveProtocolHeroGradientColors,
        start = Offset(0f, 0f),
        end = Offset(900f, 400f),
    )

@Composable
private fun HubFeaturedProtocolCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = LoveLayout.HubFeaturedShape
    val title = stringResource(R.string.hub_protocol_title)
    val subtitle = stringResource(R.string.hub_protocol_subtitle)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = LoveLayout.HubProtocolFeaturedCardHeight)
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Card, spotTint = LoveProtocolPrimary)
            .clip(shape)
            .background(HubProtocolGradientBrush())
            .border(2.dp, Color.White.copy(alpha = 0.28f), shape)
            .semantics(mergeDescendants = true) {
                contentDescription = "$title. $subtitle"
            }
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text(
                text = stringResource(R.string.hub_protocol_badge_novo),
                style = LoveTypographyTokens.HubGoLabel.copy(fontSize = 10.sp),
                color = LoveProtocolPrimary,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 80.dp, top = 22.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HubProtocolStepsIcon(
                modifier = Modifier.size(LoveLayout.HubGridIconSize),
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = stringResource(R.string.hub_protocol_title),
                    style = LoveTypographyTokens.CardTitleLight,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.hub_protocol_subtitle),
                    style = LoveTypographyTokens.CardCaptionLight,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.22f))
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = stringResource(R.string.hub_protocol_test_label),
                        style = LoveTypographyTokens.HubHeroChip,
                        color = Color.White,
                    )
                }
            }
        }
        HubGoPill(
            label = stringResource(R.string.hub_featured_go),
            accentColor = LoveProtocolPrimary,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
        )
    }
}

@Composable
private fun HubProtocolStepsIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(LoveLayout.HubGridIconCorner))
            .background(Color.White.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) { index ->
                Box(
                    Modifier
                        .size(width = 12.dp, height = 3.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color.White.copy(alpha = 0.75f - index * 0.1f)),
                )
            }
        }
    }
}

@Composable
private fun HubGoPill(
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(LoveLayout.HubGoPillHeight)
            .defaultMinSize(minWidth = LoveLayout.HubGoPillMinWidth)
            .clip(RoundedCornerShape(LoveLayout.HubGoPillCorner))
            .background(Color.White)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = LoveTypographyTokens.HubGoLabel,
            color = accentColor,
        )
    }
}

@Composable
private fun PremiumStrip(
    onClick: () -> Unit,
    isPremium: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(32.dp)
    val title = stringResource(
        if (BuildConfig.ADS_ENABLED) {
            R.string.hub_premium_title
        } else {
            R.string.hub_premium_title_support
        },
    )
    val subtitle = stringResource(
        when {
            isPremium && !BuildConfig.ADS_ENABLED -> R.string.hub_premium_subtitle_active_support
            isPremium -> R.string.hub_premium_subtitle_active
            !BuildConfig.ADS_ENABLED -> R.string.hub_premium_subtitle_support
            else -> R.string.hub_premium_subtitle
        },
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Subtle)
            .semantics(mergeDescendants = true) {
                contentDescription = "$title. $subtitle"
            }
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = LovePrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LoveHeartIcon(modifier = Modifier.size(32.dp), color = LovePrimary)
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    text = title,
                    style = LoveTypographyTokens.HubPremiumTitle,
                    color = LoveOnPrimaryContainer,
                )
                Text(
                    text = subtitle,
                    style = LoveTypographyTokens.HubPremiumSubtitle,
                    color = LoveOnPrimaryContainer,
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = LovePrimary,
                modifier = Modifier.decorativeForAccessibility(),
            )
        }
    }
}

@Composable
private fun FeaturedLoveTestCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val shape = LoveLayout.HubFeaturedShape
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = LoveLayout.HubFeaturedCardHeight)
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Card)
            .clip(shape)
            .background(LoveHeroGradientBrush())
            .border(2.dp, Color.White.copy(alpha = 0.35f), shape)
            .semantics(mergeDescendants = true) {
                contentDescription = "$title. $subtitle"
            }
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp, end = 88.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LoveHeartIcon(modifier = Modifier.size(28.dp), color = Color.White)
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = title,
                    style = LoveTypographyTokens.CardTitleLight,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = LoveTypographyTokens.CardCaptionLight,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        HubGoPill(
            label = stringResource(R.string.hub_featured_go),
            accentColor = LovePrimary,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
        )
    }
}

private data class HubTestItem(
    val title: String,
    val subtitle: String,
    val badge: @Composable () -> Unit,
    val onClick: () -> Unit,
)

@Composable
private fun HubTestGridRow(
    left: HubTestItem,
    right: HubTestItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HubGridCard(item = left, modifier = Modifier.weight(1f))
        HubGridCard(item = right, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HubGridCard(item: HubTestItem, modifier: Modifier = Modifier) {
    val shape = LoveLayout.HubGridShape
    Card(
        modifier = modifier
            .heightIn(min = LoveLayout.HubGridCellHeight)
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Subtle)
            .semantics(mergeDescendants = true) {
                contentDescription = "${item.title}. ${item.subtitle}"
            }
            .clickable(onClick = item.onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(LoveLayout.HubGridIconSize)
                    .clip(RoundedCornerShape(LoveLayout.HubGridIconCorner))
                    .background(LovePrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                item.badge()
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
            ) {
                Text(
                    text = item.title,
                    style = LoveTypographyTokens.CardTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.subtitle,
                    style = LoveTypographyTokens.CardCaption,
                    color = LoveOnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun PercentBadge() {
    Text("%", style = LoveTypographyTokens.CardTitle, color = LovePrimary)
}

@Composable
private fun RingBadge() {
    Box(
        modifier = Modifier
            .size(14.dp)
            .border(2.dp, LovePrimary, CircleShape),
    )
}

@Composable
private fun DiamondBadge() {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(14.dp)) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(size.width, size.height / 2f)
            lineTo(size.width / 2f, size.height)
            lineTo(0f, size.height / 2f)
            close()
        }
        drawPath(path, LovePrimary)
    }
}

@Composable
private fun LettersBadge() {
    Text("Aa", style = LoveTypographyTokens.CardTitle, color = LovePrimary)
}

@Composable
private fun WheelBadge() {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(14.dp)) {
        val stroke = 2.dp.toPx()
        drawCircle(color = LovePrimary, radius = 5.dp.toPx(), style = Stroke(stroke))
        drawLine(LovePrimary, center, Offset(center.x, center.y - 6.dp.toPx()), stroke)
        drawLine(
            LovePrimary,
            Offset(center.x - 5.dp.toPx(), center.y),
            Offset(center.x + 5.dp.toPx(), center.y),
            stroke,
        )
    }
}

@Composable
private fun ShareHintCard(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(32.dp)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Subtle),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LovePrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    tint = LovePrimary,
                    modifier = Modifier.decorativeForAccessibility(),
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = stringResource(R.string.hub_share_card_title),
                    style = LoveTypographyTokens.CardTitle,
                )
                Text(
                    text = stringResource(R.string.hub_share_card_subtitle),
                    style = LoveTypographyTokens.CardCaption,
                    color = LoveOnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HubBottomNav(
    onTests: () -> Unit,
    onPremium: () -> Unit,
    onSettings: () -> Unit,
    isPremium: Boolean,
) {
    Surface(
        color = LoveSurface.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding(),
    ) {
        Column {
            HorizontalDivider(color = LoveOutlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                HubNavItem(
                    label = stringResource(R.string.hub_nav_tests),
                    selected = true,
                    onClick = onTests,
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = LovePrimary,
                        modifier = Modifier.decorativeForAccessibility(),
                    )
                }
                HubNavItem(
                    label = stringResource(R.string.hub_nav_premium),
                    selected = false,
                    onClick = onPremium,
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (isPremium) LovePrimary else LoveOnSurfaceVariant,
                        modifier = Modifier
                            .size(22.dp)
                            .decorativeForAccessibility(),
                    )
                }
                HubNavItem(
                    label = stringResource(R.string.hub_nav_settings),
                    selected = false,
                    onClick = onSettings,
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null,
                        tint = LoveOnSurfaceVariant,
                        modifier = Modifier.decorativeForAccessibility(),
                    )
                }
            }
        }
    }
}

@Composable
private fun HubNavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    val description = if (selected) {
        stringResource(R.string.hub_nav_item_selected, label)
    } else {
        label
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                contentDescription = description
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (selected) LovePrimaryContainer else LoveOutlineVariant),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
        Text(
            text = label,
            style = LoveTypographyTokens.SectionKicker,
            color = if (selected) LovePrimary else LoveOnSurfaceVariant,
            textDecoration = TextDecoration.None,
            modifier = Modifier
                .padding(top = 4.dp)
                .decorativeForAccessibility(),
        )
    }
}
