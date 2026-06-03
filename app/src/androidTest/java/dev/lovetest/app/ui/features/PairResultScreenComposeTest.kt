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
class PairResultScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @After
    fun tearDown() {
        LoveTestSession.clear()
    }

    @Test
    fun pairResult_showsMetricsAndActions() {
        seedPairResult()
        val metrics = composeRule.activity.getString(R.string.pair_metrics_title)
        val tryAnother = composeRule.activity.getString(R.string.pair_try_another)
        val share = composeRule.activity.getString(R.string.love_test_share_cta)

        composeRule.setContent {
            LoveTestTheme {
                PairResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(metrics).assertIsDisplayed()
        composeRule.onNodeWithText(tryAnother).assertIsDisplayed()
        composeRule.onNodeWithText(share).assertIsDisplayed()
    }

    @Test
    fun pairResult_tryAnother_invokesCallback() {
        seedPairResult()
        val tryAnother = composeRule.activity.getString(R.string.pair_try_another)
        var retried = false

        composeRule.setContent {
            LoveTestTheme {
                PairResultScreen(
                    onShare = {},
                    onTryAnother = { retried = true },
                    onHome = {},
                )
            }
        }

        composeRule.onNodeWithText(tryAnother).performClick()
        assertTrue(retried)
    }

    private fun seedPairResult() {
        val calc = DefaultLoveScoreCalculator()
        LoveTestSession.storePairResult(
            "Sophia",
            "Dmitry",
            74,
            calc.pairMetrics("Sophia", "Dmitry"),
        )
    }
}
