package dev.lovetest.app.ui.hub

import androidx.lifecycle.ViewModel
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.debug.DebugUiPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class HubDisplayState {
    /** Только для DebugUiPreview / скриншотов. */
    Loading,
    Main,
    /** Только для DebugUiPreview / скриншотов. */
    ErrorNetwork,
}

class HubViewModel : ViewModel() {

    private val _state = MutableStateFlow(resolveInitialState())
    val state: StateFlow<HubDisplayState> = _state.asStateFlow()

    fun retryFromError() {
        _state.value = HubDisplayState.Main
    }

    private fun resolveInitialState(): HubDisplayState = when {
        BuildConfig.DEBUG && DebugUiPreview.matches("hub_loading") -> HubDisplayState.Loading
        BuildConfig.DEBUG && DebugUiPreview.matches("error_network") -> HubDisplayState.ErrorNetwork
        else -> HubDisplayState.Main
    }
}
