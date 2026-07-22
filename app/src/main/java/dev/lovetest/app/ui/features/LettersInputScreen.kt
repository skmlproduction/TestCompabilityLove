package dev.lovetest.app.ui.features

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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.app.ui.common.NameInputHint
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.app.util.loveInputFieldSemantics
import dev.lovetest.app.util.loveInputLabelForAccessibility
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
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val LettersHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF5C1228),
        LovePrimary,
        LoveHeroEnd,
    ),
)

private val LettersAccent = LovePrimary

@Composable
fun LettersInputScreen(
    onBack: () -> Unit,
    onSubmit: (String, String) -> Unit,
) {
    var word1 by rememberSaveable { mutableStateOf("") }
    var word2 by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        if (DebugUiPreview.matches("letters_input") && word1.isBlank()) {
            word1 = "ЛЮБОВЬ"
            word2 = "СЧАСТЬ"
        }
    }
    val canSubmit = NameInputValidator.canSubmitPair(word1, word2)
    val previewLetters = remember(word1) { previewLettersFromWord(word1) }

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
                title = stringResource(R.string.letters_title),
                onBack = onBack,
                backContentColor = LettersAccent,
            )
            LettersInputHero(modifier = Modifier.padding(top = 8.dp))
                LettersStreamPreview(
                    letters = previewLetters,
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
                        LettersWordField(
                            label = stringResource(R.string.letters_word1_label),
                            value = word1,
                            onValueChange = { word1 = it },
                            highlighted = true,
                            placeholder = "",
                        )
                        NameInputHint(word1)
                        LettersWordField(
                            label = stringResource(R.string.letters_word2_label),
                            value = word2,
                            onValueChange = { word2 = it },
                            highlighted = false,
                            placeholder = stringResource(R.string.letters_word2_hint),
                            modifier = Modifier.padding(top = 20.dp),
                        )
                        NameInputHint(word2)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(LovePrimaryContainer)
                                .padding(16.dp),
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.letters_info_line1),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = LoveOnSurfaceVariant,
                                )
                                Text(
                                    text = stringResource(R.string.letters_info_line2),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LoveOnSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.letters_chip_label),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoveOnSurfaceVariant,
                            modifier = Modifier.padding(top = 20.dp),
                        )
                        LettersExampleChip(stringResource(R.string.letters_chip_1)) { w1, w2 ->
                            word1 = w1
                            word2 = w2
                        }
                    }
                }
                LovePrimaryButton(
                    text = stringResource(R.string.letters_cta),
                    onClick = { onSubmit(word1.trim(), word2.trim()) },
                    enabled = canSubmit,
                    modifier = Modifier.padding(top = 24.dp, bottom = 32.dp),
                )
        }
    }
}

@Composable
private fun LettersInputHero(modifier: Modifier = Modifier) {
    LoveFeatureHero(
        modifier = modifier,
        brush = LettersHeroBrush,
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
                    text = stringResource(R.string.letters_hero_title),
                    style = LoveTypographyTokens.HeroTitle,
                    color = Color.White,
                )
                Text(
                    text = stringResource(R.string.letters_hero_body1),
                    style = LoveTypographyTokens.HeroBody,
                    color = Color.White.copy(0.92f),
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    text = stringResource(R.string.letters_hero_body2),
                    style = LoveTypographyTokens.HeroBody,
                    color = Color.White.copy(0.92f),
                )
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(0.22f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.letters_test_badge),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LettersDecorTile("A")
                LettersDecorTile("Z", alpha = 0.2f)
                LettersDecorTile("a", small = true, alpha = 0.15f)
            }
        }
    }
}

@Composable
private fun LettersDecorTile(
    letter: String,
    small: Boolean = false,
    alpha: Float = 0.25f,
) {
    Box(
        modifier = Modifier
            .size(if (small) 40.dp else 48.dp)
            .clip(RoundedCornerShape(if (small) 12.dp else 14.dp))
            .background(Color.White.copy(alpha)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            style = if (small) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
        )
    }
}

@Composable
private fun LettersStreamPreview(
    letters: List<Char>,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            letters.forEachIndexed { index, letter ->
                LettersStreamTile(
                    letter = letter.toString(),
                    highlight = index == 2,
                )
            }
            Text(text = "+", color = LoveOnSurfaceVariant, fontWeight = FontWeight.Bold)
            LettersStreamTile(letter = "?", muted = true)
            Text(
                text = stringResource(R.string.letters_stream_arrow),
                style = MaterialTheme.typography.bodySmall,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun LettersStreamTile(
    letter: String,
    highlight: Boolean = false,
    muted: Boolean = false,
) {
    val bg = when {
        muted -> LovePrimaryContainer
        highlight -> LovePrimaryContainer
        else -> LoveHeroEnd.copy(alpha = 0.55f)
    }
    val fg = when {
        muted -> LoveOnSurfaceVariant
        highlight -> LovePrimary
        else -> LettersAccent
    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = letter, fontWeight = FontWeight.ExtraBold, color = fg)
    }
}

@Composable
private fun LettersWordField(
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
            color = LettersAccent,
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
                    if (highlighted) LettersAccent else LoveOutline,
                    RoundedCornerShape(28.dp),
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    color = LoveOnSurface,
                    fontWeight = if (highlighted) FontWeight.SemiBold else FontWeight.Normal,
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                cursorBrush = SolidColor(LettersAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .loveInputFieldSemantics(label = label, value = value, placeholder = placeholder),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, color = LoveOnSurfaceVariant)
                    }
                    inner()
                },
            )
        }
    }
}

@Composable
private fun LettersExampleChip(label: String, onSelect: (String, String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .heightIn(min = LoveLayout.PresetChipMinHeight)
            .clip(RoundedCornerShape(24.dp))
            .background(LovePrimaryContainer)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = label
            }
            .clickable {
                val parts = label.split(" + ", limit = 2)
                if (parts.size == 2) onSelect(parts[0].trim(), parts[1].trim())
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = LoveOnPrimaryContainer,
        )
    }
}

private fun previewLettersFromWord(word1: String): List<Char> {
    val fromWord = word1.filter { it.isLetter() }.take(4).map { it.uppercaseChar() }
    return if (fromWord.size >= 4) fromWord else listOf('L', 'O', 'V', 'E')
}
