package dev.lovetest.app.ui.love

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import dev.lovetest.core.ui.theme.LoveTestTheme
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoveTestCalculatingScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loveCalculating_showsTitleAndSteps() {
        val title = composeRule.activity.getString(R.string.love_test_calculating_title)
        val step1 = composeRule.activity.getString(R.string.love_test_calculating_step1)
        val viewModel = previewViewModel()
        viewModel.runPreviewCalculating("Anna", "Max")

        composeRule.setContent {
            LoveTestTheme {
                LoveTestCalculatingScreen(
                    flavor = TestCalculatingFlavor.Love,
                    viewModel = viewModel,
                )
            }
        }

        composeRule.onNodeWithText(title).assertIsDisplayed()
        composeRule.onNodeWithText(step1).assertIsDisplayed()
        composeRule.onNodeWithText("Anna").assertIsDisplayed()
        composeRule.onNodeWithText("Max").assertIsDisplayed()
    }

    @Test
    fun protocolCalculating_showsProtocolTitle() {
        val title = composeRule.activity.getString(R.string.protocol_calculating_title)
        val step2 = composeRule.activity.getString(R.string.protocol_calculating_step2)
        val viewModel = previewViewModel()
        viewModel.runPreviewCalculating("Sophia", "Dmitry")

        composeRule.setContent {
            LoveTestTheme {
                LoveTestCalculatingScreen(
                    flavor = TestCalculatingFlavor.Protocol,
                    viewModel = viewModel,
                )
            }
        }

        composeRule.onNodeWithText(title).assertIsDisplayed()
        composeRule.onNodeWithText(step2).assertIsDisplayed()
        composeRule.onNodeWithText("Sophia").assertIsDisplayed()
        composeRule.onNodeWithText("Dmitry").assertIsDisplayed()
    }

    private fun previewViewModel(): LoveTestFlowViewModel {
        val prefs = mockk<AppPreferences>(relaxed = true) {
            coEvery { saveSessionSnapshot(any()) } returns Unit
        }
        return LoveTestFlowViewModel(DefaultLoveScoreCalculator(), prefs)
    }
}
