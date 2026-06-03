package dev.lovetest.app.ui.features

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProtocolResultScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @After
    fun tearDown() {
        LoveTestSession.clear()
    }

    @Test
    fun protocolResult_showsSignalsAndActions() {
        seedProtocolResult()
        val signals = composeRule.activity.getString(R.string.protocol_signals_title)
        val tryAgain = composeRule.activity.getString(R.string.protocol_try_again)
        val share = composeRule.activity.getString(R.string.love_test_share_cta)

        composeRule.setContent {
            LoveTestTheme {
                ProtocolResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(signals).assertIsDisplayed()
        composeRule.onNodeWithText(tryAgain).assertIsDisplayed()
        composeRule.onNodeWithText(share).assertIsDisplayed()
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

        composeRule.onNodeWithText(tryAgain).performClick()
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

        composeRule.onNodeWithText(lowTitle).assertIsDisplayed()
        composeRule.onNodeWithText("28%").assertIsDisplayed()
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
