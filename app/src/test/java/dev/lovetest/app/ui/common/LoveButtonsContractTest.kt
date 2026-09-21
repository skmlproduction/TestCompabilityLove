package dev.lovetest.app.ui.common

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoveButtonsContractTest {
    @Test
    fun sharedButtons_keepMinimumTouchTargetsAndAllowTwoLineLabels() {
        val source = projectFile(
            "core/ui/src/main/java/dev/lovetest/core/ui/components/LoveButtons.kt",
        ).readText()

        assertFalse("Shared buttons must not use a fixed height", ".height(" in source)
        assertTrue(
            "Primary CTA must keep its minimum height",
            ".heightIn(min = LoveLayout.PrimaryCtaHeight)" in source,
        )
        assertTrue(
            "Secondary CTA must keep its minimum height",
            source.countMatches(".heightIn(min = LoveLayout.SecondaryCtaHeight)") == 2,
        )
        assertTrue(
            "Every shared button label must allow two lines",
            source.countMatches("maxLines = 2") == 3,
        )

        val resultActions = projectFile(
            "app/src/main/java/dev/lovetest/app/ui/common/LoveFeatureResultActions.kt",
        ).readText()
        assertFalse("Home action must not use a fixed height", ".height(" in resultActions)
        assertTrue(
            "Home action must apply spacing outside its minimum touch target",
            ".padding(top = 8.dp)\n                .fillMaxWidth()\n" +
                "                .heightIn(min = LoveLayout.SecondaryCtaHeight)" in resultActions,
        )

        val shareActions = projectFile(
            "app/src/main/java/dev/lovetest/app/ui/share/ShareActionsPanel.kt",
        ).readText()
        assertFalse("Share cancel action must not use a fixed height", ".height(52.dp)" in shareActions)
        assertTrue("Share cancel action must retain its minimum", ".heightIn(min = 52.dp)" in shareActions)
    }

    private fun String.countMatches(value: String): Int = windowed(value.length).count { it == value }

    private fun projectFile(relativePath: String): File {
        var directory = File(checkNotNull(System.getProperty("user.dir")))
        while (!File(directory, "settings.gradle.kts").isFile) {
            directory = directory.parentFile ?: error("settings.gradle.kts not found")
        }
        return File(directory, relativePath)
    }
}
