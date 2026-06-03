package dev.lovetest.app.ui.love

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoveTestResultScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @After
    fun tearDown() {
        LoveTestSession.clear()
    }

    @Test
    fun loveTestResult_highScore_showsTagAndActions() {
        LoveTestSession.storeLoveResult("Anna", "Max", 87)
        val highTag = composeRule.activity.getString(R.string.love_test_result_high_tag)
        val share = composeRule.activity.getString(R.string.love_test_share_cta)
        val tryAgain = composeRule.activity.getString(R.string.love_test_try_again)

        composeRule.setContent {
            LoveTestTheme {
                LoveTestResultScreen(onShare = {}, onTryAgain = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(highTag).assertIsDisplayed()
        composeRule.onNodeWithText(share).assertIsDisplayed()
        composeRule.onNodeWithText(tryAgain).assertIsDisplayed()
    }

    @Test
    fun loveTestResult_lowScore_showsLowMessageAndTip() {
        LoveTestSession.storeLoveResult("Anna", "Max", 23)
        val lowTag = composeRule.activity.getString(R.string.love_test_result_low_tag)
        val tipTitle = composeRule.activity.getString(R.string.love_test_result_low_tip_title)

        composeRule.setContent {
            LoveTestTheme {
                LoveTestResultScreen(onShare = {}, onTryAgain = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(lowTag).assertIsDisplayed()
        composeRule.onNodeWithText(tipTitle).assertIsDisplayed()
    }

    @Test
    fun loveTestResult_tryAgain_invokesCallback() {
        LoveTestSession.storeLoveResult("Anna", "Max", 87)
        val tryAgain = composeRule.activity.getString(R.string.love_test_try_again)
        var retried = false

        composeRule.setContent {
            LoveTestTheme {
                LoveTestResultScreen(
                    onShare = {},
                    onTryAgain = { retried = true },
                    onHome = {},
                )
            }
        }

        composeRule.onNodeWithText(tryAgain).performClick()
        assertTrue(retried)
    }
}
