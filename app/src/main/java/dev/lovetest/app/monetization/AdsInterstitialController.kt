package dev.lovetest.app.monetization

import dev.lovetest.app.BuildConfig
import dev.lovetest.app.prefs.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Показ межстраничной рекламы между сессиями (после теста → hub).
 * При [BuildConfig.ADS_ENABLED] Hub показывает [AdMobInterstitialManager].
 * Debug-only UI placeholder (screen29) — только через [DebugUiPreview], не production fallback.
 */
object AdsInterstitialController {

    private val _pendingOnHub = MutableStateFlow(false)
    val pendingOnHub: StateFlow<Boolean> = _pendingOnHub.asStateFlow()

    suspend fun shouldShow(prefs: AppPreferences, consentManager: AdsConsentManager): Boolean {
        if (!BuildConfig.ADS_ENABLED) return false
        if (prefs.isPremium()) return false
        if (!prefs.isConsentCompleted()) return false
        if (!consentManager.canRequestAds()) return false
        return true
    }

    fun requestShowOnHub() {
        _pendingOnHub.value = true
    }

    fun consume() {
        _pendingOnHub.value = false
    }
}
