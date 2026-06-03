package dev.lovetest.app.ui.love

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.app.prefs.rememberPairedNameFields
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveHeroGradientBrush
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutline
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoveTestInputScreen(
    onBack: () -> Unit,
    onCalculate: (String, String) -> Unit,
) {
    val fields = rememberPairedNameFields()
    var name1 by fields.name1
    var name2 by fields.name2
    val canSubmit = name1.isNotBlank() && name2.isNotBlank()

    val pair1 = stringResource(R.string.love_test_pair_example_1)
    val pair2 = stringResource(R.string.love_test_pair_example_2)

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.love_test_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        TextButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = LovePrimary,
                                modifier = Modifier.decorativeForAccessibility(),
                            )
                            Text(
                                text = stringResource(R.string.nav_back),
                                color = LovePrimary,
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
                LoveTestInputHero(
                    title = stringResource(R.string.love_test_hero_title),
                    bodyLine1 = stringResource(R.string.love_test_hero_body_line1),
                    bodyLine2 = stringResource(R.string.love_test_hero_body_line2),
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
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                    ) {
                        LoveTestNameField(
                            label = stringResource(R.string.love_test_name1_label),
                            value = name1,
                            onValueChange = { name1 = it },
                            highlighted = true,
                            showHeartBadge = true,
                            placeholder = "",
                        )
                        LoveTestNameField(
                            label = stringResource(R.string.love_test_name2_label),
                            value = name2,
                            onValueChange = { name2 = it },
                            highlighted = false,
                            showHeartBadge = false,
                            placeholder = stringResource(R.string.love_test_name2_hint),
                            modifier = Modifier.padding(top = 20.dp),
                        )

                        Text(
                            text = stringResource(R.string.love_test_popular_pairs),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoveOnSurfaceVariant,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                        Row(
                            modifier = Modifier.padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            LoveTestPairChip(pair1) { first, second ->
                                name1 = first
                                name2 = second
                            }
                            LoveTestPairChip(pair2) { first, second ->
                                name1 = first
                                name2 = second
                            }
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
                                text = stringResource(R.string.love_test_tip_line1),
                                style = MaterialTheme.typography.bodyMedium,
                                color = LoveOnPrimaryContainer,
                            )
                            Text(
                                text = stringResource(R.string.love_test_tip_line2),
                                style = MaterialTheme.typography.bodySmall,
                                color = LoveOnPrimaryContainer,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }

                LovePrimaryButton(
                    text = stringResource(R.string.love_test_calculate_cta),
                    onClick = { onCalculate(name1.trim(), name2.trim()) },
                    enabled = canSubmit,
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 32.dp)
                        .height(48.dp),
                )
            }
        }
    }
}

@Composable
private fun LoveTestInputHero(
    title: String,
    bodyLine1: String,
    bodyLine2: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(46.dp))
            .background(LoveHeroGradientBrush()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = bodyLine1,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    text = bodyLine2,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
            LoveHeartIcon(
                modifier = Modifier.size(72.dp),
                color = Color.White.copy(alpha = 0.95f),
            )
        }
    }
}

@Composable
private fun LoveTestNameField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    highlighted: Boolean,
    showHeartBadge: Boolean,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = LovePrimary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .border(
                    width = if (highlighted) 2.dp else 1.dp,
                    color = if (highlighted) LovePrimary else LoveOutline,
                    shape = RoundedCornerShape(28.dp),
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
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                cursorBrush = SolidColor(LovePrimary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = LoveOnSurfaceVariant,
                        )
                    }
                    inner()
                },
            )
            if (showHeartBadge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LovePrimaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(Modifier.size(14.dp)) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(cx, cy + size.height * 0.22f)
                            cubicTo(
                                cx - size.width * 0.5f, cy - size.height * 0.1f,
                                cx - size.width * 0.5f, cy - size.height * 0.45f,
                                cx, cy - size.height * 0.2f,
                            )
                            cubicTo(
                                cx + size.width * 0.5f, cy - size.height * 0.45f,
                                cx + size.width * 0.5f, cy - size.height * 0.1f,
                                cx, cy + size.height * 0.22f,
                            )
                            close()
                        }
                        drawPath(path, LovePrimary.copy(alpha = 0.65f))
                    }
                }
            }
        }
    }
}

@Composable
private fun LoveTestPairChip(
    label: String,
    onSelect: (String, String) -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(LovePrimaryContainer)
            .clickable {
                val parts = label.split(" + ", limit = 2)
                if (parts.size == 2) {
                    onSelect(parts[0].trim(), parts[1].trim())
                }
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LoveOnPrimaryContainer,
        )
    }
}
