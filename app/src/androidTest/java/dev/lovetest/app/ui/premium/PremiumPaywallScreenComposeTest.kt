package dev.lovetest.app.ui.premium

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
class PremiumPaywallScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun paywall_showsBuyAndContinueFree() {
        val buy = composeRule.activity.getString(R.string.premium_buy_cta)
        val cont = composeRule.activity.getString(R.string.premium_continue_free)

        composeRule.setContent {
            LoveTestTheme {
                PremiumPaywallScreen(
                    onClose = {},
                    onPurchase = {},
                    onRestore = {},
                    onContinueFree = {},
                )
            }
        }

        composeRule.onNodeWithText(buy).assertIsDisplayed()
        composeRule.onNodeWithText(cont).assertIsDisplayed()
    }

    @Test
    fun paywall_continueFree_invokesCallback() {
        val cont = composeRule.activity.getString(R.string.premium_continue_free)
        var continued = false

        composeRule.setContent {
            LoveTestTheme {
                PremiumPaywallScreen(
                    onClose = {},
                    onPurchase = {},
                    onRestore = {},
                    onContinueFree = { continued = true },
                )
            }
        }

        composeRule.onNodeWithText(cont).performClick()
        assertTrue(continued)
    }

    @Test
    fun paywall_close_invokesCallback() {
        val closeCd = composeRule.activity.getString(R.string.premium_close_cd)
        var closed = false

        composeRule.setContent {
            LoveTestTheme {
                PremiumPaywallScreen(
                    onClose = { closed = true },
                    onPurchase = {},
                    onRestore = {},
                    onContinueFree = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription(closeCd).performClick()
        assertTrue(closed)
    }
}
