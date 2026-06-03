package dev.lovetest.core.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultLoveScoreCalculatorTest {

    private val calculator = DefaultLoveScoreCalculator()

    @Test
    fun sameNames_samePercent() {
        val first = calculator.calculatePercent("Anna", "Max")
        val second = calculator.calculatePercent("Anna", "Max")
        assertEquals(first, second)
    }

    @Test
    fun percent_inValidRange() {
        val p = calculator.calculatePercent("Sophia", "Dmitry")
        assertTrue(p in 1..99)
    }

    @Test
    fun pairMetrics_withinRange() {
        val m = calculator.pairMetrics("Anna", "Max")
        assertTrue(m.connection in 5..99)
        assertTrue(m.trust in 5..99)
        assertTrue(m.passion in 5..99)
    }

    @Test
    fun emptyBoth_returnsZero() {
        assertEquals(0, calculator.calculatePercent("", ""))
    }

    @Test
    fun protocolSignals_indicesInRange() {
        val s = calculator.protocolSignals("Anna", "Max")
        assertTrue(s.harmonyIndex in 0..2)
        assertTrue(s.resonanceIndex in 0..2)
        assertTrue(s.sparkIndex in 0..2)
        assertTrue(s.verdictBand in 0..2)
    }
}
