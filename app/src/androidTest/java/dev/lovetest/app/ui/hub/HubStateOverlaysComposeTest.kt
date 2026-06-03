package dev.lovetest.app.ui.hub

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
class HubStateOverlaysComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun hubLoadingOverlay_showsStatusAndCancelHint() {
        val status = composeRule.activity.getString(R.string.hub_loading_overlay_status)
        val cancelHint = composeRule.activity.getString(R.string.hub_loading_cancel_unavailable)

        composeRule.setContent {
            LoveTestTheme {
                HubLoadingOverlay()
            }
        }

        composeRule.onNodeWithText(status).assertIsDisplayed()
        composeRule.onNodeWithText(cancelHint).assertIsDisplayed()
    }

    @Test
    fun hubErrorOverlay_showsRetryAndContinueOffline() {
        val retry = composeRule.activity.getString(R.string.error_retry_cta)
        val offline = composeRule.activity.getString(R.string.error_network_continue_offline)
        var retried = false
        var continued = false

        composeRule.setContent {
            LoveTestTheme {
                HubErrorNetworkOverlay(
                    onRetry = { retried = true },
                    onContinueOffline = { continued = true },
                )
            }
        }

        composeRule.onNodeWithText(retry).performClick()
        composeRule.onNodeWithText(offline).performClick()
        assertTrue(retried)
        assertTrue(continued)
    }

    @Test
    fun hubErrorTopBanner_showsNetworkBanner() {
        val banner = composeRule.activity.getString(R.string.error_network_banner)

        composeRule.setContent {
            LoveTestTheme {
                HubErrorTopBanner()
            }
        }

        composeRule.onNodeWithText(banner).assertIsDisplayed()
    }
}
