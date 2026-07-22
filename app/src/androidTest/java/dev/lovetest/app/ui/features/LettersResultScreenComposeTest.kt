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
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.After
import org.junit.Assert.assertTrue
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LettersResultScreenComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @After
    fun tearDown() {
        LoveTestSession.clear()
    }

    @Test
    fun lettersResult_lowScore_showsWarningCard() {
        LoveTestSession.storeLoveResult("ЛЮБОВЬ", "СЧАСТЬ", 23)
        val warning = composeRule.activity.getString(R.string.love_test_result_low_message)

        composeRule.setContent {
            LoveTestTheme {
                LettersResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(warning).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun lettersResult_showsMessageAndActions() {
        LoveTestSession.storeLoveResult("ЛЮБОВЬ", "СЧАСТЬ", 65)
        val message = composeRule.activity.getString(R.string.letters_message_title)
        val tryAnother = composeRule.activity.getString(R.string.letters_try_another)
        val share = composeRule.activity.getString(R.string.love_test_share_cta)

        composeRule.setContent {
            LoveTestTheme {
                LettersResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(message).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(tryAnother).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(share).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun lettersResult_tryAnother_invokesCallback() {
        LoveTestSession.storeLoveResult("ЛЮБОВЬ", "СЧАСТЬ", 65)
        val tryAnother = composeRule.activity.getString(R.string.letters_try_another)
        var retried = false

        composeRule.setContent {
            LoveTestTheme {
                LettersResultScreen(
                    onShare = {},
                    onTryAnother = { retried = true },
                    onHome = {},
                )
            }
        }

        composeRule.onNodeWithText(tryAnother).performScrollTo().performClick()
        assertTrue(retried)
    }
}
