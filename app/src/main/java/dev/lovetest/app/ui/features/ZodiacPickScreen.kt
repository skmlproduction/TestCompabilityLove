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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveSurface

private val ZodiacHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1A237E),
        Color(0xFF512DA8),
        Color(0xFFC2185B),
    ),
)

private val ZodiacAccent = Color(0xFF512DA8)
private val ZodiacSlot1Color = Color(0xFF512DA8)
private val ZodiacSlot2Color = LovePrimary

@OptIn(ExperimentalMaterial3Api::class)
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

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.zodiac_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        TextButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = ZodiacAccent,
                                modifier = Modifier.decorativeForAccessibility(),
                            )
                            Text(
                                stringResource(R.string.nav_back),
                                color = ZodiacAccent,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
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
                    .loveInputContentPadding()
                    .padding(horizontal = 24.dp),
            ) {
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
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
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
                                        selected = selectedAs1 || selectedAs2,
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
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoveOnSurfaceVariant,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    }
                }
                LovePrimaryButton(
                    text = stringResource(R.string.zodiac_cta),
                    onClick = { onSubmit(sign1, sign2) },
                    enabled = canSubmit,
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 32.dp)
                        .height(48.dp),
                )
                Text(
                    text = stringResource(R.string.result_entertainment_only),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                )
            }
        }
    }
}

@Composable
private fun ZodiacPickHero(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(46.dp))
            .background(ZodiacHeroBrush),
    ) {
        Canvas(Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(0.4f),
                radius = 40.dp.toPx(),
                center = Offset(size.width * 0.82f, size.height * 0.45f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()),
            )
            drawCircle(
                color = Color.White.copy(0.3f),
                radius = 24.dp.toPx(),
                center = Offset(size.width * 0.82f, size.height * 0.45f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
            )
            drawCircle(
                color = Color.White.copy(0.8f),
                radius = 6.dp.toPx(),
                center = Offset(size.width * 0.82f, size.height * 0.45f),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(20.dp)
                .padding(end = 100.dp),
        ) {
            Text(
                text = stringResource(R.string.zodiac_hero_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = stringResource(R.string.zodiac_hero_body),
                style = MaterialTheme.typography.bodyMedium,
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
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
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
                color = ZodiacSlot1Color,
                modifier = Modifier.weight(1f),
            )
            ZodiacSlot(
                label = stringResource(R.string.zodiac_sign2_label),
                sign = sign2,
                color = ZodiacSlot2Color,
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
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = ZodiacAccent,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(if (sign.isNotBlank()) color else Color(0xFFF3EDF7)),
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
    selected: Boolean,
    slot1: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg = when {
        selected && slot1 -> ZodiacSlot1Color
        selected -> ZodiacSlot2Color
        else -> Color(0xFFF3EDF7)
    }
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .then(
                if (selected) Modifier.border(2.dp, Color.White, RoundedCornerShape(24.dp))
                else Modifier,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = sign,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else LoveOnSurface,
            textAlign = TextAlign.Center,
        )
    }
}
