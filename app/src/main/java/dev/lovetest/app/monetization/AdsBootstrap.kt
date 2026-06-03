package dev.lovetest.app.monetization

import android.content.Context
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.prefs.AppPreferences

/**
 * Initializes Mobile Ads and preloads interstitial only when ads are allowed.
 */
suspend fun bootstrapAdsIfAllowed(
    context: Context,
    preferences: AppPreferences,
    consentManager: AdsConsentManager,
    adMobManager: AdMobInterstitialManager,
) {
    if (!BuildConfig.ADS_ENABLED) return
    if (preferences.isPremium()) return
    if (!preferences.isConsentCompleted()) return
    if (!consentManager.canRequestAds()) return
    AdMobInitializer.initializeIfNeeded(context)
    adMobManager.preload()
}
