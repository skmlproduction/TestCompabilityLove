package dev.lovetest.app.ui.share

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lovetest.app.R

private val ShareCardBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFC2185B),
        Color(0xFFE91E63),
        Color(0xFFF8BBD0),
    ),
)

@Composable
fun LoveShareCard(
    percent: Int,
    name1: String,
    name2: String,
    harmonyTag: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(ShareCardBrush)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            ),
    ) {
        Canvas(Modifier.fillMaxWidth().height(420.dp)) {
            drawCircle(
                color = Color.White.copy(0.1f),
                radius = 120.dp.toPx(),
                center = Offset(size.width * 0.82f, size.height * 0.12f),
            )
            drawCircle(
                color = Color.White.copy(0.08f),
                radius = 100.dp.toPx(),
                center = Offset(size.width * 0.18f, size.height * 0.88f),
            )
            val heart = Path().apply {
                moveTo(size.width * 0.72f, size.height * 0.2f)
                cubicTo(
                    size.width * 0.68f, size.height * 0.16f,
                    size.width * 0.76f, size.height * 0.16f,
                    size.width * 0.76f, size.height * 0.2f,
                )
                cubicTo(
                    size.width * 0.76f, size.height * 0.24f,
                    size.width * 0.72f, size.height * 0.28f,
                    size.width * 0.72f, size.height * 0.28f,
                )
                cubicTo(
                    size.width * 0.72f, size.height * 0.28f,
                    size.width * 0.68f, size.height * 0.24f,
                    size.width * 0.68f, size.height * 0.2f,
                )
                close()
            }
            drawPath(heart, Color.White.copy(0.25f), style = Fill)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.share_card_brand),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
                Text(
                    text = stringResource(R.string.share_card_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.92f),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 96.sp),
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(top = 32.dp),
            )
            Text(
                text = stringResource(R.string.share_card_names, name1, name2),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.share_card_compatibility),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(0.92f),
                modifier = Modifier.padding(top = 4.dp),
            )
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(0.2f))
                    .padding(vertical = 14.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = harmonyTag,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = stringResource(R.string.share_card_footer),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(0.75f),
                modifier = Modifier.padding(top = 28.dp),
            )
            Text(
                text = stringResource(R.string.share_card_domain),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(0.75f),
            )
        }
    }
}
