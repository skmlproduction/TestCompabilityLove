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
class WheelResultScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @After
    fun tearDown() {
        LoveTestSession.clear()
    }

    @Test
    fun wheelResult_showsPrizeAndActions() {
        val segments = composeRule.activity.resources.getStringArray(R.array.wheel_segments)
        LoveTestSession.storeWheelResult(2, segments[2])
        val prizeTag = composeRule.activity.getString(R.string.wheel_prize_tag)
        val spinAgain = composeRule.activity.getString(R.string.wheel_spin_again)
        val share = composeRule.activity.getString(R.string.wheel_share_cta)

        composeRule.setContent {
            LoveTestTheme {
                WheelResultScreen(onShare = {}, onSpinAgain = {}, onHome = {})
            }
        }

        composeRule.onNodeWithText(segments[2]).assertIsDisplayed()
        composeRule.onNodeWithText(prizeTag).assertIsDisplayed()
        composeRule.onNodeWithText(spinAgain).assertIsDisplayed()
        composeRule.onNodeWithText(share).assertIsDisplayed()
    }

    @Test
    fun wheelResult_spinAgain_invokesCallback() {
        val segments = composeRule.activity.resources.getStringArray(R.array.wheel_segments)
        LoveTestSession.storeWheelResult(2, segments[2])
        val spinAgain = composeRule.activity.getString(R.string.wheel_spin_again)
        var spunAgain = false

        composeRule.setContent {
            LoveTestTheme {
                WheelResultScreen(
                    onShare = {},
                    onSpinAgain = { spunAgain = true },
                    onHome = {},
                )
            }
        }

        composeRule.onNodeWithText(spinAgain).performClick()
        assertTrue(spunAgain)
    }
}
