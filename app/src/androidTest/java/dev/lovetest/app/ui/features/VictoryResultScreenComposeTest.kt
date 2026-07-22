package dev.lovetest.app.ui.features

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VictoryResultScreenComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun victoryResult_maybeOutcome_showsOutcomeChips() {
        LoveTestSession.storeLoveResult("Olga", "Olga", 23)
        // String is literally "?" and appears on hero + outcome chip — match any.
        val maybe = composeRule.activity.getString(R.string.victory_outcome_maybe)

        composeRule.setContent {
            LoveTestTheme {
                VictoryResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        assertTrue(composeRule.onAllNodesWithText(maybe).fetchSemanticsNodes().size >= 1)
    }

    @Test
    fun victoryResult_showsVerdictAndActions() {
        LoveTestSession.storeLoveResult("Olga", "Olga", 72)
        val tag = composeRule.activity.getString(R.string.victory_hero_tag_yes)
        val tryAnother = composeRule.activity.getString(R.string.victory_try_another)
        val share = composeRule.activity.getString(R.string.love_test_share_cta)

        composeRule.setContent {
            LoveTestTheme {
                VictoryResultScreen(onShare = {}, onTryAnother = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(tag).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(tryAnother).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(share).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun victoryResult_tryAnother_invokesCallback() {
        LoveTestSession.storeLoveResult("Olga", "Olga", 72)
        val tryAnother = composeRule.activity.getString(R.string.victory_try_another)
        var retried = false

        composeRule.setContent {
            LoveTestTheme {
                VictoryResultScreen(
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
