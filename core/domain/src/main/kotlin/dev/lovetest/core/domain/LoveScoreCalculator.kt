package dev.lovetest.core.domain

/**
 * Локальный расчёт «процента совместимости» по именам (MVP, без сети).
 * Реализация детерминирована: одни и те же имена дают тот же результат.
 */
interface LoveScoreCalculator {
    fun calculatePercent(name1: String, name2: String): Int

    fun isHighScore(percent: Int, threshold: Int = DEFAULT_HIGH_THRESHOLD): Boolean =
        percent >= threshold

    fun pairMetrics(name1: String, name2: String): PairMetrics

    fun protocolSignals(name1: String, name2: String): ProtocolSignals

    companion object {
        const val DEFAULT_HIGH_THRESHOLD = 50
    }
}

data class PairMetrics(
    val connection: Int,
    val trust: Int,
    val passion: Int,
)

/** Индексы 0..2 для строковых вариантов сигналов; verdictBand: 0=низкий, 1=средний, 2=высокий. */
data class ProtocolSignals(
    val harmonyIndex: Int,
    val resonanceIndex: Int,
    val sparkIndex: Int,
    val verdictBand: Int,
)

internal fun clampMetric(value: Int): Int = value.coerceIn(5, 99)

internal fun metricJitter(seed: String, salt: Int): Int =
    (seed.sumOf { it.code } + salt * 17) % 23 - 11
