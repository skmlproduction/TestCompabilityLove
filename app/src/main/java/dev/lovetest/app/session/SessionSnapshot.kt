package dev.lovetest.app.session

import dev.lovetest.core.domain.PairMetrics
import dev.lovetest.core.domain.ProtocolSignals

enum class SessionKind {
    LOVE,
    PAIR,
    PROTOCOL,
    WHEEL,
}

data class SessionSnapshot(
    val kind: SessionKind,
    val name1: String,
    val name2: String,
    val percent: Int,
    val wheelSegmentIndex: Int = -1,
    val pairMetrics: PairMetrics? = null,
    val protocolSignals: ProtocolSignals? = null,
)

internal object SessionSnapshotCodec {
    private const val VERSION = "v1"

    fun encode(snapshot: SessionSnapshot): String = buildString {
        append(VERSION)
        append(';')
        append(snapshot.kind.name.lowercase())
        append(';')
        append(snapshot.name1.replace(';', ' '))
        append(';')
        append(snapshot.name2.replace(';', ' '))
        append(';')
        append(snapshot.percent)
        when (snapshot.kind) {
            SessionKind.LOVE -> Unit
            SessionKind.PAIR -> {
                val m = snapshot.pairMetrics ?: return@buildString
                append(';')
                append(m.connection)
                append(';')
                append(m.trust)
                append(';')
                append(m.passion)
            }
            SessionKind.PROTOCOL -> {
                val s = snapshot.protocolSignals ?: return@buildString
                append(';')
                append(s.harmonyIndex)
                append(';')
                append(s.resonanceIndex)
                append(';')
                append(s.sparkIndex)
                append(';')
                append(s.verdictBand)
            }
            SessionKind.WHEEL -> {
                append(';')
                append(snapshot.wheelSegmentIndex)
            }
        }
    }

    fun decode(raw: String): SessionSnapshot? {
        val parts = raw.split(';')
        if (parts.size < 5 || parts[0] != VERSION) return null
        val kind = runCatching { SessionKind.valueOf(parts[1].uppercase()) }.getOrNull() ?: return null
        val name1 = parts[2]
        val name2 = parts[3]
        val percent = parts[4].toIntOrNull() ?: return null
        return when (kind) {
            SessionKind.LOVE -> SessionSnapshot(kind, name1, name2, percent)
            SessionKind.PAIR -> {
                if (parts.size < 8) return null
                val c = parts[5].toIntOrNull() ?: return null
                val t = parts[6].toIntOrNull() ?: return null
                val p = parts[7].toIntOrNull() ?: return null
                SessionSnapshot(
                    kind = kind,
                    name1 = name1,
                    name2 = name2,
                    percent = percent,
                    pairMetrics = PairMetrics(c, t, p),
                )
            }
            SessionKind.PROTOCOL -> {
                if (parts.size < 9) return null
                val h = parts[5].toIntOrNull() ?: return null
                val r = parts[6].toIntOrNull() ?: return null
                val sp = parts[7].toIntOrNull() ?: return null
                val v = parts[8].toIntOrNull() ?: return null
                SessionSnapshot(
                    kind = kind,
                    name1 = name1,
                    name2 = name2,
                    percent = percent,
                    protocolSignals = ProtocolSignals(h, r, sp, v),
                )
            }
            SessionKind.WHEEL -> {
                if (parts.size < 6) return null
                val seg = parts[5].toIntOrNull() ?: return null
                SessionSnapshot(
                    kind = kind,
                    name1 = name1,
                    name2 = name2,
                    percent = percent,
                    wheelSegmentIndex = seg,
                )
            }
        }
    }
}
