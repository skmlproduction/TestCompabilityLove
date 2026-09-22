package dev.lovetest.app.monetization

import dev.lovetest.app.BuildConfig
import dev.lovetest.app.prefs.AppPreferences

/**
 * Preloads CAS interstitial only when ads are allowed.
 * CAS SDK инициализируется в `LoveTestApplication.onCreate` ([CasAdsManager]).
 */
suspend fun bootstrapAdsIfAllowed(
    preferences: AppPreferences,
    consentManager: AdsConsentManager,
    interstitialManager: CasInterstitialManager,
) {
    if (!BuildConfig.ADS_ENABLED) return
    if (preferences.isPremium()) return
    if (!preferences.isConsentCompleted()) return
    if (!consentManager.canRequestAds()) return
    interstitialManager.preload()
}
