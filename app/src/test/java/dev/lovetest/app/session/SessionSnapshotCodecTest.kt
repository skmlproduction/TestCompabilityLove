package dev.lovetest.app.session

import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionSnapshotCodecTest {

    private val calc = DefaultLoveScoreCalculator()

    @Test
    fun encodeDecode_love_roundTrip() {
        val snapshot = SessionSnapshot(SessionKind.LOVE, "Anna", "Max", 42)
        val decoded = SessionSnapshotCodec.decode(SessionSnapshotCodec.encode(snapshot))
        assertEquals(snapshot, decoded)
    }

    @Test
    fun encodeDecode_pair_roundTrip() {
        val metrics = calc.pairMetrics("A", "B")
        val snapshot = SessionSnapshot(SessionKind.PAIR, "A", "B", 55, pairMetrics = metrics)
        val decoded = SessionSnapshotCodec.decode(SessionSnapshotCodec.encode(snapshot))
        assertEquals(snapshot, decoded)
    }

    @Test
    fun encodeDecode_protocol_roundTrip() {
        val signals = calc.protocolSignals("X", "Y")
        val snapshot = SessionSnapshot(SessionKind.PROTOCOL, "X", "Y", 61, protocolSignals = signals)
        val decoded = SessionSnapshotCodec.decode(SessionSnapshotCodec.encode(snapshot))
        assertEquals(snapshot, decoded)
    }

    @Test
    fun encodeDecode_wheel_roundTrip() {
        val snapshot = SessionSnapshot(
            kind = SessionKind.WHEEL,
            name1 = "Date night",
            name2 = "",
            percent = 0,
            wheelSegmentIndex = 3,
        )
        val decoded = SessionSnapshotCodec.decode(SessionSnapshotCodec.encode(snapshot))
        assertEquals(snapshot, decoded)
    }

    @Test
    fun decode_invalid_returnsNull() {
        assertNull(SessionSnapshotCodec.decode(""))
        assertNull(SessionSnapshotCodec.decode("v2;love;a;b;1"))
    }

    @Test
    fun loveTestSession_snapshot_roundTrip() {
        LoveTestSession.storeLoveResult("Anna", "Max", 77)
        val encoded = LoveTestSession.encodeSnapshot()
        assertTrue(encoded != null)
        LoveTestSession.clear()
        assertTrue(!LoveTestSession.hasLoveResult())
        LoveTestSession.restoreFromEncoded(encoded)
        assertTrue(LoveTestSession.hasLoveResult())
        assertEquals("Anna", LoveTestSession.name1)
        assertEquals(77, LoveTestSession.percent)
        LoveTestSession.clear()
    }
}
