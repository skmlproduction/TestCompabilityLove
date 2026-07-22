package dev.lovetest.app.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.Before
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinApplication
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
class PairFlowComposeTest {


    @get:Rule
    val cleanup = LoveInstrumentedCleanup()

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun clearSession() {
        LoveTestSession.clear()
        stopKoin()
    }

    @Test
    fun pairInput_throughCalculating_reachesShareCta() {
        val chip = composeRule.activity.getString(R.string.pair_chip_1)
        val cta = composeRule.activity.getString(R.string.pair_cta)
        val shareCta = composeRule.activity.getString(R.string.love_test_share_cta)
        val tryAnother = composeRule.activity.getString(R.string.pair_try_another)

        composeRule.setContent {
            KoinApplication(
                application = { modules(navHostTestModule()) },
            ) {
                LoveTestTheme {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()
                    LoveTestNavHost(
                        navController = navController,
                        appScope = scope,
                        startDestination = Routes.PairInput,
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription(chip).performScrollTo().performClick()
        composeRule.onNodeWithText(cta).performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText(shareCta).fetchSemanticsNodes().isNotEmpty() &&
                composeRule.onAllNodesWithText(tryAnother).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
