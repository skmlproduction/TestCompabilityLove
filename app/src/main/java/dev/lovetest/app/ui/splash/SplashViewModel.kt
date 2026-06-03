package dev.lovetest.app.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.navigation.routeAfterSplash
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.session.LoveTestSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SplashDestination {
    data object Loading : SplashDestination
    data class Navigate(val route: String) : SplashDestination
}

class SplashViewModel(
    private val preferences: AppPreferences,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    private val _loadProgress = MutableStateFlow(0f)
    val loadProgress: StateFlow<Float> = _loadProgress.asStateFlow()

    init {
        viewModelScope.launch {
            if (BuildConfig.DEBUG && DebugUiPreview.matches("splash_brand")) {
                _loadProgress.value = 0.45f
                return@launch
            }
            val steps = 12
            repeat(steps) { step ->
                delay(120L)
                _loadProgress.value = (step + 1) / steps.toFloat() * 0.6f
            }
            delay(400L)
            LoveTestSession.restoreFromEncoded(preferences.loadSessionSnapshot())
            _destination.value = SplashDestination.Navigate(routeAfterSplash(preferences))
        }
    }
}
