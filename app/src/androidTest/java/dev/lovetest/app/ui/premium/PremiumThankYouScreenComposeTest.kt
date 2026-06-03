package dev.lovetest.app.ui.premium

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PremiumThankYouScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun thankYou_showsHeadlineAndHomeCta() {
        val headline = composeRule.activity.getString(R.string.premium_thank_you_headline)
        val home = composeRule.activity.getString(R.string.premium_thank_you_home)

        composeRule.setContent {
            LoveTestTheme {
                PremiumThankYouScreen(onHome = {}, onLoveTest = {})
            }
        }

        composeRule.onNodeWithText(headline).assertIsDisplayed()
        composeRule.onNodeWithText(home).assertIsDisplayed()
    }

    @Test
    fun thankYou_home_invokesCallback() {
        val home = composeRule.activity.getString(R.string.premium_thank_you_home)
        var wentHome = false

        composeRule.setContent {
            LoveTestTheme {
                PremiumThankYouScreen(onHome = { wentHome = true }, onLoveTest = {})
            }
        }

        composeRule.onNodeWithText(home).performClick()
        assertTrue(wentHome)
    }
}
