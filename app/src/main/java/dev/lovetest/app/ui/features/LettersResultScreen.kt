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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import dev.lovetest.core.ui.theme.LoveSurface

private val LettersResultHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF4A148C),
        Color(0xFF6750A4),
        Color(0xFFC2185B),
        Color(0xFFE8DEF8),
    ),
)

private val LettersAccent = Color(0xFF6750A4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LettersResultScreen(
    onShare: () -> Unit,
    onTryAnother: () -> Unit,
    onHome: () -> Unit,
) {
    val word1 = LoveTestSession.name1.ifBlank { "…" }
    val word2 = LoveTestSession.name2.ifBlank { "…" }
    val wordsLine = "${word1.uppercase()} + ${word2.uppercase()}"
    val secretCode = remember(word1, word2) { lettersSecretCode(word1, word2) }
    val percent = LoveTestSession.percent
    val harmonyTag = stringResource(R.string.letters_harmony_tag)
    val shareSheet = rememberLoveShareSheet()
    val context = LocalContext.current
    val shareText = remember(context, percent, word1, word2) {
        context.buildLoveShareText(percent, word1, word2)
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
                            text = stringResource(R.string.letters_result_title),
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
                LettersResultHeroCard(
                    wordsLine = wordsLine,
                    word1 = word1,
                    word2 = word2,
                    secretCode = secretCode,
                    modifier = Modifier.padding(top = 8.dp),
                )
                LettersMessageCard(
                    secretCode = secretCode,
                    modifier = Modifier.padding(top = 20.dp),
                )
                LovePrimaryButton(
                    text = stringResource(R.string.love_test_share_cta),
                    onClick = shareSheet.open,
                    modifier = Modifier.padding(top = 24.dp),
                )
                LoveOutlinedButton(
                    text = stringResource(R.string.letters_try_another),
                    onClick = onTryAnother,
                    modifier = Modifier.padding(top = 12.dp),
                )
                LettersResultHomeButton(
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
                LettersSharePreviewCard(
                    wordsLine = wordsLine,
                    secretCode = secretCode,
                    modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                )
            }
        }
        LoveShareResultOverlay(
            sheet = shareSheet,
            percent = percent,
            name1 = word1,
            name2 = word2,
            harmonyTag = harmonyTag,
            shareText = shareText,
            onShare = onShare,
        )
    }
}

@Composable
private fun LettersResultHeroCard(
    wordsLine: String,
    word1: String,
    word2: String,
    secretCode: Int,
    modifier: Modifier = Modifier,
) {
    val secretCd = stringResource(R.string.letters_secret_cd, secretCode)
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(48.dp),
        shadowElevation = LoveCardShadowElevation.Hero,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LettersResultHeroBrush)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = wordsLine,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            LettersWordTilesRow(
                word = word1,
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = "+",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(0.6f),
                modifier = Modifier.padding(vertical = 8.dp),
            )
            LettersWordTilesRow(word = word2)
            Text(
                text = secretCode.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 88.sp),
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .semantics { contentDescription = secretCd },
            )
            Text(
                text = stringResource(R.string.letters_secret_label),
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
                    text = stringResource(R.string.letters_harmony_tag),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun LettersWordTilesRow(word: String, modifier: Modifier = Modifier) {
    val letters = word.filter { it.isLetter() }.map { it.uppercaseChar() }
    val highlightIndex = lettersHighlightIndex(letters.size)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        letters.forEachIndexed { index, letter ->
            val highlight = index == highlightIndex
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (highlight) LovePrimaryContainer.copy(0.9f)
                        else Color.White.copy(if (highlight) 0.35f else 0.25f),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = letter.toString(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun LettersMessageCard(secretCode: Int, modifier: Modifier = Modifier) {
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
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFE8DEF8)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Aa",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = LettersAccent,
                    )
                }
                Text(
                    text = stringResource(R.string.letters_message_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
            Text(
                text = stringResource(R.string.letters_message_body1),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.letters_message_body2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.letters_message_body3, secretCode),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.letters_message_body4),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
            )
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFE8DEF8))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.letters_code_badge, secretCode),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = LettersAccent,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(LovePrimaryContainer)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.letters_test_badge),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = LoveOnPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun LettersSharePreviewCard(
    wordsLine: String,
    secretCode: Int,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
        border = BorderStroke(2.dp, Color(0xFFE8DEF8)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LettersResultHeroBrush),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = secretCode.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = wordsLine,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                )
                Text(
                    text = stringResource(R.string.letters_share_preview_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.letters_share_preview_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun LettersResultHomeButton(
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

internal fun lettersSecretCode(word1: String, word2: String): Int {
    val combined = (word1 + word2).filter { it.isLetter() }
    if (combined.isEmpty()) return 7
    val sum = combined.sumOf { it.code }
    val mod = sum % 9
    return if (mod == 0) 9 else mod
}

private fun lettersHighlightIndex(length: Int): Int {
    if (length <= 0) return -1
    return (length / 2).coerceIn(0, length - 1)
}
