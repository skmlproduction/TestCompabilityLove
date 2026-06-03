package dev.lovetest.app.ui.features

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LettersInputScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun lettersInput_showsHeroAndCta() {
        val hero = composeRule.activity.getString(R.string.letters_hero_title)
        val cta = composeRule.activity.getString(R.string.letters_cta)

        composeRule.setContent {
            LoveTestTheme {
                LettersInputScreen(onBack = {}, onSubmit = { _, _ -> })
            }
        }

        composeRule.onNodeWithText(hero).assertIsDisplayed()
        composeRule.onNodeWithText(cta).assertIsDisplayed()
    }

    @Test
    fun lettersInput_chip_triggersSubmit() {
        val chip = composeRule.activity.getString(R.string.letters_chip_1)
        val cta = composeRule.activity.getString(R.string.letters_cta)
        val parts = chip.split(" + ", limit = 2)
        val expected = parts[0].trim() to parts[1].trim()
        var submitted: Pair<String, String>? = null

        composeRule.setContent {
            LoveTestTheme {
                LettersInputScreen(
                    onBack = {},
                    onSubmit = { w1, w2 -> submitted = w1 to w2 },
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText(chip).performClick()
        composeRule.onNodeWithText(cta).performClick()
        composeRule.waitForIdle()

        assertEquals(expected, submitted)
    }
}
