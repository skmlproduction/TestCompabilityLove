package dev.lovetest.app.ui.features

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
class CalculatorInputScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun calculatorInput_showsHeroAndCta() {
        val hero = composeRule.activity.getString(R.string.calculator_hero_title)
        val cta = composeRule.activity.getString(R.string.calculator_cta)
        val prefs = mockPrefs()

        composeRule.setContent {
            KoinApplication(application = { modules(module { single { prefs } }) }) {
                LoveTestTheme {
                    CalculatorInputScreen(onBack = {}, onSubmit = { _, _ -> })
                }
            }
        }

        composeRule.onNodeWithText(hero).assertIsDisplayed()
        composeRule.onNodeWithText(cta).assertIsDisplayed()
    }

    @Test
    fun calculatorInput_names_triggersSubmit() {
        val cta = composeRule.activity.getString(R.string.calculator_cta)
        val prefs = mockPrefs()
        var submitted: Pair<String, String>? = null

        composeRule.setContent {
            KoinApplication(application = { modules(module { single { prefs } }) }) {
                LoveTestTheme {
                    CalculatorInputScreen(
                        onBack = {},
                        onSubmit = { n1, n2 -> submitted = n1 to n2 },
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onAllNodes(hasSetTextAction())[0].apply {
            performClick()
            performTextInput("Maria")
        }
        composeRule.onAllNodes(hasSetTextAction())[1].apply {
            performClick()
            performTextInput("Ivan")
        }
        composeRule.onNodeWithText(cta).performClick()
        composeRule.waitForIdle()

        assertEquals("Maria" to "Ivan", submitted)
    }

    private fun mockPrefs() = mockk<AppPreferences>(relaxed = true) {
        coEvery { getLastNames() } returns Pair("", "")
    }
}
