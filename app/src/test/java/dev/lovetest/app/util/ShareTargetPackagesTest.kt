package dev.lovetest.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ShareTargetPackagesTest {

    @Test
    fun shareTargetPackages_useExpectedIds() {
        assertEquals("org.telegram.messenger", ShareTargetPackages.TELEGRAM)
        assertEquals("com.whatsapp", ShareTargetPackages.WHATSAPP)
    }
}
