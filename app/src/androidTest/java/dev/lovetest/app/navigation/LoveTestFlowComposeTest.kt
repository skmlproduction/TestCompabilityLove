package dev.lovetest.app.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
class LoveTestFlowComposeTest {


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
    fun loveInput_throughCalculating_reachesResultActions() {
        val chip = composeRule.activity.getString(R.string.love_test_pair_example_1)
        val cta = composeRule.activity.getString(R.string.love_test_calculate_cta)
        val resultTitle = composeRule.activity.getString(R.string.love_test_result_title)
        val shareCta = composeRule.activity.getString(R.string.love_test_share_cta)

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
                        startDestination = Routes.LoveTestInput,
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText(chip).performClick()
        composeRule.onNodeWithText(cta).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(resultTitle).fetchSemanticsNodes().isNotEmpty() &&
                composeRule.onAllNodesWithText(shareCta).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
