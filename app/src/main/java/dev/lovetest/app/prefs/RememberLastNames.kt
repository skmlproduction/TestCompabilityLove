package dev.lovetest.app.prefs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import org.koin.compose.koinInject

data class PairedNameFields(
    val name1: MutableState<String>,
    val name2: MutableState<String>,
)

@Composable
fun rememberPairedNameFields(
    preferences: AppPreferences = koinInject(),
): PairedNameFields {
    val name1 = rememberSaveable { mutableStateOf("") }
    val name2 = rememberSaveable { mutableStateOf("") }
    val loaded = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!loaded.value) {
            val (saved1, saved2) = preferences.getLastNames()
            if (name1.value.isBlank() && saved1.isNotBlank()) name1.value = saved1
            if (name2.value.isBlank() && saved2.isNotBlank()) name2.value = saved2
            loaded.value = true
        }
    }
    return PairedNameFields(name1, name2)
}

@Composable
fun rememberLastSingleName(
    preferences: AppPreferences = koinInject(),
): MutableState<String> {
    val name = rememberSaveable { mutableStateOf("") }
    val loaded = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!loaded.value) {
            val (saved1, _) = preferences.getLastNames()
            if (name.value.isBlank() && saved1.isNotBlank()) name.value = saved1
            loaded.value = true
        }
    }
    return name
}
