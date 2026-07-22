package dev.lovetest.app.ui.features

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.app.ui.common.NameInputHint
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.app.util.loveInputFieldSemantics
import dev.lovetest.app.util.loveInputLabelForAccessibility
import dev.lovetest.app.prefs.rememberLastSingleName
import dev.lovetest.core.domain.NameInputValidator
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveFeatureHero
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSecondary
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val VictoryInputHeroBrush = Brush.linearGradient(
    colors = listOf(
        LovePrimary,
        LoveSecondary,
        Color(0xFFE8C547),
    ),
)

private val TrophyGoldBrush = Brush.linearGradient(
    colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)),
)

@Composable
fun VictoryInputScreen(
    onBack: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var name by rememberLastSingleName()
    val canSubmit = NameInputValidator.canSubmitSingle(name)

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .loveEdgeToEdgeScreenPadding(includeNavigationBar = false)
                .loveInputContentPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            LoveFeatureTopBar(
                title = stringResource(R.string.victory_title),
                onBack = onBack,
            )
            VictoryInputHero(modifier = Modifier.padding(top = 8.dp))
                VictoryOutcomePreview(modifier = Modifier.padding(top = 16.dp))
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
                            text = stringResource(R.string.victory_name_label),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = LovePrimary,
                            modifier = Modifier.loveInputLabelForAccessibility(),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .height(52.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(Color.White)
                                .border(2.dp, LovePrimary, RoundedCornerShape(28.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            BasicTextField(
                                value = name,
                                onValueChange = { name = it },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 18.sp, color = LoveOnSurface),
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                                cursorBrush = SolidColor(LovePrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 48.dp)
                                    .loveInputFieldSemantics(
                                        label = stringResource(R.string.victory_name_label),
                                        value = name,
                                    ),
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFF8E1)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Filled.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color(0xFFFF8F00),
                                    modifier = Modifier
                                        .decorativeForAccessibility()
                                        .size(22.dp),
                                )
                            }
                        }
                        NameInputHint(name)
                        Text(
                            text = stringResource(R.string.victory_popular_names),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoveOnSurfaceVariant,
                            modifier = Modifier.padding(top = 20.dp),
                        )
                        Row(
                            modifier = Modifier.padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            VictoryNameChip(stringResource(R.string.victory_chip_1)) { name = it }
                            VictoryNameChip(stringResource(R.string.victory_chip_2)) { name = it }
                            VictoryNameChip(stringResource(R.string.victory_chip_3)) { name = it }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFFFFF8E1))
                                .padding(16.dp),
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.victory_tip_line1),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFFE65100),
                                )
                                Text(
                                    text = stringResource(R.string.victory_tip_line2),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFF57F17),
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                            }
                        }
                    }
                }
                LovePrimaryButton(
                    text = stringResource(R.string.victory_cta),
                    onClick = { onSubmit(name.trim()) },
                    enabled = canSubmit,
                    modifier = Modifier.padding(top = 24.dp, bottom = 32.dp),
                )
        }
    }
}

@Composable
private fun VictoryInputHero(modifier: Modifier = Modifier) {
    LoveFeatureHero(
        modifier = modifier,
        brush = VictoryInputHeroBrush,
        minHeight = LoveLayout.FeatureHeroTallMinHeight,
        shape = LoveLayout.HubHeroShape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.victory_hero_title),
                    style = LoveTypographyTokens.HeroTitle,
                    color = Color.White,
                )
                Text(
                    text = stringResource(R.string.victory_hero_body1),
                    style = LoveTypographyTokens.HeroBody,
                    color = Color.White.copy(0.9f),
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    text = stringResource(R.string.victory_hero_body2),
                    style = LoveTypographyTokens.HeroBody,
                    color = Color.White.copy(0.9f),
                )
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(0.22f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.victory_test_badge),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
            VictoryTrophyDecor(modifier = Modifier.size(72.dp))
        }
    }
}

@Composable
private fun VictoryOutcomePreview(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VictoryOutcomeChip(
                text = stringResource(R.string.victory_outcome_yes),
                background = Color(0xFFE8F5E9),
                foreground = Color(0xFF2E7D32),
            )
            VictoryOutcomeChip(
                text = stringResource(R.string.victory_outcome_maybe),
                background = Color(0xFFFFF8E1),
                foreground = Color(0xFFF57F17),
            )
            VictoryOutcomeChip(
                text = stringResource(R.string.victory_outcome_soon),
                background = LovePrimaryContainer,
                foreground = LovePrimary,
            )
            Text(
                text = stringResource(R.string.victory_outcome_arrow),
                style = MaterialTheme.typography.bodySmall,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun VictoryOutcomeChip(text: String, background: Color, foreground: Color) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = foreground,
        )
    }
}

@Composable
private fun VictoryNameChip(label: String, onSelect: (String) -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = LoveLayout.PresetChipMinHeight)
            .clip(RoundedCornerShape(24.dp))
            .background(LovePrimaryContainer)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = label
            }
            .clickable { onSelect(label) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LoveOnPrimaryContainer,
        )
    }
}

@Composable
internal fun VictoryTrophyDecor(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cup = Path().apply {
                moveTo(w * 0.2f, h * 0.25f)
                lineTo(w * 0.8f, h * 0.25f)
                lineTo(w * 0.72f, h * 0.55f)
                lineTo(w * 0.6f, h * 0.85f)
                lineTo(w * 0.4f, h * 0.85f)
                lineTo(w * 0.28f, h * 0.55f)
                close()
            }
            drawPath(cup, brush = TrophyGoldBrush)
            drawRoundRect(
                brush = TrophyGoldBrush,
                topLeft = Offset(w * 0.12f, h * 0.85f),
                size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.08f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(h * 0.04f),
            )
        }
        Icon(
            Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = Color.White.copy(0.35f),
            modifier = Modifier
                .decorativeForAccessibility()
                .size(36.dp),
        )
    }
}
