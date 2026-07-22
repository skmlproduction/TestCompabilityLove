package dev.lovetest.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.loveCardShadow

@Composable
fun LoveHeroCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .loveCardShadow(LoveLayout.HeroShape, elevation = LoveCardShadowElevation.Card)
            .clip(LoveLayout.HeroShape)
            .background(LoveHeroGradientBrush())
            .padding(24.dp),
    ) {
        Column {
            if (kicker != null) {
                Text(
                    text = kicker,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.padding(top = 8.dp),
            )
            trailing?.let {
                Box(modifier = Modifier.padding(top = 16.dp)) { it() }
            }
        }
    }
}

@Composable
fun LoveSplashHero(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .loveCardShadow(shape, elevation = LoveCardShadowElevation.Hero)
            .clip(shape)
            .background(LoveHeroGradientBrush())
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LoveHeartIcon(
                modifier = Modifier.size(120.dp),
                color = Color.White.copy(alpha = 0.95f),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.padding(top = 24.dp),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp),
            )
        }
    }
}
