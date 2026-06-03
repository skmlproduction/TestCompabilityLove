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
class ZodiacPickScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun zodiacPick_showsHeroAndCta() {
        val hero = composeRule.activity.getString(R.string.zodiac_hero_title)
        val cta = composeRule.activity.getString(R.string.zodiac_cta)

        composeRule.setContent {
            LoveTestTheme {
                ZodiacPickScreen(onBack = {}, onSubmit = { _, _ -> })
            }
        }

        composeRule.onNodeWithText(hero).assertIsDisplayed()
        composeRule.onNodeWithText(cta).assertIsDisplayed()
    }

    @Test
    fun zodiacPick_twoSigns_triggersSubmit() {
        val signs = composeRule.activity.resources.getStringArray(R.array.zodiac_signs)
        val sign1 = signs[0]
        val sign2 = signs[1]
        val cta = composeRule.activity.getString(R.string.zodiac_cta)
        var submitted: Pair<String, String>? = null

        composeRule.setContent {
            LoveTestTheme {
                ZodiacPickScreen(
                    onBack = {},
                    onSubmit = { s1, s2 -> submitted = s1 to s2 },
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText(sign1).performClick()
        composeRule.onNodeWithText(sign2).performClick()
        composeRule.onNodeWithText(cta).performClick()
        composeRule.waitForIdle()

        assertEquals(sign1 to sign2, submitted)
    }
}
