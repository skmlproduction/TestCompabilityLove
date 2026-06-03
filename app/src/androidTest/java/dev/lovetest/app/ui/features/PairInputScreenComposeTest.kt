package dev.lovetest.app.ui.features

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
class PairInputScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun pairInput_showsHeroAndCta() {
        val hero = composeRule.activity.getString(R.string.pair_hero_title)
        val cta = composeRule.activity.getString(R.string.pair_cta)
        val prefs = mockPrefs()

        composeRule.setContent {
            KoinApplication(application = { modules(module { single { prefs } }) }) {
                LoveTestTheme {
                    PairInputScreen(onBack = {}, onSubmit = { _, _ -> })
                }
            }
        }

        composeRule.onNodeWithText(hero).assertIsDisplayed()
        composeRule.onNodeWithText(cta).assertIsDisplayed()
    }

    @Test
    fun pairInput_chip_triggersSubmit() {
        val chip = composeRule.activity.getString(R.string.pair_chip_1)
        val cta = composeRule.activity.getString(R.string.pair_cta)
        val parts = chip.split(" + ", limit = 2)
        val expected = parts[0].trim() to parts[1].trim()
        val prefs = mockPrefs()
        var submitted: Pair<String, String>? = null

        composeRule.setContent {
            KoinApplication(application = { modules(module { single { prefs } }) }) {
                LoveTestTheme {
                    PairInputScreen(
                        onBack = {},
                        onSubmit = { n1, n2 -> submitted = n1 to n2 },
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText(chip).performClick()
        composeRule.onNodeWithText(cta).performClick()
        composeRule.waitForIdle()

        assertEquals(expected, submitted)
    }

    private fun mockPrefs() = mockk<AppPreferences>(relaxed = true) {
        coEvery { getLastNames() } returns Pair("", "")
    }
}
