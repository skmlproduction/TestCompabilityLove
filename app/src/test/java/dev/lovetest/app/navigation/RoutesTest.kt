package dev.lovetest.app.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutesTest {

    @Test
    fun allDestinations_coversCatalogRoutes() {
        val routes = Routes.allDestinations()
        assertEquals(25, routes.size)
        assertTrue(Routes.Splash in routes)
        assertTrue(Routes.Hub in routes)
        assertTrue(Routes.LoveTestResult in routes)
        assertTrue(Routes.ProtocolInput in routes)
        assertTrue(Routes.ProtocolResult in routes)
    }

    @Test
    fun loveTestPaths_useSlashNotation() {
        assertTrue(Routes.LoveTestInput.contains("/"))
        assertEquals("love_test/input", Routes.LoveTestInput)
    }
}
