@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package dev.lovetest.app.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics

/** Иконка рядом с видимым текстом в том же контроле — не дублировать в TalkBack. */
fun Modifier.decorativeForAccessibility(): Modifier = semantics { invisibleToUser() }

/** Видимая подпись поля — озвучивается через [loveInputFieldSemantics] на самом поле. */
fun Modifier.loveInputLabelForAccessibility(): Modifier = semantics { invisibleToUser() }

/** Связка label ↔ editable field для TalkBack (WCAG 1.3.1 / 4.1.2). */
fun Modifier.loveInputFieldSemantics(
    label: String,
    value: String,
    placeholder: String = "",
): Modifier = semantics {
    contentDescription = when {
        value.isNotBlank() -> "$label, $value"
        placeholder.isNotBlank() -> "$label, $placeholder"
        else -> label
    }
}
