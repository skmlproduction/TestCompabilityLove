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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveFeatureHero
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutline
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

@Composable
fun LoveTestInputScreen(
    onBack: () -> Unit,
    onCalculate: (String, String) -> Unit,
) {
    val fields = rememberPairedNameFields()
    var name1 by fields.name1
    var name2 by fields.name2
    val canSubmit = NameInputValidator.canSubmitPair(name1, name2)

    val pair1 = stringResource(R.string.love_test_pair_example_1)
    val pair2 = stringResource(R.string.love_test_pair_example_2)

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
                title = stringResource(R.string.love_test_title),
                onBack = onBack,
            )

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
                    NameInputHint(name1)
                    LoveTestNameField(
                        label = stringResource(R.string.love_test_name2_label),
                        value = name2,
                        onValueChange = { name2 = it },
                        highlighted = false,
                        showHeartBadge = false,
                        placeholder = stringResource(R.string.love_test_name2_hint),
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    NameInputHint(name2)

                    Text(
                        text = stringResource(R.string.love_test_popular_pairs),
                        style = LoveTypographyTokens.CardCaption,
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
                            style = LoveTypographyTokens.HeroBody,
                            color = LoveOnPrimaryContainer,
                        )
                        Text(
                            text = stringResource(R.string.love_test_tip_line2),
                            style = LoveTypographyTokens.CardCaption,
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
                    .padding(top = 24.dp, bottom = 32.dp),
            )
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
    val body = listOf(bodyLine1, bodyLine2)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" ")
    LoveFeatureHero(
        modifier = modifier,
        minHeight = LoveLayout.LoveTestInputHeroMinHeight,
        shape = LoveLayout.HubHeroShape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
            ) {
                Text(
                    text = title,
                    style = LoveTypographyTokens.HeroTitle,
                    color = Color.White,
                    maxLines = 2,
                )
                if (body.isNotEmpty()) {
                    Text(
                        text = body,
                        style = LoveTypographyTokens.HeroBody.copy(lineHeight = 20.sp),
                        color = Color.White.copy(alpha = 0.92f),
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 3,
                        softWrap = true,
                    )
                }
            }
            LoveHeartIcon(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(40.dp),
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
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = LoveOnSurface,
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                cursorBrush = SolidColor(LovePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .loveInputFieldSemantics(label = label, value = value, placeholder = placeholder),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = LoveTypographyTokens.CardCaption,
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LovePrimaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(Modifier.size(16.dp)) {
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
            .heightIn(min = LoveLayout.PresetChipMinHeight)
            .clip(RoundedCornerShape(24.dp))
            .background(LovePrimaryContainer)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = label
            }
            .clickable {
                val parts = label.split(" + ", limit = 2)
                if (parts.size == 2) {
                    onSelect(parts[0].trim(), parts[1].trim())
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = LoveTypographyTokens.CardCaption,
            color = LoveOnPrimaryContainer,
        )
    }
}
