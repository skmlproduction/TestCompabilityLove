package dev.lovetest.app

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.navigation.NavIntents
import dev.lovetest.app.navigation.Routes
import dev.lovetest.app.testing.EnsureAppKoinRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DebugStartRouteInstrumentedTest {

    @get:Rule
    val ensureAppKoin = EnsureAppKoinRule()

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
    fun calculatorInputRoute_launchesWithoutCrash() {
        launchRoute(Routes.CalculatorInput)
    }

    @Test
    fun pairInputRoute_launchesWithoutCrash() {
        launchRoute(Routes.PairInput)
    }

    @Test
    fun victoryInputRoute_launchesWithoutCrash() {
        launchRoute(Routes.VictoryInput)
    }

    @Test
    fun lettersInputRoute_launchesWithoutCrash() {
        launchRoute(Routes.LettersInput)
    }

    @Test
    fun zodiacPickRoute_launchesWithoutCrash() {
        launchRoute(Routes.ZodiacPick)
    }

    @Test
    fun wheelSpinRoute_launchesWithoutCrash() {
        launchRoute(Routes.WheelSpin)
    }

    @Test
    fun premiumPaywallRoute_launchesWithoutCrash() {
        launchRoute(Routes.PremiumPaywall)
    }

    @Test
    fun premiumThankYouRoute_launchesWithoutCrash() {
        launchRoute(Routes.PremiumThankYou)
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

    @Test
    fun adInterstitialPreview_launchesWithoutCrash() {
        launchRouteWithPreview(Routes.Hub, "ad_interstitial_placeholder")
    }

    @Test
    fun loveTestResultLowPreview_launchesWithoutCrash() {
        launchRouteWithPreview(Routes.LoveTestResult, "love_test_result_low")
    }

    @Test
    fun shareResultPreview_launchesWithoutCrash() {
        launchRouteWithPreview(Routes.LoveTestResult, "share_result_card")
    }

    @Test
    fun protocolResultLowPreview_launchesWithoutCrash() {
        launchRouteWithPreview(Routes.ProtocolResult, "protocol_result_low")
    }

    private fun launchRoute(route: String) {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
            putExtra(NavIntents.EXTRA_DEBUG_START_ROUTE, route)
        }
        ActivityScenario.launch<MainActivity>(intent).use { scenario ->
            scenario.onActivity { /* settle one frame */ }
        }
        // Brief pause reduces guest LMK when launching many routes back-to-back.
        Thread.sleep(250)
    }

    private fun launchRouteWithPreview(route: String, previewId: String) {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
            putExtra(NavIntents.EXTRA_DEBUG_START_ROUTE, route)
            putExtra(NavIntents.EXTRA_DEBUG_UI_PREVIEW, previewId)
        }
        ActivityScenario.launch<MainActivity>(intent).use { scenario ->
            scenario.onActivity { /* settle one frame */ }
        }
        Thread.sleep(250)
    }
}
