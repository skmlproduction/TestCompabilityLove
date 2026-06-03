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
    fun routeAfterSplash_onboardingDone_goesToHubWhenAdsOff() = runTest {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isOnboardingCompleted() } returns true
        coEvery { prefs.isConsentCompleted() } returns false

        assertEquals(Routes.Hub, routeAfterSplash(prefs))
    }
}
