package dev.lovetest.app.monetization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingProductIdsTest {

    @Test
    fun parseBillingProductIds_empty_returnsEmpty() {
        assertTrue(parseBillingProductIds("").isEmpty())
        assertTrue(parseBillingProductIds("  ,  , ").isEmpty())
    }

    @Test
    fun parseBillingProductIds_single_trimsWhitespace() {
        assertEquals(listOf("remove_ads"), parseBillingProductIds(" remove_ads "))
    }

    @Test
    fun parseBillingProductIds_multiple_splitsAndFilters() {
        assertEquals(
            listOf("remove_ads", "premium_pack"),
            parseBillingProductIds("remove_ads, premium_pack"),
        )
    }
}
