package dev.lovetest.app.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.core.domain.NameInputValidator
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LoveTypographyTokens

/** Inline help when a field is non-blank but has no Unicode letters. */
@Composable
fun NameInputHint(
    value: String,
    modifier: Modifier = Modifier,
) {
    if (value.isBlank() || NameInputValidator.isValidName(value)) return
    Text(
        text = stringResource(R.string.input_name_letters_hint),
        style = LoveTypographyTokens.CardCaption,
        color = LovePrimary,
        modifier = modifier.padding(top = 6.dp),
    )
}
