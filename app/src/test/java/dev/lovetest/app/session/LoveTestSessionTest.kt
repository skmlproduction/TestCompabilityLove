package dev.lovetest.app.session

import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoveTestSessionTest {

    @After
    fun tearDown() {
        LoveTestSession.clear()
    }

    @Test
    fun emptySession_hasNoResults() {
        assertFalse(LoveTestSession.hasLoveResult())
        assertFalse(LoveTestSession.hasPairResult())
        assertFalse(LoveTestSession.hasWheelResult())
    }

    @Test
    fun storeLoveResult_isDetected() {
        LoveTestSession.storeLoveResult("Anna", "Max", 42)
        assertTrue(LoveTestSession.hasLoveResult())
        assertFalse(LoveTestSession.hasPairResult())
        assertFalse(LoveTestSession.hasWheelResult())
    }

    @Test
    fun storePairResult_isDetected() {
        val calc = DefaultLoveScoreCalculator()
        LoveTestSession.storePairResult("A", "B", 55, calc.pairMetrics("A", "B"))
        assertTrue(LoveTestSession.hasPairResult())
        assertTrue(LoveTestSession.hasLoveResult())
    }

    @Test
    fun storeWheelResult_isDetected() {
        LoveTestSession.storeWheelResult(2, "Свидание")
        assertTrue(LoveTestSession.hasWheelResult())
        assertFalse(LoveTestSession.hasLoveResult())
    }

    @Test
    fun storeProtocolResult_isDetected() {
        val calc = DefaultLoveScoreCalculator()
        LoveTestSession.storeProtocolResult("A", "B", 61, calc.protocolSignals("A", "B"))
        assertTrue(LoveTestSession.hasProtocolResult())
        assertTrue(LoveTestSession.hasLoveResult())
        assertFalse(LoveTestSession.hasPairResult())
    }
}
