@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package dev.lovetest.app.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics

/** Иконка рядом с видимым текстом в том же контроле — не дублировать в TalkBack. */
fun Modifier.decorativeForAccessibility(): Modifier = semantics { invisibleToUser() }
