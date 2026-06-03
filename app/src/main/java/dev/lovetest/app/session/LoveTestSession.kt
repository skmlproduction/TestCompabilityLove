package dev.lovetest.app.session

import dev.lovetest.core.domain.PairMetrics
import dev.lovetest.core.domain.ProtocolSignals

/**
 * In-memory сессия текущего расчёта; snapshot персистится в DataStore ([dev.lovetest.app.prefs.AppPreferences]).
 */
object LoveTestSession {
    var name1: String = ""
    var name2: String = ""
    var percent: Int = 0
    var pairMetrics: PairMetrics? = null
    var wheelSegmentIndex: Int = -1
    var protocolSignals: ProtocolSignals? = null

    fun storePairResult(n1: String, n2: String, pct: Int, metrics: PairMetrics) {
        name1 = n1
        name2 = n2
        percent = pct
        pairMetrics = metrics
        wheelSegmentIndex = -1
        protocolSignals = null
    }

    fun storeProtocolResult(n1: String, n2: String, pct: Int, signals: ProtocolSignals) {
        name1 = n1
        name2 = n2
        percent = pct
        protocolSignals = signals
        pairMetrics = null
        wheelSegmentIndex = -1
    }

    fun storeLoveResult(n1: String, n2: String, pct: Int) {
        name1 = n1
        name2 = n2
        percent = pct
        pairMetrics = null
        wheelSegmentIndex = -1
        protocolSignals = null
    }

    fun storeWheelResult(segmentIndex: Int, prize: String) {
        wheelSegmentIndex = segmentIndex.coerceIn(0, 7)
        name1 = prize
        name2 = ""
        percent = 0
        pairMetrics = null
        protocolSignals = null
    }

    fun clear() {
        name1 = ""
        name2 = ""
        percent = 0
        pairMetrics = null
        wheelSegmentIndex = -1
        protocolSignals = null
    }

    fun hasProtocolResult(): Boolean = hasLoveResult() && protocolSignals != null

    /** Есть данные love/calculator/letters/zodiac/victory result. */
    fun hasLoveResult(): Boolean =
        percent in 1..99 && (name1.isNotBlank() || name2.isNotBlank())

    fun hasPairResult(): Boolean = hasLoveResult() && pairMetrics != null

    fun hasWheelResult(): Boolean =
        wheelSegmentIndex >= 0 && name1.isNotBlank()

    fun toSnapshot(): SessionSnapshot? = when {
        hasWheelResult() -> SessionSnapshot(
            kind = SessionKind.WHEEL,
            name1 = name1,
            name2 = name2,
            percent = percent,
            wheelSegmentIndex = wheelSegmentIndex,
        )
        hasProtocolResult() -> SessionSnapshot(
            kind = SessionKind.PROTOCOL,
            name1 = name1,
            name2 = name2,
            percent = percent,
            protocolSignals = protocolSignals,
        )
        hasPairResult() -> SessionSnapshot(
            kind = SessionKind.PAIR,
            name1 = name1,
            name2 = name2,
            percent = percent,
            pairMetrics = pairMetrics,
        )
        hasLoveResult() -> SessionSnapshot(
            kind = SessionKind.LOVE,
            name1 = name1,
            name2 = name2,
            percent = percent,
        )
        else -> null
    }

    fun encodeSnapshot(): String? = toSnapshot()?.let(SessionSnapshotCodec::encode)

    fun applySnapshot(snapshot: SessionSnapshot) {
        when (snapshot.kind) {
            SessionKind.LOVE -> storeLoveResult(snapshot.name1, snapshot.name2, snapshot.percent)
            SessionKind.PAIR -> {
                val metrics = snapshot.pairMetrics ?: return
                storePairResult(snapshot.name1, snapshot.name2, snapshot.percent, metrics)
            }
            SessionKind.PROTOCOL -> {
                val signals = snapshot.protocolSignals ?: return
                storeProtocolResult(snapshot.name1, snapshot.name2, snapshot.percent, signals)
            }
            SessionKind.WHEEL -> storeWheelResult(snapshot.wheelSegmentIndex, snapshot.name1)
        }
    }

    fun restoreFromEncoded(raw: String?) {
        clear()
        val snapshot = raw?.let(SessionSnapshotCodec::decode) ?: return
        applySnapshot(snapshot)
    }
}
