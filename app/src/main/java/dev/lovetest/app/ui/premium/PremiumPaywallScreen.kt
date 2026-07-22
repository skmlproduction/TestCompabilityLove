package dev.lovetest.app.ui.premium

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.R
import dev.lovetest.app.legal.LegalDocuments.openPrivacyPolicy
import dev.lovetest.app.legal.LegalDocuments.openTermsOfUse
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LoveTonalButton
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSecondary
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val PremiumHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF5C1228),
        LovePrimary,
        LoveSecondary,
        Color(0xFFE8C547),
    ),
)

private val PremiumGoldBrush = Brush.linearGradient(
    colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)),
)

@Composable
fun PremiumPaywallScreen(
    onClose: () -> Unit,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    onContinueFree: () -> Unit,
    displayPrice: String? = null,
) {
    val closeCd = stringResource(R.string.premium_close_cd)
    val context = LocalContext.current
    val benefits = premiumPaywallBenefits()
    val heroSub = stringResource(
        if (BuildConfig.ADS_ENABLED) R.string.premium_hero_sub_ads else R.string.premium_hero_sub,
    )
    val priceText = displayPrice?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.premium_price)

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .loveEdgeToEdgeScreenPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .semantics { contentDescription = closeCd }
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LovePrimaryContainer),
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = null,
                        tint = LoveOnSurfaceVariant,
                        modifier = Modifier.decorativeForAccessibility(),
                    )
                }
            }

            PremiumPaywallHero(
                heroSub = heroSub,
                modifier = Modifier.padding(top = 8.dp),
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
                        text = stringResource(
                            if (BuildConfig.ADS_ENABLED) {
                                R.string.premium_benefits_title
                            } else {
                                R.string.premium_benefits_title_support
                            },
                        ),
                        style = LoveTypographyTokens.ScreenHeadline,
                        color = LoveOnSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    benefits.forEachIndexed { index, benefit ->
                        PremiumBenefitRow(
                            title = benefit.title,
                            body = benefit.body,
                            modifier = Modifier.padding(top = if (index == 0) 20.dp else 16.dp),
                        )
                    }
                }
            }

            LoveShadowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                shape = RoundedCornerShape(32.dp),
                shadowElevation = LoveCardShadowElevation.Subtle,
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                border = BorderStroke(2.dp, Color(0xFFFFE082)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = priceText,
                        style = LoveTypographyTokens.ScreenHeadline,
                        fontWeight = FontWeight.ExtraBold,
                        color = LoveOnSurface,
                    )
                    Text(
                        text = stringResource(R.string.premium_price_sub),
                        style = LoveTypographyTokens.HeroBody,
                        color = LoveOnSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }

            LovePrimaryButton(
                text = stringResource(R.string.premium_buy_cta),
                onClick = onPurchase,
                modifier = Modifier.padding(top = 24.dp),
            )

            TextButton(
                onClick = onRestore,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.premium_restore),
                    style = LoveTypographyTokens.CardTitle,
                    fontWeight = FontWeight.SemiBold,
                    color = LovePrimary,
                )
            }

            Text(
                text = stringResource(R.string.premium_legal_1),
                style = LoveTypographyTokens.CardCaption,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(onClick = { context.openPrivacyPolicy() }) {
                    Text(
                        text = stringResource(R.string.premium_privacy_link),
                        style = LoveTypographyTokens.CardCaption,
                        color = LovePrimary,
                    )
                }
                Text(
                    text = "·",
                    style = LoveTypographyTokens.CardCaption,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp),
                )
                TextButton(onClick = { context.openTermsOfUse() }) {
                    Text(
                        text = stringResource(R.string.premium_terms_link),
                        style = LoveTypographyTokens.CardCaption,
                        color = LovePrimary,
                    )
                }
            }

            LoveTonalButton(
                text = stringResource(R.string.premium_continue_free),
                onClick = onContinueFree,
                containerColor = LovePrimaryContainer,
                contentColor = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp),
            )

            Text(
                text = stringResource(R.string.premium_billing_footer),
                style = LoveTypographyTokens.CardCaption,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp),
            )
        }
    }
}

@Composable
private fun premiumPaywallBenefits(): List<PremiumBenefit> {
    val allTestsTitle = stringResource(R.string.premium_benefit_1_title)
    val allTestsBody = stringResource(R.string.premium_benefit_1_body)
    val supportDevTitle = stringResource(R.string.premium_benefit_support_dev_title)
    val supportDevBody = stringResource(R.string.premium_benefit_support_dev_body)
    val supportTitle = stringResource(R.string.premium_benefit_2_title)
    val supportBody = stringResource(R.string.premium_benefit_2_body)
    val foreverTitle = stringResource(R.string.premium_benefit_3_title)
    val foreverBody = stringResource(R.string.premium_benefit_3_body)
    val gratitudeTitle = stringResource(R.string.premium_benefit_gratitude_title)
    val gratitudeBody = stringResource(R.string.premium_benefit_gratitude_body)
    return if (BuildConfig.ADS_ENABLED) {
        listOf(
            PremiumBenefit(
                stringResource(R.string.premium_benefit_ads_title),
                stringResource(R.string.premium_benefit_ads_body),
            ),
            PremiumBenefit(allTestsTitle, allTestsBody),
            PremiumBenefit(supportTitle, supportBody),
        )
    } else {
        listOf(
            PremiumBenefit(supportDevTitle, supportDevBody),
            PremiumBenefit(foreverTitle, foreverBody),
            PremiumBenefit(gratitudeTitle, gratitudeBody),
        )
    }
}

private data class PremiumBenefit(val title: String, val body: String)

@Composable
private fun PremiumPaywallHero(
    heroSub: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = LoveLayout.ResultHeroShape,
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PremiumHeroBrush)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(PremiumGoldBrush),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .decorativeForAccessibility()
                        .size(48.dp),
                )
            }
            Text(
                text = stringResource(R.string.settings_premium_title),
                style = LoveTypographyTokens.HeroTitleOnGradient,
                color = Color.White,
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = heroSub,
                style = LoveTypographyTokens.HeroBody,
                color = Color.White.copy(0.92f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp),
            )
        }
    }
}

@Composable
private fun PremiumBenefitRow(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Check, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(28.dp))
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = title,
                style = LoveTypographyTokens.CardTitle,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
            )
            Text(
                text = body,
                style = LoveTypographyTokens.HeroBody,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
