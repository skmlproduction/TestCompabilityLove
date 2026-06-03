package dev.lovetest.app.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Документирует ожидаемые route id (полная логика с BuildConfig — в androidTest).
 */
class StartupNavigationTest {

    @Test
    fun routeConstants_areStable() {
        assertEquals("splash", Routes.Splash)
        assertEquals("onboarding", Routes.Onboarding)
        assertEquals("consent", Routes.Consent)
        assertEquals("hub", Routes.Hub)
    }
}
