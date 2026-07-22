package dev.lovetest.app.ui.share

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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.components.loveCardShadow
import dev.lovetest.core.ui.theme.LoveOutline
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import kotlinx.coroutines.delay

@Composable
fun AdInterstitialPlaceholder(
    onClose: () -> Unit,
    onPremium: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var secondsLeft by remember { mutableIntStateOf(5) }
    val cd = stringResource(R.string.ad_interstitial_placeholder_cd)

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1_000)
            secondsLeft -= 1
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1C1B1F))
            .semantics { contentDescription = cd },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF49454F))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.ad_badge),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
                Box(
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = LoveLayout.MinTouchTarget,
                            minHeight = LoveLayout.MinTouchTarget,
                        )
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF49454F).copy(alpha = 0.9f))
                        .clickable(enabled = secondsLeft == 0, onClick = onClose)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (secondsLeft > 0) {
                            stringResource(R.string.ad_close_countdown, secondsLeft)
                        } else {
                            stringResource(R.string.ad_close_now)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(
                            alpha = if (secondsLeft > 0) 0.85f else 1f,
                        ),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .loveCardShadow(RoundedCornerShape(16.dp), elevation = LoveCardShadowElevation.Card)
                        .clip(RoundedCornerShape(16.dp))
                        .background(LoveOutlineVariant)
                        .border(
                            width = 3.dp,
                            color = LoveOutline,
                            shape = RoundedCornerShape(16.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = LoveOutline,
                            topLeft = Offset(24f, 24f),
                            size = Size(size.width - 48f, size.height - 48f),
                            cornerRadius = CornerRadius(16.dp.toPx()),
                            style = Stroke(
                                width = 3.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f)),
                            ),
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AdCreativeMock()
                        Text(
                            text = stringResource(R.string.ad_placeholder_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF49454F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                        Text(
                            text = stringResource(R.string.ad_placeholder_sub1),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF79747E),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                        Text(
                            text = stringResource(R.string.ad_placeholder_sub2),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF79747E),
                            textAlign = TextAlign.Center,
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 32.dp)
                                .fillMaxWidth(0.7f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(44.dp))
                                .background(Color(0xFF4285F4)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.ad_cta_mock),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LovePrimary.copy(alpha = 0.85f))
                    .clickable(onClick = onPremium)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LoveHeartIcon(Modifier.size(28.dp), color = Color.White)
                Text(
                    text = stringResource(R.string.ad_premium_escape),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.decorativeForAccessibility(),
                )
            }
            Text(
                text = stringResource(R.string.ad_caption1),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(0.45f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
            Text(
                text = stringResource(R.string.ad_caption2),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(0.45f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 24.dp),
            )
        }
    }
}

@Composable
private fun AdCreativeMock() {
    Box(
        modifier = Modifier
            .size(200.dp, 120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(LoveOutline),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LoveOutlineVariant),
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LoveOutlineVariant),
                    )
                }
            }
        }
    }
}
