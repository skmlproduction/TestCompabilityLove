package dev.lovetest.app.ui.features

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
class ZodiacResultScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @After
    fun tearDown() {
        LoveTestSession.clear()
    }

    @Test
    fun zodiacResult_showsHarmonyTagAndActions() {
        LoveTestSession.storeLoveResult("Лев", "Рыбы", 78)
        val harmony = composeRule.activity.getString(R.string.zodiac_harmony_tag)
        val tryAnother = composeRule.activity.getString(R.string.zodiac_try_another)
        val share = composeRule.activity.getString(R.string.love_test_share_cta)

        composeRule.setContent {
            LoveTestTheme {
                ZodiacResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(harmony).assertIsDisplayed()
        composeRule.onNodeWithText(tryAnother).assertIsDisplayed()
        composeRule.onNodeWithText(share).assertIsDisplayed()
    }

    @Test
    fun zodiacResult_tryAnother_invokesCallback() {
        LoveTestSession.storeLoveResult("Лев", "Рыбы", 78)
        val tryAnother = composeRule.activity.getString(R.string.zodiac_try_another)
        var retried = false

        composeRule.setContent {
            LoveTestTheme {
                ZodiacResultScreen(
                    onShare = {},
                    onTryAnother = { retried = true },
                    onHome = {},
                )
            }
        }

        composeRule.onNodeWithText(tryAnother).performClick()
        assertTrue(retried)
    }
}
