package dev.lovetest.app.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.lovetest.app.R
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHeartIcon
import dev.lovetest.core.ui.components.LoveSplashBackground
import dev.lovetest.core.ui.components.LoveSplashHeroPanel
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    onNavigate: (String) -> Unit,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()
    val progress by viewModel.loadProgress.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        if (destination is SplashDestination.Navigate) {
            onNavigate((destination as SplashDestination.Navigate).route)
        }
    }

    val loadingCd = stringResource(R.string.splash_loading_cd)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = loadingCd },
    ) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveSplashBackground(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(48.dp))

            LoveSplashHeroPanel(
                kicker = stringResource(R.string.splash_brand_kicker),
                line1 = stringResource(R.string.splash_hero_line1),
                line2 = stringResource(R.string.splash_hero_line2),
            )

            Text(
                text = stringResource(R.string.splash_headline_line1),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 28.dp),
            )
            Text(
                text = stringResource(R.string.splash_headline_line2),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.splash_body_line1),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = stringResource(R.string.splash_body_line2),
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = stringResource(R.string.splash_loading_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = LoveOnPrimaryContainer,
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LovePrimary,
                trackColor = LoveOutlineVariant,
                strokeCap = StrokeCap.Round,
            )
            CircularProgressIndicator(
                modifier = Modifier.size(56.dp),
                color = LovePrimary,
                trackColor = LovePrimaryContainer,
                strokeWidth = 5.dp,
                progress = { progress },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            ) {
                SplashFeatureChip(
                    label = stringResource(R.string.splash_chip_test),
                    icon = { LoveHeartIcon(modifier = Modifier.size(18.dp), color = LovePrimary) },
                )
                SplashFeatureChip(
                    label = stringResource(R.string.splash_chip_zodiac),
                    icon = { SplashRingIcon() },
                )
                SplashFeatureChip(
                    label = stringResource(R.string.splash_chip_wheel),
                    icon = { SplashRingIcon() },
                )
            }

            Text(
                text = stringResource(R.string.splash_footer),
                style = MaterialTheme.typography.bodySmall,
                color = LoveOnSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 24.dp),
            )
        }
    }
}

@Composable
private fun SplashRingIcon() {
    Canvas(Modifier.size(18.dp)) {
        drawCircle(
            color = LovePrimary,
            radius = 8.dp.toPx(),
            style = androidx.compose.ui.graphics.drawscope.Stroke(3.dp.toPx()),
        )
    }
}

@Composable
private fun SplashFeatureChip(
    label: String,
    icon: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .background(LovePrimaryContainer)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        icon()
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = LoveOnPrimaryContainer,
        )
    }
}
