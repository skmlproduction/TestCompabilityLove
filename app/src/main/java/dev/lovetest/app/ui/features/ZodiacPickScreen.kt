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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveFeatureHero
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens
import dev.lovetest.core.ui.theme.LoveZodiacAccentPink
import dev.lovetest.core.ui.theme.LoveZodiacHeroBrush
import dev.lovetest.core.ui.theme.LoveZodiacSlotUnselected
import dev.lovetest.core.ui.theme.LoveZodiacViolet

@Composable
fun ZodiacPickScreen(
    onBack: () -> Unit,
    onSubmit: (String, String) -> Unit,
) {
    val signs = stringArrayResource(R.array.zodiac_signs).toList()
    var sign1 by rememberSaveable { mutableStateOf("") }
    var sign2 by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(signs) {
        if (DebugUiPreview.matches("zodiac_pick") && sign1.isBlank()) {
            sign1 = signs.getOrElse(4) { "" }
            sign2 = signs.getOrElse(10) { "" }
        }
    }
    val canSubmit = sign1.isNotBlank() && sign2.isNotBlank()

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
                title = stringResource(R.string.zodiac_title),
                onBack = onBack,
                backContentColor = LoveZodiacViolet,
            )
            ZodiacPickHero(modifier = Modifier.padding(top = 8.dp))
                ZodiacSelectedSlots(
                    sign1 = sign1,
                    sign2 = sign2,
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
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(R.string.zodiac_all_signs),
                            style = LoveTypographyTokens.ScreenHeadline,
                            color = LoveOnSurface,
                        )
                        signs.chunked(3).forEachIndexed { rowIndex, row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = if (rowIndex == 0) 16.dp else 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                row.forEach { sign ->
                                    val selectedAs1 = sign == sign1
                                    val selectedAs2 = sign == sign2
                                    ZodiacSignCell(
                                        sign = sign,
                                        isSelected = selectedAs1 || selectedAs2,
                                        slot1 = selectedAs1,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            when {
                                                sign == sign1 -> sign1 = ""
                                                sign == sign2 -> sign2 = ""
                                                sign1.isBlank() -> sign1 = sign
                                                sign2.isBlank() -> sign2 = sign
                                                else -> sign2 = sign
                                            }
                                        },
                                    )
                                }
                                repeat(3 - row.size) {
                                    Box(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        Text(
                            text = stringResource(R.string.zodiac_pick_hint),
                            style = LoveTypographyTokens.HeroBody,
                            color = LoveOnSurfaceVariant,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    }
                }
                LovePrimaryButton(
                    text = stringResource(R.string.zodiac_cta),
                    onClick = { onSubmit(sign1, sign2) },
                    enabled = canSubmit,
                    modifier = Modifier.padding(top = 24.dp, bottom = 32.dp),
                )
                Text(
                    text = stringResource(R.string.result_entertainment_only),
                    style = LoveTypographyTokens.CardCaption,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                )
        }
    }
}

@Composable
private fun ZodiacPickHero(modifier: Modifier = Modifier) {
    LoveFeatureHero(
        modifier = modifier,
        brush = LoveZodiacHeroBrush,
        minHeight = LoveLayout.ZodiacPickHeroMinHeight,
        shape = LoveLayout.HubHeroShape,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ZodiacCosmicStarDecor(Modifier.matchParentSize())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.zodiac_hero_title),
                        style = LoveTypographyTokens.HeroTitle,
                        color = Color.White,
                    )
                    Text(
                        text = stringResource(R.string.zodiac_hero_body),
                        style = LoveTypographyTokens.HeroBody,
                        color = Color.White.copy(0.92f),
                        modifier = Modifier.padding(top = 6.dp),
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White.copy(0.22f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.zodiac_test_badge),
                            style = LoveTypographyTokens.HubHeroChip,
                            color = Color.White,
                        )
                    }
                }
                ZodiacOrbitDecor(modifier = Modifier.size(72.dp))
            }
        }
    }
}

@Composable
private fun ZodiacCosmicStarDecor(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stars = listOf(
            Offset(size.width * 0.12f, size.height * 0.22f) to 2.5.dp.toPx(),
            Offset(size.width * 0.28f, size.height * 0.12f) to 1.8.dp.toPx(),
            Offset(size.width * 0.62f, size.height * 0.18f) to 2.dp.toPx(),
            Offset(size.width * 0.82f, size.height * 0.35f) to 2.2.dp.toPx(),
            Offset(size.width * 0.9f, size.height * 0.55f) to 1.6.dp.toPx(),
        )
        stars.forEach { (center, radius) ->
            drawCircle(color = Color.White.copy(0.55f), radius = radius, center = center)
        }
    }
}

@Composable
private fun ZodiacOrbitDecor(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(0.4f),
                radius = 40.dp.toPx(),
                center = Offset(size.width / 2f, size.height / 2f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()),
            )
            drawCircle(
                color = Color.White.copy(0.3f),
                radius = 24.dp.toPx(),
                center = Offset(size.width / 2f, size.height / 2f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
            )
            drawCircle(
                color = Color.White.copy(0.8f),
                radius = 6.dp.toPx(),
                center = Offset(size.width / 2f, size.height / 2f),
            )
        }
    }
}

@Composable
private fun ZodiacSelectedSlots(
    sign1: String,
    sign2: String,
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
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ZodiacSlot(
                label = stringResource(R.string.zodiac_sign1_label),
                sign = sign1,
                color = LoveZodiacViolet,
                modifier = Modifier.weight(1f),
            )
            ZodiacSlot(
                label = stringResource(R.string.zodiac_sign2_label),
                sign = sign2,
                color = LoveZodiacAccentPink,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ZodiacSlot(
    label: String,
    sign: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = LoveTypographyTokens.FieldLabel,
            color = LoveZodiacViolet,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(if (sign.isNotBlank()) color else LoveZodiacSlotUnselected),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = sign.ifBlank { "—" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (sign.isNotBlank()) Color.White else LoveOnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ZodiacSignCell(
    sign: String,
    isSelected: Boolean,
    slot1: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg = when {
        isSelected && slot1 -> LoveZodiacViolet
        isSelected -> LoveZodiacAccentPink
        else -> LoveZodiacSlotUnselected
    }
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .then(
                if (isSelected) Modifier.border(2.dp, Color.White, RoundedCornerShape(24.dp))
                else Modifier,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = sign
                if (isSelected) {
                    selected = true
                }
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = sign,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else LoveOnSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .decorativeForAccessibility(),
        )
    }
}
