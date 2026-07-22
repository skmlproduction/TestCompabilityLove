package dev.lovetest.app.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.core.ui.components.loveEdgeToEdgeScreenPadding
import dev.lovetest.core.ui.theme.LoveTypographyTokens

@Composable
fun WheelSharePreview(
    prize: String,
    shareText: String,
    onDismiss: () -> Unit,
    onShareFallback: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val cardCd = stringResource(R.string.share_preview_cd)
    val dismissCd = stringResource(R.string.share_cancel)
    val consumeClicks = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1C1B1F).copy(alpha = 0.55f))
            .loveEdgeToEdgeScreenPadding(includeNavigationBar = false)
            .navigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable(onClick = onDismiss)
                .semantics { contentDescription = dismissCd },
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        indication = null,
                        interactionSource = consumeClicks,
                        onClick = {},
                    )
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.share_preview_title),
                    style = LoveTypographyTokens.FeatureScreenTitle,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp, bottom = 20.dp),
                )
                WheelShareCard(
                    prize = prize,
                    contentDescription = cardCd,
                )
            }
        }
        ShareActionsPanel(
            shareText = shareText,
            cardContent = null,
            wheelPrize = prize,
            onShareFallback = onShareFallback,
            onDismiss = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
