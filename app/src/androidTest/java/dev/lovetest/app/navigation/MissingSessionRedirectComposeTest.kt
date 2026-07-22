package dev.lovetest.app.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.Assert.assertFalse
import org.junit.Before
import dev.lovetest.app.testing.LoveInstrumentedCleanup
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinApplication
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
class MissingSessionRedirectComposeTest {


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
    fun loveResultWithoutSession_redirectsToHub() {
        assertFalse(LoveTestSession.hasLoveResult())
        val hubTitle = composeRule.activity.getString(R.string.hub_hero_title)

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
                        startDestination = Routes.LoveTestResult,
                    )
                }
            }
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(hubTitle).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
