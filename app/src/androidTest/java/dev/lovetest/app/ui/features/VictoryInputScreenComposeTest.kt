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
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import org.koin.compose.KoinApplication
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class VictoryInputScreenComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun stopPreviousKoin() {
        stopKoin()
    }

    @Test
    fun victoryInput_showsHeroAndCta() {
        val hero = composeRule.activity.getString(R.string.victory_hero_title)
        val cta = composeRule.activity.getString(R.string.victory_cta)
        val prefs = mockPrefs()

        composeRule.setContent {
            KoinApplication(application = { modules(module { single { prefs } }) }) {
                LoveTestTheme {
                    VictoryInputScreen(onBack = {}, onSubmit = {})
                }
            }
        }

        composeRule.onNodeWithText(hero).assertIsDisplayed()
        composeRule.onNodeWithText(cta).assertIsDisplayed()
    }

    @Test
    fun victoryInput_chip_triggersSubmit() {
        val chip = composeRule.activity.getString(R.string.victory_chip_1)
        val cta = composeRule.activity.getString(R.string.victory_cta)
        val prefs = mockPrefs()
        var submitted: String? = null

        composeRule.setContent {
            KoinApplication(application = { modules(module { single { prefs } }) }) {
                LoveTestTheme {
                    VictoryInputScreen(
                        onBack = {},
                        onSubmit = { name -> submitted = name },
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText(chip).performClick()
        composeRule.onNodeWithText(cta).performClick()
        composeRule.waitForIdle()

        assertEquals(chip, submitted)
    }

    private fun mockPrefs() = mockk<AppPreferences>(relaxed = true) {
        coEvery { getLastNames() } returns Pair("", "")
    }
}
