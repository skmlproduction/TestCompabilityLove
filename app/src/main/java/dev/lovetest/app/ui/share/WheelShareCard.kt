package dev.lovetest.app.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveSurface

private val WheelShareHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFC2185B),
        Color(0xFFE91E63),
        Color(0xFFF8BBD0),
    ),
)

@Composable
fun WheelShareCard(
    prize: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    LoveShadowCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics { this.contentDescription = contentDescription },
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(WheelShareHeroBrush),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = prize,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = stringResource(R.string.wheel_share_preview_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LoveOnSurface,
                )
                Text(
                    text = stringResource(R.string.wheel_share_preview_body, prize),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.wheel_share_preview_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
