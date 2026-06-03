package dev.lovetest.app.ui.features

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.components.LovePrimaryButton
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveSurface
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelSpinScreen(
    onBack: () -> Unit,
    onSpinComplete: (segmentIndex: Int, prize: String) -> Unit,
) {
    val segments = stringArrayResource(R.array.wheel_segments).toList()
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    val spinningCd = stringResource(R.string.wheel_spinning_cd)

    LaunchedEffect(Unit) {
        if (DebugUiPreview.matches("wheel_spin")) {
            rotation.snapTo(wheelRotationForSegment(segmentIndex = 3, extraSpins = 4))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFFFD54F).copy(0.5f),
                radius = 12.dp.toPx(),
                center = Offset(size.width * 0.15f, size.height * 0.38f),
            )
            drawCircle(
                color = Color(0xFFFFD54F).copy(0.4f),
                radius = 10.dp.toPx(),
                center = Offset(size.width * 0.88f, size.height * 0.55f),
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.wheel_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    },
                    navigationIcon = {
                        TextButton(onClick = onBack, enabled = !isSpinning) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LovePrimary)
                            Text(
                                stringResource(R.string.nav_back),
                                color = LovePrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = LoveSurface.copy(alpha = 0.85f),
                    ),
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.wheel_hero_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFFCE4EC))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.wheel_test_badge),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF880E4F),
                    )
                }
                WheelPointer(modifier = Modifier.padding(top = 16.dp))
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .semantics {
                            if (isSpinning) contentDescription = spinningCd
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    WheelDisc(
                        segments = segments,
                        rotationDegrees = rotation.value,
                        discSize = 300.dp,
                    )
                }
                LoveShadowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    shape = RoundedCornerShape(32.dp),
                    shadowElevation = LoveCardShadowElevation.Card,
                    colors = CardDefaults.cardColors(containerColor = LoveSurface),
                ) {
                    Text(
                        text = stringResource(
                            if (isSpinning) R.string.wheel_spinning_hint else R.string.wheel_ready_hint,
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF880E4F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 20.dp),
                    )
                }
                LovePrimaryButton(
                    text = stringResource(R.string.wheel_spin_cta),
                    onClick = {
                        if (isSpinning) return@LovePrimaryButton
                        val targetIndex = Random.nextInt(WHEEL_SEGMENT_COUNT)
                        val prize = segments[targetIndex]
                        isSpinning = true
                        scope.launch {
                            val target = wheelRotationForSegment(targetIndex) +
                                ((rotation.value / 360f).toInt() + 1) * 360f
                            rotation.animateTo(
                                targetValue = target,
                                animationSpec = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
                            )
                            isSpinning = false
                            onSpinComplete(targetIndex, prize)
                        }
                    },
                    enabled = !isSpinning,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .height(52.dp),
                )
                Text(
                    text = stringResource(R.string.wheel_spin_note1),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                )
                Text(
                    text = stringResource(R.string.wheel_spin_note2),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.wheel_spin_note3),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoveOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 32.dp),
                )
            }
        }
    }
}
