package dev.lovetest.app.ui.share

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.MainActivity
import dev.lovetest.app.testing.EnsureAppKoinRule
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * Uses [MainActivity] (exported) + [EnsureAppKoinRule]. Capture only needs a live
 * ComponentActivity window — Koin soft-ensure avoids ClosedScope under hitchhike runs.
 */
@RunWith(AndroidJUnit4::class)
class ShareCardImageExporterInstrumentedTest {

    private val ensureAppKoin = EnsureAppKoinRule()
    private val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(ensureAppKoin).around(activityRule)

    @Test
    fun captureLoveShareCard_returnsNonEmptyBitmap() {
        activityRule.scenario.onActivity { activity ->
            val bitmap = ShareCardImageExporter.captureLoveShareCard(
                activity,
                ShareCardContent(
                    percent = 77,
                    name1 = "Anna",
                    name2 = "Max",
                    harmonyTag = "Harmony",
                    high = true,
                ),
            )
            assertNotNull("captureLoveShareCard returned null", bitmap)
            val bmp = bitmap!!
            assertTrue(bmp.width >= ShareCardDimensions.WIDTH_PX)
            assertTrue(bmp.height >= ShareCardDimensions.HEIGHT_PX)
        }
    }

    @Test
    fun captureWheelShareCard_returnsNonEmptyBitmap() {
        activityRule.scenario.onActivity { activity ->
            val bitmap = ShareCardImageExporter.captureWheelShareCard(activity, "Lucky day")
            assertNotNull("captureWheelShareCard returned null", bitmap)
            val bmp = bitmap!!
            assertTrue(bmp.width >= ShareCardDimensions.WIDTH_PX)
            assertTrue(bmp.height >= ShareCardDimensions.HEIGHT_PX)
        }
    }
}
