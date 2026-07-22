package dev.lovetest.app.ui.share

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.lovetest.app.R
import dev.lovetest.core.ui.theme.LoveTestTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShareResultPreviewComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun sharePreview_showsTitleAndPercent() {
        val title = composeRule.activity.getString(R.string.share_preview_title)

        composeRule.setContent {
            LoveTestTheme {
                ShareResultPreview(
                    percent = 87,
                    name1 = "Anna",
                    name2 = "Max",
                    harmonyTag = "Great pair",
                    shareText = "Love Tester — 87%",
                    high = true,
                    onDismiss = {},
                )
            }
        }

        composeRule.onNodeWithText(title).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("87%").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Anna + Max").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun sharePreview_showsShareTargets() {
        val telegram = composeRule.activity.getString(R.string.share_target_telegram)
        val whatsapp = composeRule.activity.getString(R.string.share_target_whatsapp)

        composeRule.setContent {
            LoveTestTheme {
                ShareResultPreview(
                    percent = 42,
                    name1 = "A",
                    name2 = "B",
                    harmonyTag = "Low",
                    shareText = "Love Tester — 42%",
                    high = false,
                    onDismiss = {},
                )
            }
        }

        composeRule.onNodeWithText(telegram).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(whatsapp).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("42%").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun sharePreview_mutedCard_showsLowPercent() {
        composeRule.setContent {
            LoveTestTheme {
                ShareResultPreview(
                    percent = 23,
                    name1 = "Ivan",
                    name2 = "Olga",
                    harmonyTag = "Needs work",
                    shareText = "Love Tester — 23%",
                    high = false,
                    onDismiss = {},
                )
            }
        }

        composeRule.onNodeWithText("23%").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Ivan + Olga").performScrollTo().assertIsDisplayed()
    }
}
