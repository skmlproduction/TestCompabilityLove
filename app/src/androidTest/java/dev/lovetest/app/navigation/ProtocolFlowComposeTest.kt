package dev.lovetest.app.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.monetization.PremiumBillingManager
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.love.LoveTestFlowViewModel
import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import dev.lovetest.core.ui.theme.LoveTestTheme
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.compose.KoinApplication
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class ProtocolFlowComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun clearSession() {
        LoveTestSession.clear()
    }

    @Test
    fun protocolInput_throughCalculating_reachesResultSignals() {
        val cta = composeRule.activity.getString(R.string.protocol_cta)
        val signals = composeRule.activity.getString(R.string.protocol_signals_title)
        val prefs = mockk<AppPreferences>(relaxed = true) {
            coEvery { getLastNames() } returns Pair("", "")
            every { isPremiumFlow } returns flowOf(false)
        }
        val billing = mockk<PremiumBillingManager>(relaxed = true)

        composeRule.setContent {
            KoinApplication(
                application = {
                    modules(
                        module {
                            single { DefaultLoveScoreCalculator() }
                            single { prefs }
                            single { billing }
                            viewModel { LoveTestFlowViewModel(get(), get()) }
                        },
                    )
                },
            ) {
                LoveTestTheme {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()
                    LoveTestNavHost(
                        navController = navController,
                        appScope = scope,
                        startDestination = Routes.ProtocolInput,
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onAllNodes(hasSetTextAction())[0].apply {
            performClick()
            performTextInput("Anna")
        }
        composeRule.onAllNodes(hasSetTextAction())[1].apply {
            performClick()
            performTextInput("Max")
        }
        composeRule.onNodeWithText(cta).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(signals).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
