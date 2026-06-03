package dev.lovetest.app.ui.hub

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.core.ui.theme.LoveTestTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class HubScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun hub_showsLoveTestCard() {
        val loveTitle = composeRule.activity.getString(R.string.hub_test_love_title)

        composeRule.setContent {
            hubTestContent(onOpenLoveTest = {})
        }

        composeRule.onNodeWithText(loveTitle).assertIsDisplayed()
    }

    @Test
    fun hub_loveTestCard_invokesCallback() {
        val loveTitle = composeRule.activity.getString(R.string.hub_test_love_title)
        var opened = false

        composeRule.setContent {
            hubTestContent(onOpenLoveTest = { opened = true })
        }

        composeRule.onNodeWithText(loveTitle).performClick()
        assertTrue(opened)
    }

    @Test
    fun hub_settings_invokesCallback() {
        val settingsCd = composeRule.activity.getString(R.string.hub_settings_cd)
        var opened = false

        composeRule.setContent {
            hubTestContent(onOpenSettings = { opened = true })
        }

        composeRule.onNodeWithContentDescription(settingsCd).performClick()
        assertTrue(opened)
    }

    @Composable
    private fun hubTestContent(
        onOpenLoveTest: () -> Unit = {},
        onOpenSettings: () -> Unit = {},
    ) {
        val prefs = mockk<AppPreferences>(relaxed = true) {
            every { isPremiumFlow } returns flowOf(false)
        }
        KoinApplication(
            application = {
                modules(
                    module {
                        viewModel { HubViewModel() }
                        single { prefs }
                    },
                )
            },
        ) {
            LoveTestTheme {
                HubScreen(
                    onOpenLoveTest = onOpenLoveTest,
                    onOpenCalculator = {},
                    onOpenPair = {},
                    onOpenVictory = {},
                    onOpenLetters = {},
                    onOpenZodiac = {},
                    onOpenWheel = {},
                    onOpenProtocol = {},
                    onOpenPremium = {},
                    onOpenSettings = onOpenSettings,
                )
            }
        }
    }
}
