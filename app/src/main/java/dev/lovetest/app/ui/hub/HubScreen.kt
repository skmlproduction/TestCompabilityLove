package dev.lovetest.app.ui.hub

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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
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
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.ui.share.AdInterstitialPlaceholder
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveHeroGradientBrush
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.loveCardShadow
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveProtocolHeroGradientColors
import dev.lovetest.core.ui.theme.LoveProtocolPrimary
import dev.lovetest.core.ui.theme.LoveSurface
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
    val showAdOverlay = !isPremium && adPending ||
        (DebugUiPreview.matches("ad_interstitial_placeholder") && !debugAdDismissed)

    LaunchedEffect(isPremium) {
        if (isPremium) {
            AdsInterstitialController.consume()
            adMobManager.discard()
        }
    }

    LaunchedEffect(adPending, isPremium) {
        if (!adPending || isPremium || !BuildConfig.ADS_ENABLED || !adsConsentManager.canRequestAds()) {
            return@LaunchedEffect
        }
        val activity = context.findActivity() ?: return@LaunchedEffect
        if (adMobManager.show(activity) { AdsInterstitialController.consume() }) {
            AdsInterstitialController.consume()
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

            if (state == HubDisplayState.ErrorNetwork) {
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
                    .padding(horizontal = 24.dp),
            ) {
                HubTopBar(onOpenSettings = onOpenSettings)

                HubWelcomeHero(modifier = Modifier.padding(top = 8.dp))

                PremiumStrip(
                    onClick = onOpenPremium,
                    isPremium = isPremium,
                    modifier = Modifier.padding(top = 16.dp),
                )

                Text(
                    text = stringResource(R.string.hub_section_tests),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp),
                )
                Text(
                    text = stringResource(R.string.hub_section_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
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
                    modifier = Modifier.padding(top = 12.dp),
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
                    modifier = Modifier.padding(top = 12.dp),
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
                    modifier = Modifier.padding(top = 12.dp),
                )

                HubProtocolStrip(
                    onClick = onOpenProtocol,
                    modifier = Modifier.padding(top = 16.dp),
                )

                ShareHintCard(
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                )
            }

            if (state == HubDisplayState.Loading) {
                HubLoadingOverlay()
            }
            if (state == HubDisplayState.ErrorNetwork) {
                HubErrorNetworkOverlay(
                    onRetry = viewModel::retryFromError,
                    onContinueOffline = viewModel::retryFromError,
                )
            }
            if (showAdOverlay) {
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
private fun HubTopBar(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(LovePrimaryContainer),
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = stringResource(R.string.hub_settings_cd),
                tint = LovePrimary,
            )
        }
    }
}

@Composable
private fun HubWelcomeHero(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(46.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Hero)
            .clip(shape)
            .background(LoveHeroGradientBrush())
            .padding(24.dp),
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = stringResource(R.string.hub_hero_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = Color.White,
            )
            Text(
                text = stringResource(R.string.hub_hero_body_line1),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = stringResource(R.string.hub_hero_body_line2),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.92f),
            )
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.22f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.hub_hero_chip),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
        LoveHeartIcon(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopEnd),
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
private fun HubProtocolStrip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(38.dp)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Card, spotTint = LoveProtocolPrimary)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HubProtocolGradientBrush())
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "✓",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 14.dp)
                        .weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.hub_protocol_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = stringResource(R.string.hub_protocol_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.92f),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Text(
                        text = stringResource(R.string.hub_protocol_test_label),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.88f),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.hub_protocol_badge_novo),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = LoveProtocolPrimary,
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .decorativeForAccessibility(),
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumStrip(
    onClick: () -> Unit,
    isPremium: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(32.dp)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Subtle)
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
                    text = stringResource(R.string.hub_premium_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnPrimaryContainer,
                )
                Text(
                    text = stringResource(
                        if (isPremium) R.string.hub_premium_subtitle_active
                        else R.string.hub_premium_subtitle,
                    ),
                    style = MaterialTheme.typography.bodySmall,
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
    val shape = RoundedCornerShape(38.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Card)
            .clip(shape)
            .background(LoveHeroGradientBrush())
            .border(2.dp, Color.White.copy(alpha = 0.35f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LoveHeartIcon(modifier = Modifier.size(36.dp), color = Color.White)
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.hub_featured_go),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = LovePrimary,
            )
        }
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
    val shape = RoundedCornerShape(32.dp)
    Card(
        modifier = modifier
            .height(88.dp)
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Subtle)
            .clickable(onClick = item.onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LovePrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                item.badge()
            }
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun PercentBadge() {
    Text("%", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LovePrimary)
}

@Composable
private fun RingBadge() {
    Box(
        modifier = Modifier
            .size(28.dp)
            .border(3.dp, LovePrimary, CircleShape),
    )
}

@Composable
private fun DiamondBadge() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(LovePrimary, RoundedCornerShape(2.dp)),
    )
}

@Composable
private fun LettersBadge() {
    Text("Aa", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LovePrimary)
}

@Composable
private fun WheelBadge() {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(28.dp)) {
        val stroke = 3.dp.toPx()
        drawCircle(color = LovePrimary, radius = 10.dp.toPx(), style = Stroke(stroke))
        drawLine(LovePrimary, center, Offset(center.x, center.y - 12.dp.toPx()), stroke)
        drawLine(LovePrimary, Offset(center.x - 8.dp.toPx(), center.y), Offset(center.x + 8.dp.toPx(), center.y), stroke)
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
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.hub_share_card_subtitle),
                    style = MaterialTheme.typography.bodySmall,
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
    ) {
        Column {
            HorizontalDivider(color = LoveOutlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
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
                    Text(
                        "★",
                        fontSize = 20.sp,
                        color = if (isPremium) LovePrimary else LoveOnSurfaceVariant,
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (selected) LovePrimaryContainer else LoveOutlineVariant),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) LovePrimary else LoveOnSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
