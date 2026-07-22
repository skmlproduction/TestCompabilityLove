package dev.lovetest.app.ui.splash

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.navigation.NavIntents
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.core.ui.theme.LoveTestTheme
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.After
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.junit.Before
import org.koin.compose.KoinApplication
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class SplashScreenComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun stopPreviousKoin() {
        stopKoin()
    }

    @After
    fun tearDown() {
        DebugUiPreview.applyFromIntent(null)
    }

    @Test
    fun splash_showsHeadlineAndFeatureChips() {
        DebugUiPreview.applyFromIntent(
            Intent().putExtra(NavIntents.EXTRA_DEBUG_UI_PREVIEW, "splash_brand"),
        )
        val heroLine1 = composeRule.activity.getString(R.string.splash_hero_line1)
        val chipTest = composeRule.activity.getString(R.string.splash_chip_test)
        val prefs = mockk<AppPreferences>(relaxed = true) {
            coEvery { loadSessionSnapshot() } returns null
        }

        composeRule.setContent {
            KoinApplication(
                application = {
                    modules(
                        module {
                            single { prefs }
                            viewModel { SplashViewModel(get()) }
                        },
                    )
                },
            ) {
                LoveTestTheme {
                    SplashScreen(onNavigate = {})
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText(heroLine1).assertIsDisplayed()
        composeRule.onNodeWithText(chipTest).assertIsDisplayed()
    }
}
