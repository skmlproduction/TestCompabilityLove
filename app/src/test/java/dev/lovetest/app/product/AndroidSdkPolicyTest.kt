package dev.lovetest.app.product

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidSdkPolicyTest {
    @Test
    fun allAndroidModulesCompileWithApi36AndAppTargetsApi36() {
        val root = projectRoot()
        val appBuild = File(root, "app/build.gradle.kts").readText()
        val uiBuild = File(root, "core/ui/build.gradle.kts").readText()

        assertEquals(1, Regex("compileSdk\\s*=\\s*36").findAll(appBuild).count())
        assertEquals(1, Regex("targetSdk\\s*=\\s*36").findAll(appBuild).count())
        assertEquals(1, Regex("compileSdk\\s*=\\s*36").findAll(uiBuild).count())
    }

    private fun projectRoot(): File {
        var directory = File(checkNotNull(System.getProperty("user.dir")))
        while (!File(directory, "settings.gradle.kts").isFile) {
            directory = directory.parentFile ?: error("settings.gradle.kts not found")
        }
        return directory
    }
}
