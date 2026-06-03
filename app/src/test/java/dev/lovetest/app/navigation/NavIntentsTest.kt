package dev.lovetest.app.navigation

import org.junit.Assert.assertTrue
import org.junit.Test

class NavIntentsTest {

    @Test
    fun debugExtras_useReverseDnsPrefix() {
        assertTrue(NavIntents.EXTRA_DEBUG_START_ROUTE.startsWith("lovetest.intent."))
        assertTrue(NavIntents.EXTRA_DEBUG_UI_PREVIEW.startsWith("lovetest.intent."))
    }
}
