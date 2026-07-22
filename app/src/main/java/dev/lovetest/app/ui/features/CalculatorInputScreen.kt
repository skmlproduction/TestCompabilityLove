package dev.lovetest.app.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.app.ui.common.NameInputHint
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.app.util.loveInputFieldSemantics
import dev.lovetest.app.util.loveInputLabelForAccessibility
import dev.lovetest.app.prefs.rememberPairedNameFields
import dev.lovetest.core.domain.NameInputValidator
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveFeatureHero
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import dev.lovetest.core.ui.theme.LoveHeroEnd
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutline
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val CalculatorHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF5C1228),
        LovePrimary,
        LoveHeroEnd,
    ),
)

@Composable
fun CalculatorInputScreen(
    onBack: () -> Unit,
    onSubmit: (String, String) -> Unit,
) {
    val fields = rememberPairedNameFields()
    var name1 by fields.name1
    var name2 by fields.name2
    val canSubmit = NameInputValidator.canSubmitPair(name1, name2)
    val letterPreview = name1.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"

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
                title = stringResource(R.string.calculator_title),
                onBack = onBack,
            )
            CalculatorHeroCard(
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
                        CalculatorNameField(
                            label = stringResource(R.string.calculator_name1_label),
                            value = name1,
                            onValueChange = { name1 = it },
                            highlighted = true,
                            badge = "1",
                            placeholder = "",
                        )
                        NameInputHint(name1)
                        CalculatorNameField(
                            label = stringResource(R.string.calculator_name2_label),
                            value = name2,
                            onValueChange = { name2 = it },
                            highlighted = false,
                            badge = "2",
                            placeholder = stringResource(R.string.calculator_name2_hint),
                            modifier = Modifier.padding(top = 20.dp),
                        )
                        NameInputHint(name2)
                        Text(
                            text = stringResource(R.string.calculator_letters_preview),
                            style = LoveTypographyTokens.CardCaption,
                            color = LoveOnSurfaceVariant,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                        Row(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            LetterChip(letterPreview, primary = true)
                            LetterChip("+", primary = true)
                            LetterChip("?", primary = false)
                            Text(
                                text = stringResource(R.string.calculator_letters_arrow),
                                style = MaterialTheme.typography.bodySmall,
                                color = LoveOnSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(LovePrimaryContainer)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.calculator_info_line1),
                                style = LoveTypographyTokens.HeroBody,
                                color = LoveOnPrimaryContainer,
                            )
                            Text(
                                text = stringResource(R.string.calculator_info_line2),
                                style = LoveTypographyTokens.CardCaption,
                                color = LoveOnPrimaryContainer,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
                LovePrimaryButton(
                    text = stringResource(R.string.calculator_cta),
                    onClick = { onSubmit(name1.trim(), name2.trim()) },
                    enabled = canSubmit,
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 32.dp),
                )
        }
    }
}

@Composable
private fun CalculatorHeroCard(modifier: Modifier = Modifier) {
    LoveFeatureHero(
        modifier = modifier,
        brush = CalculatorHeroBrush,
        minHeight = LoveLayout.LoveTestInputHeroMinHeight,
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
                    text = stringResource(R.string.calculator_hero_title),
                    style = LoveTypographyTokens.HeroTitle,
                    color = Color.White,
                )
                Text(
                    text = stringResource(R.string.calculator_hero_body1),
                    style = LoveTypographyTokens.HeroBody,
                    color = Color.White.copy(0.9f),
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    text = stringResource(R.string.calculator_hero_body2),
                    style = LoveTypographyTokens.HeroBody,
                    color = Color.White.copy(0.9f),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = Color.White.copy(0.35f),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LetterBadge("A")
                    LetterBadge("Z", alpha = 0.15f)
                }
            }
        }
    }
}

@Composable
private fun LetterBadge(letter: String, alpha: Float = 0.2f) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
        )
    }
}

@Composable
private fun CalculatorNameField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    highlighted: Boolean,
    badge: String,
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
                    width = if (highlighted) 2.dp else 1.dp,
                    color = if (highlighted) LovePrimary else LoveOutline,
                    shape = RoundedCornerShape(28.dp),
                )
                .padding(start = 16.dp, end = 56.dp),
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
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (highlighted) LovePrimaryContainer else LoveOutlineVariant)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (highlighted) LovePrimary else LoveOnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LetterChip(letter: String, primary: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (primary) LovePrimaryContainer else LoveHeroEnd.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (primary) LovePrimary else LoveOnPrimaryContainer,
            textAlign = TextAlign.Center,
        )
    }
}
