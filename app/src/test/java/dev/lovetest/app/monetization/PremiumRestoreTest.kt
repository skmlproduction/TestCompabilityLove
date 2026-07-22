package dev.lovetest.app.monetization

import dev.lovetest.app.prefs.AppPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PremiumRestoreTest {

    @Test
    fun restorePremiumAccess_whenAlreadyPremium_returnsRestored() = runTest {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns true
        val billing = mockk<PremiumBillingManager>(relaxed = true)

        assertEquals(
            PremiumRestoreOutcome.Restored,
            restorePremiumAccess(prefs, billing),
        )
    }

    @Test
    fun restorePremiumAccess_whenBillingNotConfigured_returnsUnavailable() = runTest {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns false
        val billing = mockk<PremiumBillingManager>()
        every { billing.isConfigured() } returns false

        assertEquals(
            PremiumRestoreOutcome.Unavailable,
            restorePremiumAccess(prefs, billing),
        )
    }

    @Test
    fun restorePremiumAccess_whenOwned_returnsRestored() = runTest {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns false
        val billing = mockk<PremiumBillingManager>()
        every { billing.isConfigured() } returns true
        coEvery { billing.queryPremiumOwnership() } returns true

        assertEquals(
            PremiumRestoreOutcome.Restored,
            restorePremiumAccess(prefs, billing),
        )
    }

    @Test
    fun restorePremiumAccess_whenNotOwned_returnsNotFound() = runTest {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns false
        val billing = mockk<PremiumBillingManager>()
        every { billing.isConfigured() } returns true
        coEvery { billing.queryPremiumOwnership() } returns false

        assertEquals(
            PremiumRestoreOutcome.NotFound,
            restorePremiumAccess(prefs, billing),
        )
    }

    @Test
    fun restorePremiumAccess_whenQueryFails_returnsUnavailable() = runTest {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns false
        val billing = mockk<PremiumBillingManager>()
        every { billing.isConfigured() } returns true
        coEvery { billing.queryPremiumOwnership() } returns null

        assertEquals(
            PremiumRestoreOutcome.Unavailable,
            restorePremiumAccess(prefs, billing),
        )
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
