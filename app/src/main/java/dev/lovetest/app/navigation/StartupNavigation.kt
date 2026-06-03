package dev.lovetest.app.navigation

import dev.lovetest.app.BuildConfig
import dev.lovetest.app.prefs.AppPreferences

suspend fun routeAfterSplash(preferences: AppPreferences): String = when {
    !preferences.isOnboardingCompleted() -> Routes.Onboarding
    requiresConsentScreen(preferences) -> Routes.Consent
    else -> Routes.Hub
}

suspend fun routeAfterOnboarding(preferences: AppPreferences): String = when {
    requiresConsentScreen(preferences) -> Routes.Consent
    else -> Routes.Hub
}

internal fun requiresConsentForAds(adsEnabled: Boolean, consentCompleted: Boolean): Boolean =
    adsEnabled && !consentCompleted

private suspend fun requiresConsentScreen(preferences: AppPreferences): Boolean =
    requiresConsentForAds(BuildConfig.ADS_ENABLED, preferences.isConsentCompleted())
