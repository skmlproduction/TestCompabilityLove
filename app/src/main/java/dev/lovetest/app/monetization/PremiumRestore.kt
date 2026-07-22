package dev.lovetest.app.monetization

import dev.lovetest.app.prefs.AppPreferences

enum class PremiumRestoreOutcome {
    Restored,
    NotFound,
    Unavailable,
}

suspend fun restorePremiumAccess(
    preferences: AppPreferences,
    billingManager: PremiumBillingManager,
): PremiumRestoreOutcome {
    if (preferences.isPremium()) return PremiumRestoreOutcome.Restored
    if (!billingManager.isConfigured()) return PremiumRestoreOutcome.Unavailable
    return when (billingManager.queryPremiumOwnership()) {
        true -> PremiumRestoreOutcome.Restored
        false -> PremiumRestoreOutcome.NotFound
        null -> PremiumRestoreOutcome.Unavailable
    }
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
