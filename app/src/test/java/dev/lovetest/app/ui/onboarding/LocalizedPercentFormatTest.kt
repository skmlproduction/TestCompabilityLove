package dev.lovetest.app.ui.onboarding

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalizedPercentFormatTest {

    @Test
    fun protocolDisclaimer_escapesLiteralPercentInEverySupportedLocale() {
        listOf("values", "values-en").forEach { valuesDir ->
            val xml = projectFile("app/src/main/res/$valuesDir/strings.xml").readText()
            val value = Regex(
                """<string name=\"onboarding_protocol_disclaimer_body\">(.*?)</string>""",
            ).find(xml)?.groupValues?.get(1).orEmpty()
            assertTrue("$valuesDir must escape its literal percent", value.contains("%%"))
        }
    }

    private fun projectFile(relativePath: String): File {
        var dir = File(checkNotNull(System.getProperty("user.dir")))
        while (!File(dir, "settings.gradle.kts").exists()) {
            dir = dir.parentFile ?: error("settings.gradle.kts not found")
        }
        return File(dir, relativePath)
    }
}
