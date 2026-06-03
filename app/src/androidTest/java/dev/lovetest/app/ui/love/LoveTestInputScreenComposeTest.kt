package dev.lovetest.app.ui.love

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.core.ui.theme.LoveTestTheme
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinApplication
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class LoveTestInputScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loveTestInput_showsCalculateCta() {
        val cta = composeRule.activity.getString(R.string.love_test_calculate_cta)
        val prefs = mockk<AppPreferences>(relaxed = true) {
            coEvery { getLastNames() } returns Pair("", "")
        }

        composeRule.setContent {
            KoinApplication(
                application = { modules(module { single { prefs } }) },
            ) {
                LoveTestTheme {
                    LoveTestInputScreen(onBack = {}, onCalculate = { _, _ -> })
                }
            }
        }

        composeRule.onNodeWithText(cta).assertIsDisplayed()
    }

    @Test
    fun loveTestInput_pairChip_triggersCalculate() {
        val chip = composeRule.activity.getString(R.string.love_test_pair_example_1)
        val cta = composeRule.activity.getString(R.string.love_test_calculate_cta)
        val parts = chip.split(" + ", limit = 2)
        val expected = parts[0].trim() to parts[1].trim()
        val prefs = mockk<AppPreferences>(relaxed = true) {
            coEvery { getLastNames() } returns Pair("", "")
        }
        var calculated: Pair<String, String>? = null

        composeRule.setContent {
            KoinApplication(
                application = { modules(module { single { prefs } }) },
            ) {
                LoveTestTheme {
                    LoveTestInputScreen(
                        onBack = {},
                        onCalculate = { n1, n2 -> calculated = n1 to n2 },
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText(chip).performClick()
        composeRule.onNodeWithText(cta).performClick()
        composeRule.waitForIdle()

        assertEquals(expected, calculated)
    }
}
