package dev.lovetest.app.testing

import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.monetization.AdsInterstitialController
import dev.lovetest.app.session.LoveTestSession
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Resets process-wide session/preview state between instrumented Compose tests.
 *
 * Declare this rule *before* [androidx.compose.ui.test.junit4.createAndroidComposeRule]
 * so [finished] runs after Compose dispose (JUnit applies finished in reverse field order).
 *
 * Does **not** call stopKoin — that races Application coroutines and MainActivity under LMK.
 * Tests that host [org.koin.compose.KoinApplication] call stopKoin in `@Before`.
 * MainActivity tests use [EnsureAppKoinRule].
 */
class LoveInstrumentedCleanup : TestWatcher() {
    override fun starting(description: Description) {
        resetSession()
    }

    override fun finished(description: Description) {
        resetSession()
    }

    private fun resetSession() {
        LoveTestSession.clear()
        DebugUiPreview.clear()
        AdsInterstitialController.consume()
    }
}
