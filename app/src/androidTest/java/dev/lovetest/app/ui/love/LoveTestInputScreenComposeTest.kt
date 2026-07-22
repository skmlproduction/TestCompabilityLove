package dev.lovetest.app.ui.love

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
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
import org.junit.Assert.assertNull
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import org.koin.compose.KoinApplication
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class LoveTestInputScreenComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun stopPreviousKoin() {
        stopKoin()
    }

    @Test
    fun loveTestInput_showsInlineDisclaimer() {
        val disclaimer = composeRule.activity.getString(R.string.love_test_tip_line1)
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

        composeRule.onNodeWithText(disclaimer).assertIsDisplayed()
    }

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

    @Test
    fun loveTestInput_digitsOnly_keepsCtaDisabled() {
        val cta = composeRule.activity.getString(R.string.love_test_calculate_cta)
        val label1 = composeRule.activity.getString(R.string.love_test_name1_label)
        val label2 = composeRule.activity.getString(R.string.love_test_name2_label)
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

        composeRule.onNodeWithText(label1).performTextInput("123")
        composeRule.onNodeWithText(label2).performTextInput("456")
        composeRule.onNodeWithText(cta).assertIsNotEnabled()
        composeRule.onNodeWithText(cta).performClick()
        composeRule.waitForIdle()
        assertNull(calculated)
    }
}
