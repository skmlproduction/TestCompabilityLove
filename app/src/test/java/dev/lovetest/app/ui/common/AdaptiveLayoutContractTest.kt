package dev.lovetest.app.ui.common

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveLayoutContractTest {
    @Test
    fun textBearingHeroesChipsAndPillsUseMinimumRatherThanFixedHeights() {
        assertAdaptive(
            "core/ui/src/main/java/dev/lovetest/core/ui/components/LoveSplashHeroPanel.kt",
            fixed = ".height(240.dp)",
            minimum = ".heightIn(min = 240.dp)",
        )
        assertAdaptive(
            "app/src/main/java/dev/lovetest/app/ui/onboarding/OnboardingScreen.kt",
            fixed = ".height(heroHeight)",
            minimum = ".heightIn(min = heroHeight)",
        )
        assertAdaptive(
            "app/src/main/java/dev/lovetest/app/ui/splash/SplashScreen.kt",
            fixed = ".height(LoveLayout.SplashFeatureChipHeight)",
            minimum = ".heightIn(min = LoveLayout.SplashFeatureChipHeight)",
        )

        val hub = projectFile("app/src/main/java/dev/lovetest/app/ui/hub/HubScreen.kt").readText()
        assertFalse("Hub text pills must not use fixed token heights", ".height(LoveLayout.HubGoPillHeight)" in hub)
        assertTrue("Hub GO pill must retain its minimum", ".heightIn(min = LoveLayout.HubGoPillHeight)" in hub)
        assertTrue("Hub hero chip must retain its minimum", ".heightIn(min = LoveLayout.HubHeroChipHeight)" in hub)
    }

    @Test
    fun selectionAndSettingsControlsExposeTheirInteractionRoles() {
        val hub = projectFile("app/src/main/java/dev/lovetest/app/ui/hub/HubScreen.kt").readText()
        assertTrue("Bottom navigation must expose tab semantics", "role = Role.Tab" in hub)
        assertTrue("Bottom navigation must expose both selected states", "this.selected = selected" in hub)

        val zodiac = projectFile("app/src/main/java/dev/lovetest/app/ui/features/ZodiacPickScreen.kt").readText()
        assertTrue("Zodiac choices must expose radio-button semantics", "role = Role.RadioButton" in zodiac)
        assertTrue("Zodiac choices must expose both selected states", "this.selected = isSelected" in zodiac)

        val settings = projectFile("app/src/main/java/dev/lovetest/app/ui/settings/SettingsScreen.kt").readText()
        assertTrue("Settings rows must expose button semantics", "role = Role.Button" in settings)

        val adPlaceholder = projectFile("app/src/main/java/dev/lovetest/app/ui/share/AdInterstitialPlaceholder.kt").readText()
        assertTrue("Ad close and premium actions must expose button semantics", "role = Role.Button" in adPlaceholder)
    }

    private fun assertAdaptive(relativePath: String, fixed: String, minimum: String) {
        val source = projectFile(relativePath).readText()
        assertFalse("$relativePath must not contain $fixed", fixed in source)
        assertTrue("$relativePath must contain $minimum", minimum in source)
    }

    private fun projectFile(relativePath: String): File {
        var directory = File(checkNotNull(System.getProperty("user.dir")))
        while (!File(directory, "settings.gradle.kts").isFile) {
            directory = directory.parentFile ?: error("settings.gradle.kts not found")
        }
        return File(directory, relativePath)
    }
}
