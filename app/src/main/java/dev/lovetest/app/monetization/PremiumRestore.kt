package dev.lovetest.app.monetization

import dev.lovetest.app.prefs.AppPreferences

suspend fun restorePremiumAccess(
    preferences: AppPreferences,
    billingManager: PremiumBillingManager,
): Boolean {
    if (preferences.isPremium()) return true
    if (!billingManager.isConfigured()) return false
    return billingManager.restorePurchases()
}

/** Sync premium flag with Play on cold start; keeps local flag if billing is unavailable. */
suspend fun syncPremiumOnStartup(
    preferences: AppPreferences,
    billingManager: PremiumBillingManager,
) {
    when (billingManager.queryPremiumOwnership()) {
        true -> preferences.setPremium(true)
        false -> preferences.setPremium(false)
        null -> Unit
    }
}
