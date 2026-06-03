package dev.lovetest.core.domain

/**
 * Детерминированный «игровой» алгоритм: сумма кодов букв + позиционный микс.
 */
class DefaultLoveScoreCalculator : LoveScoreCalculator {

    override fun pairMetrics(name1: String, name2: String): PairMetrics {
        val base = calculatePercent(name1, name2)
        return PairMetrics(
            connection = clampMetric(base + metricJitter(name1, 6)),
            trust = clampMetric(base + metricJitter(name2, 12)),
            passion = clampMetric(base - metricJitter(name1 + name2, 8)),
        )
    }

    override fun protocolSignals(name1: String, name2: String): ProtocolSignals {
        val pct = calculatePercent(name1, name2)
        val seed = normalize(name1) + "|" + normalize(name2)
        val base = seed.hashCode()
        fun idx(salt: Int): Int = ((base + salt * 31) and 0x7FFFFFFF) % 3
        val verdictBand = when {
            pct < 34 -> 0
            pct < 67 -> 1
            else -> 2
        }
        return ProtocolSignals(
            harmonyIndex = idx(1),
            resonanceIndex = idx(2),
            sparkIndex = idx(3),
            verdictBand = verdictBand,
        )
    }

    override fun calculatePercent(name1: String, name2: String): Int {
        val a = normalize(name1)
        val b = normalize(name2)
        if (a.isEmpty() && b.isEmpty()) return 0
        if (a.isEmpty() || b.isEmpty()) return 17

        var score = 0
        val maxLen = maxOf(a.length, b.length, 1)
        for (i in 0 until maxLen) {
            val ca = a.getOrNull(i)?.code ?: 0
            val cb = b.getOrNull(i)?.code ?: 0
            score += (ca * 3 + cb * 5 + (i + 1) * 7) % 97
        }
        score += (a.length * 11 + b.length * 13) % 53
        score += symmetricOverlap(a, b) * 4
        return (score % 100).coerceIn(1, 99)
    }

    private fun normalize(raw: String): String =
        raw.trim().lowercase().filter { it.isLetter() }

    private fun symmetricOverlap(a: String, b: String): Int {
        if (a.isEmpty() || b.isEmpty()) return 0
        val setB = b.toSet()
        return a.count { it in setB }
    }

}
