package dev.lovetest.app.ui.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.lovetest.app.session.LoveTestSession

@Composable
fun RequireLoveResult(
    onMissing: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (LoveTestSession.hasLoveResult()) {
        content()
    } else {
        RedirectWhenSessionLost(onMissing)
    }
}

@Composable
fun RequirePairResult(
    onMissing: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (LoveTestSession.hasPairResult()) {
        content()
    } else {
        RedirectWhenSessionLost(onMissing)
    }
}

@Composable
fun RequireProtocolResult(
    onMissing: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (LoveTestSession.hasProtocolResult()) {
        content()
    } else {
        RedirectWhenSessionLost(onMissing)
    }
}

@Composable
fun RequireWheelResult(
    onMissing: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (LoveTestSession.hasWheelResult()) {
        content()
    } else {
        RedirectWhenSessionLost(onMissing)
    }
}

@Composable
private fun RedirectWhenSessionLost(onRedirect: () -> Unit) {
    LaunchedEffect(Unit) {
        onRedirect()
    }
}
