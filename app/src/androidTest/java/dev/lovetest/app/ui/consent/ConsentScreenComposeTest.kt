package dev.lovetest.app.ui.consent

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.Assert.assertTrue
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConsentScreenComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun consentScreen_showsHeadlineAndAcceptButton() {
        val headline = composeRule.activity.getString(R.string.consent_headline)
        val accept = composeRule.activity.getString(R.string.consent_accept)

        composeRule.setContent {
            LoveTestTheme {
                ConsentScreen(onAccept = {}, onManage = {}, onOpenPrivacy = {})
            }
        }

        composeRule.onNodeWithText(headline).performScrollTo().assertIsDisplayed()
        // Accept lives in the sticky footer (outside the scroll column).
        composeRule.onNodeWithText(accept).assertIsDisplayed()
        if (!dev.lovetest.app.BuildConfig.ADS_ENABLED && dev.lovetest.app.BuildConfig.DEBUG) {
            composeRule.onNodeWithText(
                composeRule.activity.getString(R.string.consent_ads_off_banner),
            ).performScrollTo().assertIsDisplayed()
        }
    }

    @Test
    fun consentAccept_invokesCallback() {
        val accept = composeRule.activity.getString(R.string.consent_accept)
        var accepted = false

        composeRule.setContent {
            LoveTestTheme {
                ConsentScreen(
                    onAccept = { accepted = true },
                    onManage = {},
                    onOpenPrivacy = {},
                )
            }
        }

        composeRule.onNodeWithText(accept).performClick()
        assertTrue(accepted)
    }
}
