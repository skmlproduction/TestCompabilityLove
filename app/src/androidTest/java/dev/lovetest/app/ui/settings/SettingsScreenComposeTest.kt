package dev.lovetest.app.ui.settings

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.R
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.ui.settings.SettingsScreen
import dev.lovetest.core.ui.theme.LoveTestTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun settingsPremiumRow_showsSupportSubtitleWhenAdsOff() {
        org.junit.Assume.assumeFalse(BuildConfig.ADS_ENABLED)
        val premiumTitle = composeRule.activity.getString(R.string.settings_premium_title)
        val premiumSub = composeRule.activity.getString(R.string.settings_premium_sub)
        val prefs = mockk<AppPreferences>(relaxed = true) {
            every { isPremiumFlow } returns flowOf(false)
        }

        composeRule.setContent {
            LoveTestTheme {
                SettingsScreen(
                    onBack = {},
                    onPremium = {},
                    onRestorePurchases = {},
                    onReplayOnboarding = {},
                    onLanguage = {},
                    onPrivacy = {},
                    onDataCollection = {},
                    onClearSavedNames = {},
                    onManageAdsConsent = {},
                    preferences = prefs,
                )
            }
        }

        composeRule.onNodeWithText(premiumTitle).assertIsDisplayed()
        composeRule.onNodeWithText(premiumSub).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_backButton_hasMergedLabel() {
        val back = composeRule.activity.getString(R.string.nav_back)
        val prefs = mockk<AppPreferences>(relaxed = true) {
            every { isPremiumFlow } returns flowOf(false)
        }

        composeRule.setContent {
            LoveTestTheme {
                settingsTestScreen(prefs = prefs)
            }
        }

        composeRule.onNodeWithContentDescription(back).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsPrivacyRow() {
        val privacyTitle = composeRule.activity.getString(R.string.settings_privacy_open)
        val prefs = mockk<AppPreferences>(relaxed = true) {
            every { isPremiumFlow } returns flowOf(false)
        }

        composeRule.setContent {
            LoveTestTheme {
                SettingsScreen(
                    onBack = {},
                    onPremium = {},
                    onRestorePurchases = {},
                    onReplayOnboarding = {},
                    onLanguage = {},
                    onPrivacy = {},
                    onDataCollection = {},
                    onClearSavedNames = {},
                    onManageAdsConsent = {},
                    preferences = prefs,
                )
            }
        }

        composeRule.onNodeWithText(privacyTitle).assertIsDisplayed()
    }

    @Test
    fun settingsPrivacyRow_invokesCallback() {
        val privacyTitle = composeRule.activity.getString(R.string.settings_privacy_open)
        val prefs = mockk<AppPreferences>(relaxed = true) {
            every { isPremiumFlow } returns flowOf(false)
        }
        var privacyClicked = false

        composeRule.setContent {
            LoveTestTheme {
                SettingsScreen(
                    onBack = {},
                    onPremium = {},
                    onRestorePurchases = {},
                    onReplayOnboarding = {},
                    onLanguage = {},
                    onPrivacy = { privacyClicked = true },
                    onDataCollection = {},
                    onClearSavedNames = {},
                    onManageAdsConsent = {},
                    preferences = prefs,
                )
            }
        }

        composeRule.onNodeWithText(privacyTitle).performClick()
        assertTrue(privacyClicked)
    }

    @Composable
    private fun settingsTestScreen(prefs: AppPreferences) {
        SettingsScreen(
            onBack = {},
            onPremium = {},
            onRestorePurchases = {},
            onReplayOnboarding = {},
            onLanguage = {},
            onPrivacy = {},
            onDataCollection = {},
            onClearSavedNames = {},
            onManageAdsConsent = {},
            preferences = prefs,
        )
    }
}
