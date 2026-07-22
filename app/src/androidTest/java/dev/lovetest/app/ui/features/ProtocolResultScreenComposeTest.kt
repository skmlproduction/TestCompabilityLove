package dev.lovetest.app.ui.features

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.After
import org.junit.Assert.assertTrue
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProtocolResultScreenComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @After
    fun tearDown() {
        LoveTestSession.clear()
    }

    @Test
    fun protocolResult_lowScore_showsWarningAndTip() {
        seedProtocolResultLow()
        val warning = composeRule.activity.getString(R.string.protocol_low_warning_title)
        val tip = composeRule.activity.getString(R.string.protocol_low_tip_title)

        composeRule.setContent {
            LoveTestTheme {
                ProtocolResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(warning).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(tip).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun protocolResult_showsSignalsAndActions() {
        seedProtocolResult()
        val summary = composeRule.activity.getString(R.string.protocol_summary_title)
        val tryAgain = composeRule.activity.getString(R.string.protocol_try_again)
        val share = composeRule.activity.getString(R.string.love_test_share_cta)

        composeRule.setContent {
            LoveTestTheme {
                ProtocolResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(summary).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(tryAgain).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(share).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun protocolResult_missingSignals_recoversAndShowsSummary() {
        LoveTestSession.storeLoveResult("Anna", "Max", 61)
        val summary = composeRule.activity.getString(R.string.protocol_summary_title)
        val fallback = composeRule.activity.getString(R.string.protocol_signals_fallback_note)

        composeRule.setContent {
            LoveTestTheme {
                ProtocolResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(summary).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(fallback).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun protocolResult_tryAgain_invokesCallback() {
        seedProtocolResult()
        val tryAgain = composeRule.activity.getString(R.string.protocol_try_again)
        var retried = false

        composeRule.setContent {
            LoveTestTheme {
                ProtocolResultScreen(
                    onShare = {},
                    onTryAnother = { retried = true },
                    onHome = {},
                )
            }
        }

        composeRule.onNodeWithText(tryAgain).performScrollTo().performClick()
        assertTrue(retried)
    }

    @Test
    fun protocolResult_lowVerdict_showsLowTitle() {
        seedProtocolResultLow()
        val lowTitle = composeRule.activity.getString(R.string.protocol_verdict_title_0)

        composeRule.setContent {
            LoveTestTheme {
                ProtocolResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(lowTitle).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("28%").performScrollTo().assertIsDisplayed()
    }

    private fun seedProtocolResult() {
        val calc = DefaultLoveScoreCalculator()
        LoveTestSession.storeProtocolResult(
            "Anna",
            "Max",
            82,
            calc.protocolSignals("Anna", "Max"),
        )
    }

    private fun seedProtocolResultLow() {
        val calc = DefaultLoveScoreCalculator()
        LoveTestSession.storeProtocolResult(
            "Anna",
            "Max",
            28,
            calc.protocolSignals("Anna", "Max"),
        )
    }
}
