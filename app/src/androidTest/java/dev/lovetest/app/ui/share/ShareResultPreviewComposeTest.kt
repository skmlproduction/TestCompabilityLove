package dev.lovetest.app.ui.share

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
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
                    onDismiss = {},
                    onShare = {},
                )
            }
        }

        composeRule.onNodeWithText(title).assertIsDisplayed()
        composeRule.onNodeWithText("87%").assertIsDisplayed()
        composeRule.onNodeWithText("Anna + Max").assertIsDisplayed()
    }
}
