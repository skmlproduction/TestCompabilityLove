package dev.lovetest.app.navigation

import dev.lovetest.app.prefs.AppPreferences
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RouteAfterSplashTest {

    @Test
    fun routeAfterSplash_onboardingNotDone_goesToOnboarding() = runTest {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isOnboardingCompleted() } returns false

        assertEquals(Routes.Onboarding, routeAfterSplash(prefs))
    }

    @Test
    fun routeAfterSplash_onboardingDoneWithoutConsent_goesToConsentWhenAdsOn() = runTest {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isOnboardingCompleted() } returns true
        coEvery { prefs.isConsentCompleted() } returns false

        // ADS_ENABLED=true (CAS): после онбординга требуется экран согласия.
        assertEquals(Routes.Consent, routeAfterSplash(prefs))
    }

    @Test
    fun routeAfterSplash_onboardingAndConsentDone_goesToHub() = runTest {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isOnboardingCompleted() } returns true
        coEvery { prefs.isConsentCompleted() } returns true

        assertEquals(Routes.Hub, routeAfterSplash(prefs))
    }
}
