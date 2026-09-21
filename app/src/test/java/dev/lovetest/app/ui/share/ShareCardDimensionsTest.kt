package dev.lovetest.app.ui.share

import org.junit.Assert.assertEquals
import org.junit.Test

class ShareCardDimensionsTest {

    @Test
    fun captureUsesAStableThreeXDensity() {
        assertEquals(3f, ShareCardDimensions.DENSITY)
        assertEquals(360 * ShareCardDimensions.DENSITY.toInt(), ShareCardDimensions.WIDTH_PX)
        assertEquals(387 * ShareCardDimensions.DENSITY.toInt(), ShareCardDimensions.HEIGHT_PX)
    }
}
