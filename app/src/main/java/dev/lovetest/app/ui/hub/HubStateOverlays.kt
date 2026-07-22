package dev.lovetest.app.ui.hub

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveOutlinedButton
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.components.loveCardShadow
import dev.lovetest.core.ui.theme.LoveErrorContainer
import dev.lovetest.core.ui.theme.LoveOnErrorContainer
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSurface

@Composable
fun HubLoadingOverlay(modifier: Modifier = Modifier) {
    val loadingCd = stringResource(R.string.hub_loading_cd)
    val panelShape = RoundedCornerShape(38.dp)
    val progress = remember { Animatable(0.63f) }
    // Finite pulse: infinite loop keeps Compose test idling resources busy forever.
    LaunchedEffect(Unit) {
        repeat(3) {
            progress.animateTo(0.85f, tween(1400, easing = LinearEasing))
            progress.animateTo(0.45f, tween(1400, easing = LinearEasing))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = loadingCd },
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .loveCardShadow(panelShape, elevation = LoveCardShadowElevation.Hero)
                .border(2.dp, LovePrimaryContainer, panelShape),
            shape = panelShape,
            colors = CardDefaults.cardColors(containerColor = LoveSurface.copy(alpha = 0.88f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    progress = { progress.value.coerceIn(0f, 1f) },
                    modifier = Modifier.size(72.dp),
                    color = LovePrimary,
                    strokeWidth = 12.dp,
                    trackColor = LovePrimaryContainer,
                )
                Text(
                    text = stringResource(R.string.hub_loading_overlay_status),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LovePrimary,
                    modifier = Modifier.padding(top = 24.dp),
                )
                Text(
                    text = stringResource(R.string.hub_loading_overlay_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text = stringResource(R.string.hub_loading_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp),
                )
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth(0.75f)
                        .clip(RoundedCornerShape(5.dp)),
                    color = LovePrimary,
                    trackColor = LoveOutlineVariant,
                )
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(LovePrimaryContainer)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.hub_loading_cancel_unavailable),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = LoveOnPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
fun HubErrorTopBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = LoveErrorContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.WifiOff,
                contentDescription = null,
                tint = LoveOnErrorContainer,
                modifier = Modifier.decorativeForAccessibility(),
            )
            Text(
                text = stringResource(R.string.error_network_banner),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = LoveOnErrorContainer,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
    }
}

@Composable
fun HubErrorNetworkOverlay(
    onRetry: () -> Unit,
    onContinueOffline: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val panelShape = RoundedCornerShape(38.dp)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .loveCardShadow(panelShape, elevation = LoveCardShadowElevation.Hero)
                .border(4.dp, LoveErrorContainer, panelShape),
            shape = panelShape,
            colors = CardDefaults.cardColors(containerColor = LoveSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(LoveErrorContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = LoveOnErrorContainer,
                        modifier = Modifier
                            .size(48.dp)
                            .decorativeForAccessibility(),
                    )
                }
                Text(
                    text = stringResource(R.string.error_network_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnErrorContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp),
                )
                Text(
                    text = stringResource(R.string.error_network_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = stringResource(R.string.error_network_body_offline),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text = stringResource(R.string.error_network_body_ads),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
                LovePrimaryButton(
                    text = stringResource(R.string.error_retry_cta),
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 28.dp),
                )
                LoveOutlinedButton(
                    text = stringResource(R.string.error_network_continue_offline),
                    onClick = onContinueOffline,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}
