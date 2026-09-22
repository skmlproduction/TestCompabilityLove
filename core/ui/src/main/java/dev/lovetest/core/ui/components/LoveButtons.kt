package dev.lovetest.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.lovetest.core.ui.theme.VelvetAccentBrush
import dev.lovetest.core.ui.theme.VelvetText

private val PrimaryButtonShape = RoundedCornerShape(22.dp)
private val SecondaryButtonShape = RoundedCornerShape(18.dp)

/**
 * Velvet primary CTA — rose→coral gradient pill with glow.
 * Если caller передаёт явный containerColor — рисуем solid (legacy-совместимость).
 */
@Composable
fun LovePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color? = null,
    contentColor: Color = VelvetText,
) {
    val shape = PrimaryButtonShape
    val bgModifier = if (containerColor == null && enabled) {
        Modifier
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Card)
            .clip(shape)
            .background(VelvetAccentBrush)
    } else {
        Modifier
            .clip(shape)
            .background(containerColor ?: MaterialTheme.colorScheme.surfaceVariant)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = LoveLayout.PrimaryCtaHeight)
            .then(bgModifier)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) contentColor else contentColor.copy(alpha = 0.5f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun LoveOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = LoveLayout.SecondaryCtaHeight),
        shape = SecondaryButtonShape,
        border = BorderStroke(1.5.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

/** Tonal secondary CTA — glass pill on dark. */
@Composable
fun LoveTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = LoveLayout.SecondaryCtaHeight),
        shape = SecondaryButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.38f),
            disabledContentColor = contentColor.copy(alpha = 0.38f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
