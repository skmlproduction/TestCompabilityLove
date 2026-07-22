package dev.lovetest.app.ui.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.lovetest.app.debug.DebugUiPreview

data class LoveShareSheetController(
    val visible: Boolean,
    val open: () -> Unit,
    val close: () -> Unit,
)

@Composable
fun rememberLoveShareSheet(debugAutoOpenId: String? = null): LoveShareSheetController {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(debugAutoOpenId) {
        if (debugAutoOpenId != null && DebugUiPreview.matches(debugAutoOpenId)) {
            visible = true
        }
    }
    return LoveShareSheetController(
        visible = visible,
        open = { visible = true },
        close = { visible = false },
    )
}

private val shareSheetEnter =
    fadeIn(animationSpec = tween(280)) +
        slideInVertically(
            animationSpec = tween(350, easing = FastOutSlowInEasing),
            initialOffsetY = { fullHeight -> fullHeight / 3 },
        )

private val shareSheetExit =
    fadeOut(animationSpec = tween(200)) +
        slideOutVertically(
            animationSpec = tween(250, easing = FastOutSlowInEasing),
            targetOffsetY = { fullHeight -> fullHeight / 3 },
        )

@Composable
fun LoveShareResultOverlay(
    sheet: LoveShareSheetController,
    percent: Int,
    name1: String,
    name2: String,
    harmonyTag: String,
    shareText: String,
    high: Boolean,
    onShareFallback: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = sheet.visible,
        enter = shareSheetEnter,
        exit = shareSheetExit,
    ) {
        ShareResultPreview(
            percent = percent,
            name1 = name1,
            name2 = name2,
            harmonyTag = harmonyTag,
            shareText = shareText,
            high = high,
            onDismiss = sheet.close,
            onShareFallback = onShareFallback,
        )
    }
}

@Composable
fun LoveWheelShareOverlay(
    sheet: LoveShareSheetController,
    prize: String,
    shareText: String,
    onShare: () -> Unit,
) {
    AnimatedVisibility(
        visible = sheet.visible,
        enter = shareSheetEnter,
        exit = shareSheetExit,
    ) {
        WheelSharePreview(
            prize = prize,
            shareText = shareText,
            onDismiss = sheet.close,
            onShareFallback = onShare,
        )
    }
}
