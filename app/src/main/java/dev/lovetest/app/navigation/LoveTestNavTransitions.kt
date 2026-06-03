package dev.lovetest.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

private const val NAV_ANIM_MS = 280

fun loveTestEnterTransition(): EnterTransition =
    fadeIn(animationSpec = tween(NAV_ANIM_MS)) +
        slideInHorizontally(
            animationSpec = tween(NAV_ANIM_MS, easing = FastOutSlowInEasing),
            initialOffsetX = { fullWidth -> fullWidth / 5 },
        )

fun loveTestExitTransition(): ExitTransition =
    fadeOut(animationSpec = tween(NAV_ANIM_MS)) +
        slideOutHorizontally(
            animationSpec = tween(NAV_ANIM_MS, easing = FastOutSlowInEasing),
            targetOffsetX = { fullWidth -> -fullWidth / 5 },
        )

fun loveTestPopEnterTransition(): EnterTransition =
    fadeIn(animationSpec = tween(NAV_ANIM_MS)) +
        slideInHorizontally(
            animationSpec = tween(NAV_ANIM_MS, easing = FastOutSlowInEasing),
            initialOffsetX = { fullWidth -> -fullWidth / 5 },
        )

fun loveTestPopExitTransition(): ExitTransition =
    fadeOut(animationSpec = tween(NAV_ANIM_MS)) +
        slideOutHorizontally(
            animationSpec = tween(NAV_ANIM_MS, easing = FastOutSlowInEasing),
            targetOffsetX = { fullWidth -> fullWidth / 5 },
        )
