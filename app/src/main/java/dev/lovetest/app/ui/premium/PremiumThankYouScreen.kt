package dev.lovetest.app.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.R
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.LoveTonalButton
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSecondary
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val ThankYouHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1B5E4A),
        LovePrimary,
        LoveSecondary,
        Color(0xFFE8C547),
    ),
)

private val ThankYouGoldBrush = Brush.linearGradient(
    colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)),
)

@Composable
fun PremiumThankYouScreen(
    onHome: () -> Unit,
    onLoveTest: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .loveEdgeToEdgeScreenPadding(),
        ) {
            PremiumThankYouHero(modifier = Modifier.padding(top = 8.dp))

            Text(
                text = stringResource(R.string.premium_thank_you_headline),
                style = LoveTypographyTokens.ScreenHeadline,
                color = LoveOnSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            )
            Text(
                text = stringResource(R.string.premium_thank_you_body),
                style = LoveTypographyTokens.HeroBody,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )

            LoveShadowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                shape = RoundedCornerShape(38.dp),
                shadowElevation = LoveCardShadowElevation.Card,
                colors = CardDefaults.cardColors(containerColor = LoveSurface),
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    Text(
                        text = stringResource(R.string.premium_whats_next_title),
                        style = LoveTypographyTokens.ScreenHeadline,
                        color = LoveOnSurface,
                    )
                    Text(
                        text = stringResource(
                            if (BuildConfig.ADS_ENABLED) {
                                R.string.premium_whats_next_body1
                            } else {
                                R.string.premium_whats_next_body1_support
                            },
                        ),
                        style = LoveTypographyTokens.HeroBody,
                        color = LoveOnSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                    Text(
                        text = stringResource(
                            if (BuildConfig.ADS_ENABLED) {
                                R.string.premium_whats_next_body2
                            } else {
                                R.string.premium_whats_next_body2_support
                            },
                        ),
                        style = LoveTypographyTokens.HeroBody,
                        color = LoveOnSurfaceVariant,
                    )
                    Text(
                        text = stringResource(
                            if (BuildConfig.ADS_ENABLED) {
                                R.string.premium_whats_next_body3
                            } else {
                                R.string.premium_whats_next_body3_support
                            },
                        ),
                        style = LoveTypographyTokens.HeroBody,
                        color = LoveOnSurfaceVariant,
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.premium_active_badge),
                            style = LoveTypographyTokens.HubHeroChip,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32),
                        )
                    }
                }
            }

            LovePrimaryButton(
                text = stringResource(R.string.premium_thank_you_home),
                onClick = onHome,
                modifier = Modifier.padding(top = 24.dp),
            )

            LoveTonalButton(
                text = stringResource(R.string.premium_try_love_test),
                onClick = onLoveTest,
                containerColor = LovePrimaryContainer,
                contentColor = LoveOnPrimaryContainer,
                modifier = Modifier.padding(top = 12.dp),
            )

            Text(
                text = stringResource(R.string.premium_support_footer),
                style = LoveTypographyTokens.HeroBody,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            )
            Text(
                text = stringResource(R.string.premium_account_footer),
                style = LoveTypographyTokens.CardCaption,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 32.dp),
            )
        }
    }
}

@Composable
private fun PremiumThankYouHero(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = LoveLayout.ResultHeroShape,
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ThankYouHeroBrush)
                .padding(vertical = 32.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 32.dp, top = 16.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ThankYouGoldBrush),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier
                                .decorativeForAccessibility()
                                .size(56.dp),
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.premium_thank_you_title),
                    style = LoveTypographyTokens.HeroTitleOnGradient,
                    color = Color.White,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Text(
                    text = stringResource(R.string.premium_thank_you_hero_sub),
                    style = LoveTypographyTokens.HeroBody,
                    color = Color.White.copy(0.95f),
                    modifier = Modifier.padding(top = 8.dp),
                )
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(0.22f))
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.premium_thank_you_badge),
                        style = LoveTypographyTokens.HubHeroChip,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
