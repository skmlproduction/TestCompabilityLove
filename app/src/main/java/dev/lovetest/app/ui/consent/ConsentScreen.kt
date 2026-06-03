package dev.lovetest.app.ui.consent

import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import dev.lovetest.app.util.decorativeForAccessibility
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
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
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSurface

@Composable
fun ConsentScreen(
    onAccept: () -> Unit,
    onManage: () -> Unit,
    onOpenPrivacy: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            ConsentIllustrationCard(modifier = Modifier.padding(top = 24.dp))
            Text(
                text = stringResource(R.string.consent_headline),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .semantics { heading() },
            )
            Text(
                text = stringResource(R.string.consent_intro_line1),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.consent_intro_line2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
            )

            ConsentDetailsCard(modifier = Modifier.padding(top = 20.dp))
            ConsentPremiumHint(modifier = Modifier.padding(top = 16.dp))

            LovePrimaryButton(
                text = stringResource(R.string.consent_accept),
                onClick = onAccept,
                modifier = Modifier.padding(top = 24.dp),
            )
            LoveOutlinedButton(
                text = stringResource(R.string.consent_manage),
                onClick = onManage,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = stringResource(R.string.consent_privacy_footer),
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = TextDecoration.Underline,
                ),
                color = LovePrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { role = Role.Button }
                    .clickable(onClick = onOpenPrivacy)
                    .padding(top = 16.dp, bottom = 32.dp),
            )
        }
    }
}

@Composable
private fun ConsentIllustrationCard(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier
            .fillMaxWidth()
            .decorativeForAccessibility(),
        shape = RoundedCornerShape(48.dp),
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = LovePrimaryContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(0.9f))
                    .padding(12.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.7f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(LoveOutlineVariant),
                    )
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(LoveOutlineVariant),
                    )
                    Box(
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LovePrimary.copy(0.3f)),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.consent_illustration_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnPrimaryContainer,
                )
                Text(
                    text = stringResource(R.string.consent_illustration_body1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnPrimaryContainer,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.consent_illustration_body2),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnPrimaryContainer,
                )
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(LovePrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                LoveHeartIcon(Modifier.size(36.dp), color = LovePrimary.copy(0.5f))
            }
        }
    }
}

@Composable
private fun ConsentDetailsCard(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
            Text(
                text = stringResource(R.string.consent_section_what),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = LovePrimary,
            )
            listOf(
                R.string.consent_item_1,
                R.string.consent_item_2,
                R.string.consent_item_3,
                R.string.consent_item_4,
            ).forEach { res ->
                ConsentBulletItem(
                    text = stringResource(res),
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
                color = LoveOutlineVariant,
            )
            Text(
                text = stringResource(R.string.consent_choice_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LoveOnSurface,
            )
            Text(
                text = stringResource(R.string.consent_choice_body1),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.consent_choice_body2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun ConsentBulletItem(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(LovePrimary),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = LoveOnSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun ConsentPremiumHint(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(38.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LovePrimaryContainer),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LoveHeartIcon(Modifier.size(40.dp), color = LovePrimary)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = stringResource(R.string.consent_premium_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnPrimaryContainer,
                )
                Text(
                    text = stringResource(R.string.consent_premium_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnPrimaryContainer,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
