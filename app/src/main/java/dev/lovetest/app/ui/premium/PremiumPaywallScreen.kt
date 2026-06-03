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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveSurface

private val PremiumHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF880E4F),
        Color(0xFFC2185B),
        Color(0xFFE91E63),
        Color(0xFFFFB74D),
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
) {
    val closeCd = stringResource(R.string.premium_close_cd)

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
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
                        .background(Color(0xFFF3EDF7)),
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = null,
                        tint = LoveOnSurfaceVariant,
                        modifier = Modifier.decorativeForAccessibility(),
                    )
                }
            }

            PremiumPaywallHero(modifier = Modifier.padding(top = 8.dp))

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
                        text = stringResource(R.string.premium_benefits_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = LoveOnSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    PremiumBenefitRow(
                        title = stringResource(R.string.premium_benefit_1_title),
                        body = stringResource(R.string.premium_benefit_1_body),
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    PremiumBenefitRow(
                        title = stringResource(R.string.premium_benefit_2_title),
                        body = stringResource(R.string.premium_benefit_2_body),
                        modifier = Modifier.padding(top = 16.dp),
                    )
                    PremiumBenefitRow(
                        title = stringResource(R.string.premium_benefit_3_title),
                        body = stringResource(R.string.premium_benefit_3_body),
                        modifier = Modifier.padding(top = 16.dp),
                    )
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
                        text = stringResource(R.string.premium_price),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = LoveOnSurface,
                    )
                    Text(
                        text = stringResource(R.string.premium_price_sub),
                        style = MaterialTheme.typography.bodyLarge,
                        color = LoveOnSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }

            LovePrimaryButton(
                text = stringResource(R.string.premium_buy_cta),
                onClick = onPurchase,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .height(52.dp),
            )

            TextButton(
                onClick = onRestore,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.premium_restore),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = LovePrimary,
                )
            }

            Text(
                text = stringResource(R.string.premium_legal_1),
                style = MaterialTheme.typography.bodySmall,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.premium_legal_2),
                style = MaterialTheme.typography.bodySmall,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color(0xFFF3EDF7))
                    .clickable(onClick = onContinueFree),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.premium_continue_free),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF49454F),
                )
            }

            Text(
                text = stringResource(R.string.premium_billing_footer),
                style = MaterialTheme.typography.bodySmall,
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
private fun PremiumPaywallHero(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(48.dp),
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
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = stringResource(R.string.premium_hero_sub),
                style = MaterialTheme.typography.bodyLarge,
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
