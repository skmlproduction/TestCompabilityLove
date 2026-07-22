package dev.lovetest.app.ui.share

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import dev.lovetest.app.R
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.theme.LoveHeroEnd
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveSecondary
import dev.lovetest.core.ui.theme.LoveTypographyTokens

private val ShareCardBrush = Brush.linearGradient(
    colors = listOf(
        LovePrimary,
        LoveSecondary,
        LoveHeroEnd,
    ),
)

private val ShareCardMutedBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF6B5E66),
        Color(0xFF9A8A90),
        Color(0xFFEDE0E4),
    ),
)

@Composable
fun LoveShareCard(
    percent: Int,
    name1: String,
    name2: String,
    harmonyTag: String,
    modifier: Modifier = Modifier,
    high: Boolean = true,
    contentDescription: String? = null,
) {
    val cardBrush = if (high) ShareCardBrush else ShareCardMutedBrush
    val percentStyle = LoveTypographyTokens.percentForRing(LoveLayout.LoveShareCardHeight * 0.78f)
    val primaryText = if (high) Color.White else LoveOnSurface
    val secondaryText = if (high) Color.White.copy(0.92f) else LoveOnSurfaceVariant
    val footerText = if (high) Color.White.copy(0.75f) else LoveOnSurfaceVariant.copy(0.85f)
    val decorAlpha = if (high) 1f else 0.55f
    val pillBackground = if (high) Color.White.copy(0.2f) else Color.White.copy(0.45f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(LoveLayout.LoveShareCardHeight)
            .clip(RoundedCornerShape(40.dp))
            .background(cardBrush)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            ),
    ) {
        Canvas(Modifier.fillMaxWidth().height(LoveLayout.LoveShareCardHeight)) {
            drawCircle(
                color = Color.White.copy(0.1f * decorAlpha),
                radius = 120.dp.toPx(),
                center = Offset(size.width * 0.82f, size.height * 0.12f),
            )
            drawCircle(
                color = Color.White.copy(0.08f * decorAlpha),
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
            drawPath(heart, Color.White.copy(0.25f * decorAlpha), style = Fill)
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
                    style = LoveTypographyTokens.CardTitleLight,
                    color = primaryText,
                )
                Text(
                    text = stringResource(R.string.share_card_tagline),
                    style = LoveTypographyTokens.CardCaptionLight,
                    color = secondaryText,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                text = "$percent%",
                style = percentStyle,
                fontWeight = FontWeight.ExtraBold,
                color = primaryText,
                modifier = Modifier.padding(top = 40.dp),
            )
            Text(
                text = stringResource(R.string.share_card_names, name1, name2),
                style = LoveTypographyTokens.CardTitleLight,
                color = primaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.share_card_compatibility),
                style = LoveTypographyTokens.HeroBody,
                color = secondaryText,
                modifier = Modifier.padding(top = 4.dp),
            )
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(pillBackground)
                    .padding(vertical = 14.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = harmonyTag,
                    style = LoveTypographyTokens.CardTitleLight,
                    color = primaryText,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = stringResource(R.string.share_card_footer),
                style = LoveTypographyTokens.CardCaption,
                color = footerText,
                modifier = Modifier.padding(top = 28.dp),
            )
            Text(
                text = stringResource(R.string.share_card_domain),
                style = LoveTypographyTokens.CardCaption,
                color = footerText,
            )
        }
    }
}
