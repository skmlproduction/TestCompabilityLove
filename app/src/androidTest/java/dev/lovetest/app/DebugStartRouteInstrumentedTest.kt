package dev.lovetest.app

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.navigation.NavIntents
import dev.lovetest.app.navigation.Routes
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DebugStartRouteInstrumentedTest {

    @Test
    fun hubRoute_launchesWithoutCrash() {
        launchRoute(Routes.Hub)
    }

    @Test
    fun consentRoute_launchesWithoutCrash() {
        launchRoute(Routes.Consent)
    }

    @Test
    fun loveTestInputRoute_launchesWithoutCrash() {
        launchRoute(Routes.LoveTestInput)
    }

    @Test
    fun premiumPaywallRoute_launchesWithoutCrash() {
        launchRoute(Routes.PremiumPaywall)
    }

    @Test
    fun settingsRoute_launchesWithoutCrash() {
        launchRoute(Routes.Settings)
    }

    @Test
    fun onboardingRoute_launchesWithoutCrash() {
        launchRoute(Routes.Onboarding)
    }

    @Test
    fun protocolInputRoute_launchesWithoutCrash() {
        launchRoute(Routes.ProtocolInput)
    }

    @Test
    fun hubLoadingPreview_launchesWithoutCrash() {
        launchRouteWithPreview(Routes.Hub, "hub_loading")
    }

    @Test
    fun errorNetworkPreview_launchesWithoutCrash() {
        launchRouteWithPreview(Routes.Hub, "error_network")
    }

    private fun launchRoute(route: String) {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
            putExtra(NavIntents.EXTRA_DEBUG_START_ROUTE, route)
        }
        ActivityScenario.launch<MainActivity>(intent).use { }
    }

    private fun launchRouteWithPreview(route: String, previewId: String) {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
            putExtra(NavIntents.EXTRA_DEBUG_START_ROUTE, route)
            putExtra(NavIntents.EXTRA_DEBUG_UI_PREVIEW, previewId)
        }
        ActivityScenario.launch<MainActivity>(intent).use { }
    }
}
