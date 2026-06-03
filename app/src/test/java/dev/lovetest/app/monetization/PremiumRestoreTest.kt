package dev.lovetest.app.monetization

import dev.lovetest.app.prefs.AppPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumRestoreTest {

    @Test
    fun restorePremiumAccess_whenAlreadyPremium_returnsTrue() = runTest {
        val prefs = mockk<AppPreferences>()
        val billing = mockk<PremiumBillingManager>()
        coEvery { prefs.isPremium() } returns true

        assertTrue(restorePremiumAccess(prefs, billing))
    }

    @Test
    fun restorePremiumAccess_whenBillingNotConfigured_returnsFalse() = runTest {
        val prefs = mockk<AppPreferences>()
        val billing = mockk<PremiumBillingManager>()
        coEvery { prefs.isPremium() } returns false
        coEvery { billing.isConfigured() } returns false

        assertFalse(restorePremiumAccess(prefs, billing))
    }

    @Test
    fun restorePremiumAccess_delegatesToBillingWhenConfigured() = runTest {
        val prefs = mockk<AppPreferences>()
        val billing = mockk<PremiumBillingManager>()
        coEvery { prefs.isPremium() } returns false
        coEvery { billing.isConfigured() } returns true
        coEvery { billing.restorePurchases() } returns true

        assertTrue(restorePremiumAccess(prefs, billing))
    }

    @Test
    fun syncPremiumOnStartup_setsPremiumWhenOwned() = runTest {
        val prefs = mockk<AppPreferences>(relaxed = true)
        val billing = mockk<PremiumBillingManager>()
        coEvery { billing.queryPremiumOwnership() } returns true

        syncPremiumOnStartup(prefs, billing)

        coVerify { prefs.setPremium(true) }
    }

    @Test
    fun syncPremiumOnStartup_clearsPremiumWhenNotOwned() = runTest {
        val prefs = mockk<AppPreferences>(relaxed = true)
        val billing = mockk<PremiumBillingManager>()
        coEvery { billing.queryPremiumOwnership() } returns false

        syncPremiumOnStartup(prefs, billing)

        coVerify { prefs.setPremium(false) }
    }

    @Test
    fun syncPremiumOnStartup_keepsFlagWhenBillingUnavailable() = runTest {
        val prefs = mockk<AppPreferences>(relaxed = true)
        val billing = mockk<PremiumBillingManager>()
        coEvery { billing.queryPremiumOwnership() } returns null

        syncPremiumOnStartup(prefs, billing)

        coVerify(exactly = 0) { prefs.setPremium(any()) }
    }
}
