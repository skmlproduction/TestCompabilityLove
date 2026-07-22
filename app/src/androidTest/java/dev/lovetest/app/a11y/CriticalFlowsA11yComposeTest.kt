package dev.lovetest.app.a11y

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.navigation.navHostTestModule
import dev.lovetest.app.ui.features.CalculatorInputScreen
import dev.lovetest.app.ui.features.ProtocolInputScreen
import dev.lovetest.app.ui.features.WheelSpinScreen
import dev.lovetest.app.ui.features.ZodiacPickScreen
import dev.lovetest.app.ui.hub.HubScreen
import dev.lovetest.app.ui.love.LoveTestInputScreen
import dev.lovetest.app.ui.love.LoveTestResultScreen
import dev.lovetest.app.ui.onboarding.OnboardingScreen
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.settings.SettingsScreen
import dev.lovetest.core.ui.theme.LoveTestTheme
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinApplication
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class CriticalFlowsA11yComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun stopPreviousKoin() {
        stopKoin()
        LoveTestSession.clear()
    }

    @After
    fun tearDown() {
        LoveTestSession.clear()
        stopKoin()
    }

    @Test
    fun loveTestInput_backAndNameFields_haveAccessibilityNames() {
        val back = composeRule.activity.getString(R.string.nav_back)
        val name1 = composeRule.activity.getString(R.string.love_test_name1_label)
        val name2Label = composeRule.activity.getString(R.string.love_test_name2_label)
        val name2Hint = composeRule.activity.getString(R.string.love_test_name2_hint)
        val prefs = mockPrefs()

        composeRule.setContent {
            KoinApplication(application = { modules(module { single { prefs } }) }) {
                LoveTestTheme {
                    LoveTestInputScreen(onBack = {}, onCalculate = { _, _ -> })
                }
            }
        }

        composeRule.onNodeWithContentDescription(back).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(name1).assertIsDisplayed()
        composeRule.onNodeWithContentDescription("$name2Label, $name2Hint").assertIsDisplayed()
    }

    @Test
    fun calculatorInput_nameField_hasLabelSemantics() {
        val label = composeRule.activity.getString(R.string.calculator_name1_label)
        val prefs = mockPrefs()

        composeRule.setContent {
            KoinApplication(application = { modules(module { single { prefs } }) }) {
                LoveTestTheme {
                    CalculatorInputScreen(onBack = {}, onSubmit = { _, _ -> })
                }
            }
        }

        composeRule.onNodeWithContentDescription(label).assertIsDisplayed()
    }

    @Test
    fun protocolInput_nameField_hasLabelSemantics() {
        val label = composeRule.activity.getString(R.string.protocol_name1_label)
        val prefs = mockPrefs()

        composeRule.setContent {
            KoinApplication(application = { modules(module { single { prefs } }) }) {
                LoveTestTheme {
                    ProtocolInputScreen(onBack = {}, onSubmit = { _, _ -> })
                }
            }
        }

        composeRule.onNodeWithContentDescription(label).assertIsDisplayed()
    }

    @Test
    fun zodiacPick_signCell_hasSignContentDescription() {
        val sign = composeRule.activity.resources.getStringArray(R.array.zodiac_signs).first()

        composeRule.setContent {
            LoveTestTheme {
                ZodiacPickScreen(onBack = {}, onSubmit = { _, _ -> })
            }
        }

        composeRule.onNodeWithContentDescription(sign).assertIsDisplayed()
    }

    @Test
    fun hubBottomNav_selectedTestsAnnounced() {
        val tests = composeRule.activity.getString(R.string.hub_nav_tests)
        val selected = composeRule.activity.getString(R.string.hub_nav_item_selected, tests)

        composeRule.setContent {
            hubTestContent()
        }

        composeRule.onNodeWithContentDescription(selected).assertIsDisplayed()
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

    @Test
    fun onboarding_showsWelcomeHeadlineForScreenReaders() {
        val headline = composeRule.activity.getString(R.string.onboarding_welcome_headline)

        composeRule.setContent {
            LoveTestTheme {
                OnboardingScreen(onComplete = {}, onSkip = {})
            }
        }

        composeRule.onNodeWithText(headline).assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.onboarding_next)).assertIsDisplayed()
    }

    @Test
    fun loveTestResult_percentRing_hasContentDescription() {
        LoveTestSession.storeLoveResult("Anna", "Max", 87)
        val percentCd = composeRule.activity.getString(R.string.love_test_percent_cd)

        composeRule.setContent {
            LoveTestTheme {
                LoveTestResultScreen(onShare = {}, onTryAgain = {}, onHome = {})
            }
        }

        composeRule.onNodeWithContentDescription(percentCd).assertIsDisplayed()
    }

    @Test
    fun settings_backAndPremiumRow_haveMergedSemantics() {
        val back = composeRule.activity.getString(R.string.nav_back)
        val premiumTitle = composeRule.activity.getString(R.string.settings_premium_title)
        val premiumSub = composeRule.activity.getString(R.string.settings_premium_sub)
        val prefs = mockk<AppPreferences>(relaxed = true) {
            every { isPremiumFlow } returns flowOf(false)
        }

        composeRule.setContent {
            LoveTestTheme {
                SettingsScreen(
                    onBack = {},
                    onPremium = {},
                    onRestorePurchases = {},
                    onReplayOnboarding = {},
                    onLanguage = {},
                    onPrivacy = {},
                    onDataCollection = {},
                    onClearSavedNames = {},
                    onManageAdsConsent = {},
                    preferences = prefs,
                )
            }
        }

        composeRule.onNodeWithContentDescription(back).assertIsDisplayed()
        composeRule.onNodeWithContentDescription("$premiumTitle, $premiumSub").assertIsDisplayed()
    }

    private fun mockPrefs() = mockk<AppPreferences>(relaxed = true) {
        coEvery { getLastNames() } returns Pair("", "")
    }

    @androidx.compose.runtime.Composable
    private fun hubTestContent() {
        KoinApplication(
            application = { modules(navHostTestModule()) },
        ) {
            LoveTestTheme {
                HubScreen(
                    onOpenLoveTest = {},
                    onOpenCalculator = {},
                    onOpenPair = {},
                    onOpenVictory = {},
                    onOpenLetters = {},
                    onOpenZodiac = {},
                    onOpenProtocol = {},
                    onOpenWheel = {},
                    onOpenPremium = {},
                    onOpenSettings = {},
                )
            }
        }
    }
}
