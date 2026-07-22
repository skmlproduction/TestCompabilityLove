package dev.lovetest.app.ui.features

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WheelSpinScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun wheelSpin_showsHeroAndSpinCta() {
        val hero = composeRule.activity.getString(R.string.wheel_hero_body)
        val cta = composeRule.activity.getString(R.string.wheel_spin_cta)
        val ready = composeRule.activity.getString(R.string.wheel_ready_hint)

        composeRule.setContent {
            LoveTestTheme {
                WheelSpinScreen(onBack = {}, onSpinComplete = { _, _ -> })
            }
        }

        composeRule.onNodeWithText(hero).assertIsDisplayed()
        composeRule.onNodeWithText(cta).assertIsDisplayed()
        composeRule.onNodeWithText(ready).assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.wheel_spin_note3)).assertIsDisplayed()
    }

    @Test
    fun wheelSpin_discHasSegmentsContentDescription() {
        val segments = composeRule.activity.resources
            .getStringArray(R.array.wheel_segments)
            .joinToString()
        val segmentsCd = composeRule.activity.getString(R.string.wheel_segments_cd, segments)

        composeRule.setContent {
            LoveTestTheme {
                WheelSpinScreen(onBack = {}, onSpinComplete = { _, _ -> })
            }
        }

        composeRule.onNodeWithContentDescription(segmentsCd).assertIsDisplayed()
    }
}
