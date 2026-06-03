package dev.lovetest.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoveSplashHeroPanel(
    kicker: String,
    line1: String,
    line2: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(27.dp))
            .background(LoveHeroGradientBrush()),
    ) {
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 20.dp),
        ) {
            drawCircle(Color.White.copy(alpha = 0.12f), size.minDimension / 2f)
        }
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 20.dp),
        ) {
            drawCircle(Color.White.copy(alpha = 0.08f), size.minDimension / 2f)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        ) {
            LoveHeartIcon(modifier = Modifier.size(100.dp), color = Color.White.copy(0.95f))
            Text(
                text = kicker,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = Color.White.copy(0.9f),
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = line1,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = line2,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }
}
