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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.prefs.rememberPairedNameFields
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.app.util.loveInputContentPadding
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutline
import dev.lovetest.core.ui.theme.LoveProtocolContainer
import dev.lovetest.core.ui.theme.LoveProtocolHeroGradientColors
import dev.lovetest.core.ui.theme.LoveProtocolPrimary
import dev.lovetest.core.ui.theme.LoveSurface

private val ProtocolHeroBrush = Brush.linearGradient(colors = LoveProtocolHeroGradientColors)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolInputScreen(
    onBack: () -> Unit,
    onSubmit: (String, String) -> Unit,
) {
    val fields = rememberPairedNameFields()
    var name1 by fields.name1
    var name2 by fields.name2
    val canSubmit = name1.isNotBlank() && name2.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.protocol_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        TextButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = LoveProtocolPrimary,
                                modifier = Modifier.decorativeForAccessibility(),
                            )
                            Text(
                                stringResource(R.string.nav_back),
                                color = LoveProtocolPrimary,
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
                LoveShadowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(48.dp),
                    shadowElevation = LoveCardShadowElevation.Hero,
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ProtocolHeroBrush)
                            .padding(20.dp),
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.protocol_hero_title),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Text(
                                text = stringResource(R.string.protocol_hero_body1),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(0.92f),
                                modifier = Modifier.padding(top = 8.dp),
                            )
                            Text(
                                text = stringResource(R.string.protocol_hero_body2),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(0.92f),
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
                        ProtocolNameField(
                            label = stringResource(R.string.protocol_name2_label),
                            value = name2,
                            onValueChange = { name2 = it },
                            placeholder = stringResource(R.string.protocol_name2_hint),
                            modifier = Modifier.padding(top = 16.dp),
                        )
                        Text(
                            text = stringResource(R.string.protocol_tip),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoveOnSurfaceVariant,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    }
                }

                LovePrimaryButton(
                    text = stringResource(R.string.protocol_cta),
                    onClick = { onSubmit(name1, name2) },
                    enabled = canSubmit,
                    modifier = Modifier.padding(top = 24.dp, bottom = 24.dp),
                )
            }
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
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ProtocolStepChip(1, stringResource(R.string.protocol_calculating_step1))
            ProtocolStepChip(2, stringResource(R.string.protocol_calculating_step2))
            ProtocolStepChip(3, stringResource(R.string.protocol_calculating_step3))
        }
    }
}

@Composable
private fun ProtocolStepChip(number: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
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
            style = MaterialTheme.typography.labelLarge,
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
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = LoveOnSurface,
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
                .clip(RoundedCornerShape(28.dp))
                .border(2.dp, borderColor, RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            decorationBox = { inner ->
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(placeholder, color = LoveOnSurfaceVariant)
                }
                inner()
            },
        )
    }
}
