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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
import dev.lovetest.app.prefs.rememberPairedNameFields
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.app.ui.common.NameInputHint
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.core.domain.NameInputValidator
import dev.lovetest.app.util.loveInputFieldSemantics
import dev.lovetest.app.util.loveInputLabelForAccessibility
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveFeatureHero
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import dev.lovetest.core.ui.theme.LoveHeroEnd
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutline
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSecondary
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val PairHeroBrush = Brush.linearGradient(
    colors = listOf(
        LovePrimary,
        LoveSecondary,
        LoveHeroEnd,
    ),
)

@Composable
fun PairInputScreen(
    onBack: () -> Unit,
    onSubmit: (String, String) -> Unit,
) {
    val fields = rememberPairedNameFields()
    var name1 by fields.name1
    var name2 by fields.name2
    val canSubmit = NameInputValidator.canSubmitPair(name1, name2)
    val initial1 = name1.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val initial2 = name2.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"

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
                title = stringResource(R.string.pair_title),
                onBack = onBack,
            )
            PairInputHero(modifier = Modifier.padding(top = 8.dp))
                PairPreviewStrip(
                    initial1 = initial1,
                    initial2 = initial2,
                    modifier = Modifier.padding(top = 16.dp),
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
                        PairNameField(
                            label = stringResource(R.string.pair_name1_label),
                            value = name1,
                            onValueChange = { name1 = it },
                            highlighted = true,
                            placeholder = "",
                        )
                        NameInputHint(name1)
                        PairNameField(
                            label = stringResource(R.string.pair_name2_label),
                            value = name2,
                            onValueChange = { name2 = it },
                            highlighted = false,
                            placeholder = stringResource(R.string.pair_name2_hint),
                            modifier = Modifier.padding(top = 20.dp),
                        )
                        NameInputHint(name2)
                        Text(
                            text = stringResource(R.string.pair_quick_pick),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoveOnSurfaceVariant,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                        Row(
                            modifier = Modifier.padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            PairChip(stringResource(R.string.pair_chip_1)) { a, b ->
                                name1 = a
                                name2 = b
                            }
                            PairChip(stringResource(R.string.pair_chip_2)) { a, b ->
                                name1 = a
                                name2 = b
                            }
                        }
                        Text(
                            text = stringResource(R.string.pair_privacy_note),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoveOnPrimaryContainer,
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(LovePrimaryContainer)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }
                }
                LovePrimaryButton(
                    text = stringResource(R.string.pair_cta),
                    onClick = { onSubmit(name1.trim(), name2.trim()) },
                    enabled = canSubmit,
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 32.dp),
                )
        }
    }
}

@Composable
private fun PairInputHero(modifier: Modifier = Modifier) {
    LoveFeatureHero(
        modifier = modifier,
        brush = PairHeroBrush,
        minHeight = LoveLayout.FeatureHeroTallMinHeight,
        shape = LoveLayout.HubHeroShape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(0.25f)))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.35f)),
                    contentAlignment = Alignment.Center,
                ) {
                    LoveHeartIcon(Modifier.size(20.dp), color = Color.White)
                }
                Box(Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(0.25f)))
            }
            Text(
                text = stringResource(R.string.pair_hero_title),
                style = LoveTypographyTokens.HeroTitle,
                color = Color.White,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = stringResource(R.string.pair_hero_body1),
                style = LoveTypographyTokens.HeroBody,
                color = Color.White.copy(0.9f),
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = stringResource(R.string.pair_hero_body2),
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
                    text = stringResource(R.string.pair_test_badge),
                    style = LoveTypographyTokens.HubHeroChip,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun PairPreviewStrip(
    initial1: String,
    initial2: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp),
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val y = size.height / 2f
                drawLine(
                    color = LovePrimary.copy(0.5f),
                    start = androidx.compose.ui.geometry.Offset(size.width * 0.25f, y),
                    end = androidx.compose.ui.geometry.Offset(size.width * 0.75f, y),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PairAvatar(initial1, LovePrimaryContainer, LovePrimary)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LovePrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    LoveHeartIcon(Modifier.size(22.dp), color = Color.White)
                }
                PairAvatar(initial2, LoveHeroEnd, LoveOnPrimaryContainer)
            }
        }
    }
}

@Composable
private fun PairAvatar(letter: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = fg,
        )
    }
}

@Composable
private fun PairNameField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    highlighted: Boolean,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = LoveTypographyTokens.FieldLabel,
            color = LovePrimary,
            modifier = Modifier.loveInputLabelForAccessibility(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(LoveLayout.LoveTestInputFieldHeight)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .border(
                    if (highlighted) 2.dp else 1.dp,
                    if (highlighted) LovePrimary else LoveOutline,
                    RoundedCornerShape(28.dp),
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(fontSize = 18.sp, color = LoveOnSurface),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                cursorBrush = SolidColor(LovePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .loveInputFieldSemantics(label = label, value = value, placeholder = placeholder),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = LoveOnSurfaceVariant)
                    }
                    inner()
                },
            )
        }
    }
}

@Composable
private fun PairChip(label: String, onSelect: (String, String) -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = LoveLayout.PresetChipMinHeight)
            .clip(RoundedCornerShape(28.dp))
            .background(LovePrimaryContainer)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = label
            }
            .clickable {
                val parts = label.split(" + ", limit = 2)
                if (parts.size == 2) onSelect(parts[0].trim(), parts[1].trim())
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LoveOnPrimaryContainer,
            textAlign = TextAlign.Center,
        )
    }
}
