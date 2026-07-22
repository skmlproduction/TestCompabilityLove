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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import dev.lovetest.app.R
import dev.lovetest.app.prefs.rememberPairedNameFields
import dev.lovetest.app.ui.common.LoveFeatureTopBar
import dev.lovetest.app.ui.common.NameInputHint
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.core.domain.NameInputValidator
import dev.lovetest.app.util.loveInputFieldSemantics
import dev.lovetest.app.util.loveInputLabelForAccessibility
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
import dev.lovetest.core.ui.theme.LoveOutline
import dev.lovetest.core.ui.theme.LoveProtocolContainer
import dev.lovetest.core.ui.theme.LoveProtocolHeroGradientColors
import dev.lovetest.core.ui.theme.LoveProtocolPrimary
import dev.lovetest.core.ui.theme.LoveProtocolPrimaryDark
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val ProtocolHeroBrush = Brush.linearGradient(colors = LoveProtocolHeroGradientColors)

@Composable
fun ProtocolInputScreen(
    onBack: () -> Unit,
    onSubmit: (String, String) -> Unit,
) {
    val fields = rememberPairedNameFields()
    var name1 by fields.name1
    var name2 by fields.name2
    val canSubmit = NameInputValidator.canSubmitPair(name1, name2)

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
                title = stringResource(R.string.protocol_title),
                onBack = onBack,
                backContentColor = LoveProtocolPrimary,
            )
            LoveFeatureHero(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                    brush = ProtocolHeroBrush,
                    minHeight = LoveLayout.ProtocolInputHeroMinHeight,
                    shape = LoveLayout.HubHeroShape,
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Column(modifier = Modifier.align(Alignment.CenterStart)) {
                            Text(
                                text = stringResource(R.string.protocol_hero_title),
                                style = LoveTypographyTokens.HeroTitle,
                                color = Color.White,
                            )
                            Text(
                                text = stringResource(R.string.protocol_hero_body1),
                                style = LoveTypographyTokens.HeroBody,
                                color = Color.White.copy(0.92f),
                                modifier = Modifier.padding(top = 8.dp),
                            )
                            Text(
                                text = stringResource(R.string.protocol_hero_body2),
                                style = LoveTypographyTokens.HeroBody,
                                color = Color.White.copy(0.92f),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.hub_protocol_test_label),
                                style = LoveTypographyTokens.HubHeroChip,
                                color = Color.White,
                            )
                        }
                    }
                }

                ProtocolStepsPreview(modifier = Modifier.padding(top = 16.dp))

                LoveShadowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(38.dp),
                    shadowElevation = LoveCardShadowElevation.Card,
                    colors = CardDefaults.cardColors(containerColor = LoveSurface),
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                        ProtocolNameField(
                            label = stringResource(R.string.protocol_name1_label),
                            value = name1,
                            onValueChange = { name1 = it },
                            highlighted = true,
                        )
                        NameInputHint(name1)
                        ProtocolNameField(
                            label = stringResource(R.string.protocol_name2_label),
                            value = name2,
                            onValueChange = { name2 = it },
                            placeholder = stringResource(R.string.protocol_name2_hint),
                            modifier = Modifier.padding(top = 16.dp),
                        )
                        NameInputHint(name2)
                        Text(
                            text = stringResource(R.string.protocol_tip),
                            style = LoveTypographyTokens.HeroBody,
                            color = LoveProtocolPrimaryDark,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(LoveProtocolContainer)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        )
                    }
                }

                LovePrimaryButton(
                    text = stringResource(R.string.protocol_cta),
                    onClick = { onSubmit(name1.trim(), name2.trim()) },
                    enabled = canSubmit,
                    containerColor = LoveProtocolPrimary,
                    modifier = Modifier.padding(top = 24.dp),
                )
                Text(
                    text = stringResource(R.string.protocol_input_disclaimer),
                    style = LoveTypographyTokens.CardCaption,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 32.dp),
                )
        }
    }
}

@Composable
private fun ProtocolStepsPreview(modifier: Modifier = Modifier) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Subtle,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ProtocolStepChip(1, stringResource(R.string.onboarding_protocol_step_1))
            ProtocolStepChip(2, stringResource(R.string.onboarding_protocol_step_2))
            ProtocolStepChip(3, stringResource(R.string.onboarding_protocol_step_3))
        }
    }
}

@Composable
private fun ProtocolStepChip(number: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(LoveProtocolContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                fontWeight = FontWeight.ExtraBold,
                color = LoveProtocolPrimary,
            )
        }
        Text(
            text = label,
            style = LoveTypographyTokens.CardCaption,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ProtocolNameField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    placeholder: String = "",
) {
    val borderColor = if (highlighted) LoveProtocolPrimary else LoveOutline
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = LoveTypographyTokens.FieldLabel,
            color = LoveProtocolPrimary,
            modifier = Modifier.loveInputLabelForAccessibility(),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Medium,
                color = LoveOnSurface,
            ),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            cursorBrush = SolidColor(LoveProtocolPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(LoveLayout.LoveTestInputFieldHeight)
                .clip(RoundedCornerShape(28.dp))
                .border(
                    width = if (highlighted) 2.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(28.dp),
                )
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .loveInputFieldSemantics(label = label, value = value, placeholder = placeholder),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, color = LoveOnSurfaceVariant)
                    }
                    inner()
                }
            },
        )
    }
}
