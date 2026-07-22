package dev.lovetest.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveTypographyTokens

/** Feature flow header — screen8+: back + centered title, без opaque TopAppBar. */
@Composable
fun LoveFeatureTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    backEnabled: Boolean = true,
    backContentColor: Color = LovePrimary,
) {
    if (onBack == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = title,
                style = LoveTypographyTokens.FeatureScreenTitle,
                color = LoveOnSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        return
    }

    // Row + weight: title gets remaining width (avoids mid-word RU wraps from fixed gutters).
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LoveScreenBackButton(
            onClick = onBack,
            enabled = backEnabled,
            contentColor = if (backEnabled) {
                backContentColor
            } else {
                backContentColor.copy(alpha = 0.38f)
            },
        )
        Text(
            text = title,
            style = LoveTypographyTokens.FeatureScreenTitle,
            color = LoveOnSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        // Optical balance for the back control (~icon + «Назад»).
        Spacer(modifier = Modifier.width(72.dp))
    }
}
