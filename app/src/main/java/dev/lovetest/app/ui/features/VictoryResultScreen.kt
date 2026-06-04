package dev.lovetest.app.ui.features

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.share.LoveShareResultOverlay
import dev.lovetest.app.ui.share.rememberLoveShareSheet
import dev.lovetest.app.util.buildLoveShareText
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.core.domain.LoveScoreCalculator
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LoveOutlinedButton
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveResultMutedHeroBrush
import dev.lovetest.core.ui.theme.LoveSurface

private val VictoryResultHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF2E7D32),
        Color(0xFFC2185B),
        Color(0xFFE91E63),
        Color(0xFFFFB74D),
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VictoryResultScreen(
    onShare: () -> Unit,
    onTryAnother: () -> Unit,
    onHome: () -> Unit,
) {
    val name = LoveTestSession.name1.ifBlank { "…" }
    val percent = LoveTestSession.percent
    val victory = percent >= LoveScoreCalculator.DEFAULT_HIGH_THRESHOLD
    val outcomeLabel = stringResource(
        if (victory) R.string.victory_outcome_yes else R.string.victory_outcome_maybe,
    )
    val harmonyTag = stringResource(
        if (victory) R.string.victory_hero_tag_yes else R.string.victory_hero_tag_no,
    )
    val shareSheet = rememberLoveShareSheet()
    val context = LocalContext.current
    val shareText = remember(context, percent, name) {
        context.buildLoveShareText(percent, name, name)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.victory_result_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { heading() },
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
                VictoryResultHeroCard(
                    name = name,
                    victory = victory,
                    modifier = Modifier.padding(top = 8.dp),
                )
                VictoryMessageCard(
                    name = name,
                    victory = victory,
                    outcomeLabel = outcomeLabel,
                    modifier = Modifier.padding(top = 20.dp),
                )
                LovePrimaryButton(
                    text = stringResource(R.string.love_test_share_cta),
                    onClick = shareSheet.open,
                    modifier = Modifier.padding(top = 24.dp),
                )
                LoveOutlinedButton(
                    text = stringResource(R.string.victory_try_another),
                    onClick = onTryAnother,
                    modifier = Modifier.padding(top = 12.dp),
                )
                VictoryResultHomeButton(
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
                VictorySharePreviewCard(
                    name = name,
                    victory = victory,
                    modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                )
            }
        }
        LoveShareResultOverlay(
            sheet = shareSheet,
            percent = percent,
            name1 = name,
            name2 = name,
            harmonyTag = harmonyTag,
            shareText = shareText,
            onShare = onShare,
        )
    }
}

@Composable
private fun VictoryResultHeroCard(
    name: String,
    victory: Boolean,
    modifier: Modifier = Modifier,
) {
    val verdictText = stringResource(
        if (victory) R.string.victory_verdict_yes else R.string.victory_verdict_no,
    )
    val verdictCd = stringResource(R.string.victory_verdict_cd, verdictText)
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(48.dp),
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (victory) VictoryResultHeroBrush else LoveResultMutedHeroBrush)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                VictoryTrophyDecor(modifier = Modifier.size(96.dp).decorativeForAccessibility())
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = verdictText,
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 72.sp),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .semantics { contentDescription = verdictCd },
                )
                Text(
                    text = stringResource(
                        if (victory) R.string.victory_verdict_sub_yes else R.string.victory_verdict_sub_no,
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(0.95f),
                )
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(0.22f))
                        .padding(vertical = 14.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(
                            if (victory) R.string.victory_hero_tag_yes else R.string.victory_hero_tag_no,
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun VictoryMessageCard(
    name: String,
    victory: Boolean,
    outcomeLabel: String,
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
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Check, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(32.dp))
                }
                Text(
                    text = stringResource(R.string.victory_message_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
            if (victory) {
                Text(
                    text = stringResource(R.string.victory_message_body1, name),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = stringResource(R.string.victory_message_body2),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.victory_message_body3),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.victory_message_body4),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                )
            } else {
                Text(
                    text = stringResource(R.string.victory_message_body_low1, name),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = stringResource(R.string.victory_message_body_low2),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.victory_message_body_low3),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.victory_message_body_low4),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                )
            }
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFFFF8E1))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.victory_test_badge),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.victory_outcome_label, outcomeLabel),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                    )
                }
            }
        }
    }
}

@Composable
private fun VictorySharePreviewCard(
    name: String,
    victory: Boolean,
    modifier: Modifier = Modifier,
) {
    val verdict = stringResource(
        if (victory) R.string.victory_verdict_yes else R.string.victory_verdict_no,
    )
    val subtitle = stringResource(
        if (victory) R.string.victory_verdict_sub_yes else R.string.victory_verdict_sub_no,
    )

    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
        border = BorderStroke(2.dp, Color(0xFFFFE082)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (victory) VictoryResultHeroBrush else LoveResultMutedHeroBrush),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = verdict.trimEnd('!'),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = stringResource(R.string.victory_share_preview_title, name, subtitle),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                )
                Text(
                    text = stringResource(R.string.victory_share_preview_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.victory_share_preview_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun VictoryResultHomeButton(
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
