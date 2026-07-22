package dev.lovetest.app.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.core.ui.components.LoveOutlinedButton
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.LoveTonalButton
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimaryContainer

/** @deprecated Prefer [LoveTonalButton]; kept for empty-session protocol fallback. */
@Composable
fun LoveFeatureResultHomeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LovePrimaryContainer,
    contentColor: Color = LoveOnPrimaryContainer,
) {
    LoveTonalButton(
        text = text,
        onClick = onClick,
        containerColor = backgroundColor,
        contentColor = contentColor,
        modifier = modifier,
    )
}

@Composable
fun LoveFeatureResultActions(
    tryAgainLabel: String,
    onShare: () -> Unit,
    onTryAgain: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier,
    topSpacing: androidx.compose.ui.unit.Dp = 24.dp,
    @StringRes shareCtaRes: Int = R.string.love_test_share_cta,
    @StringRes disclaimerRes: Int = R.string.result_entertainment_only,
    primaryContainerColor: Color = MaterialTheme.colorScheme.primary,
    primaryContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    outlinedContentColor: Color = MaterialTheme.colorScheme.primary,
    homeBackgroundColor: Color = LovePrimaryContainer,
    homeContentColor: Color = LoveOnPrimaryContainer,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LovePrimaryButton(
            text = stringResource(shareCtaRes),
            onClick = onShare,
            containerColor = primaryContainerColor,
            contentColor = primaryContentColor,
            modifier = Modifier.padding(top = topSpacing),
        )
        LoveOutlinedButton(
            text = tryAgainLabel,
            onClick = onTryAgain,
            contentColor = outlinedContentColor,
            borderColor = outlinedContentColor,
            modifier = Modifier.padding(top = 12.dp),
        )
        LoveTonalButton(
            text = stringResource(R.string.love_test_back_home),
            onClick = onHome,
            containerColor = homeBackgroundColor,
            contentColor = homeContentColor,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = stringResource(disclaimerRes),
            style = MaterialTheme.typography.bodySmall,
            color = LoveOnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        )
    }
}
