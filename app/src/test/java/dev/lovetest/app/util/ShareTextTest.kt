package dev.lovetest.app.util

import org.junit.Assert.assertTrue
import org.junit.Test

class ShareTextTest {

    @Test
    fun formatLoveShareText_containsNamesPercentAndFooter() {
        val text = formatLoveShareText(
            appName = "Love Tester",
            footer = "For entertainment only",
            percent = 87,
            name1 = "Anna",
            name2 = "Max",
        )
        assertTrue(text.contains("87%"))
        assertTrue(text.contains("Anna"))
        assertTrue(text.contains("Max"))
        assertTrue(text.contains("For entertainment only"))
    }

    @Test
    fun formatProtocolShareText_containsNamesPercentAndFooter() {
        val text = formatProtocolShareText(
            appName = "Love Tester",
            protocolTitle = "Love protocol",
            footer = "For entertainment only",
            percent = 72,
            name1 = "Anna",
            name2 = "Max",
        )
        assertTrue(text.contains("Love protocol"))
        assertTrue(text.contains("72%"))
        assertTrue(text.contains("Anna"))
        assertTrue(text.contains("For entertainment only"))
    }

    @Test
    fun formatWheelShareText_containsPrizeAndLocalizedParts() {
        val text = formatWheelShareText(
            appName = "Love Tester",
            wheelKicker = "Fantasy wheel",
            footer = "For entertainment only",
            prize = "Свидание",
        )
        assertTrue(text.contains("Свидание"))
        assertTrue(text.contains("Fantasy wheel"))
        assertTrue(text.contains("For entertainment only"))
    }
}
