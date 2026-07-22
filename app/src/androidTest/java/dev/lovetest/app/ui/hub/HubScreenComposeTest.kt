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
import dev.lovetest.app.navigation.navHostTestModule
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.Assert.assertTrue
import org.junit.Before
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinApplication
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
class HubScreenComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun stopPreviousKoin() {
        stopKoin()
    }

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
        val settingsLabel = composeRule.activity.getString(R.string.hub_nav_settings)
        var opened = false

        composeRule.setContent {
            hubTestContent(onOpenSettings = { opened = true })
        }

        composeRule.onNodeWithText(settingsLabel).performClick()
        assertTrue(opened)
    }

    @Test
    fun hub_bottomNav_selectedTests_hasAccessibilityLabel() {
        val tests = composeRule.activity.getString(R.string.hub_nav_tests)
        val selected = composeRule.activity.getString(R.string.hub_nav_item_selected, tests)

        composeRule.setContent {
            hubTestContent()
        }

        composeRule.onNodeWithContentDescription(selected).assertIsDisplayed()
    }

    @Composable
    private fun hubTestContent(
        onOpenLoveTest: () -> Unit = {},
        onOpenSettings: () -> Unit = {},
    ) {
        KoinApplication(
            application = { modules(navHostTestModule()) },
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
