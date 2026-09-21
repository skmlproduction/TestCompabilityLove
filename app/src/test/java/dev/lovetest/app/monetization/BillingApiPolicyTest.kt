package dev.lovetest.app.monetization

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingApiPolicyTest {
    @Test
    fun billing9CatalogResponseIsHandledThroughQueryResult() {
        val catalog = projectFile("gradle/libs.versions.toml").readText()
        val manager = projectFile(
            "app/src/main/java/dev/lovetest/app/monetization/PremiumBillingManager.kt",
        ).readText()

        assertTrue("Play Billing must stay on the audited v9 line", "billing = \"9.1.0\"" in catalog)
        assertTrue(
            "Billing v9 catalog callback must read QueryProductDetailsResult.productDetailsList",
            "response.productDetailsList.firstOrNull()" in manager,
        )
    }

    private fun projectFile(relativePath: String): File {
        var directory = File(checkNotNull(System.getProperty("user.dir")))
        while (!File(directory, "settings.gradle.kts").isFile) {
            directory = directory.parentFile ?: error("settings.gradle.kts not found")
        }
        return File(directory, relativePath)
    }
}
