package dev.lovetest.app.util

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier

/** Keeps input CTAs visible above the keyboard and clear of the nav bar. */
fun Modifier.loveInputContentPadding(): Modifier = imePadding().navigationBarsPadding()
