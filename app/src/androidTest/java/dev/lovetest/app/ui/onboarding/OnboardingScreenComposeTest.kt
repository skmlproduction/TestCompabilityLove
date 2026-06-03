package dev.lovetest.app.ui.onboarding

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
class OnboardingScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun onboarding_showsWelcomeHeadline() {
        val headline = composeRule.activity.getString(R.string.onboarding_welcome_headline)

        composeRule.setContent {
            LoveTestTheme {
                OnboardingScreen(onComplete = {}, onSkip = {})
            }
        }

        composeRule.onNodeWithText(headline).assertIsDisplayed()
    }

    @Test
    fun onboarding_next_navigatesToTestsPage() {
        val welcome = composeRule.activity.getString(R.string.onboarding_welcome_headline)
        val tests = composeRule.activity.getString(R.string.onboarding_tests_headline)
        val next = composeRule.activity.getString(R.string.onboarding_next)

        composeRule.setContent {
            LoveTestTheme {
                OnboardingScreen(onComplete = {}, onSkip = {})
            }
        }

        composeRule.onNodeWithText(welcome).assertIsDisplayed()
        composeRule.onNodeWithText(next).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText(tests).assertIsDisplayed()
    }

    @Test
    fun onboarding_skip_invokesCallback() {
        val skip = composeRule.activity.getString(R.string.onboarding_skip)
        var skipped = false

        composeRule.setContent {
            LoveTestTheme {
                OnboardingScreen(onComplete = {}, onSkip = { skipped = true })
            }
        }

        composeRule.onNodeWithText(skip).performClick()
        assertTrue(skipped)
    }
}
