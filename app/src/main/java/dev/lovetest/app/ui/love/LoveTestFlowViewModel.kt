package dev.lovetest.app.ui.love

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.core.domain.LoveScoreCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

data class CalculatingUiState(
    val name1: String = "",
    val name2: String = "",
    val progress: Float = 0f,
    val activeStep: Int = 0,
)

class LoveTestFlowViewModel(
    private val calculator: LoveScoreCalculator,
    private val preferences: AppPreferences,
) : ViewModel() {

    private val calculationRunning = AtomicBoolean(false)

    private val _calculating = MutableStateFlow(false)
    val calculating: StateFlow<Boolean> = _calculating.asStateFlow()

    private val _uiState = MutableStateFlow(CalculatingUiState())
    val uiState: StateFlow<CalculatingUiState> = _uiState.asStateFlow()

    private suspend fun animateCalculating(name1: String, name2: String) {
        val n1 = name1.trim()
        val n2 = name2.trim()
        _uiState.value = CalculatingUiState(n1, n2)
        val frames = 36
        val frameDelay = 1_800L / frames
        for (i in 1..frames) {
            delay(frameDelay)
            val progress = i / frames.toFloat()
            val step = when {
                progress < 0.34f -> 0
                progress < 0.67f -> 1
                else -> 2
            }
            _uiState.value = CalculatingUiState(n1, n2, progress, step)
        }
    }

    fun runPreviewCalculating(name1: String = "Anna", name2: String = "Max") {
        if (!BuildConfig.DEBUG) return
        val n1 = name1.trim()
        val n2 = name2.trim()
        _calculating.value = true
        _uiState.value = CalculatingUiState(
            name1 = n1,
            name2 = n2,
            progress = 0.55f,
            activeStep = 1,
        )
    }

    /**
     * @return false если расчёт уже идёт (повторный вызов игнорируется).
     */
    fun runLoveTest(name1: String, name2: String, onDone: () -> Unit): Boolean =
        launchCalculation {
            animateCalculating(name1, name2)
            val n1 = name1.trim()
            val n2 = name2.trim()
            preferences.saveLastNames(n1, n2)
            val percent = calculator.calculatePercent(n1, n2)
            LoveTestSession.storeLoveResult(n1, n2, percent)
            persistSession()
            onDone()
        }

    fun runPairTest(name1: String, name2: String, onDone: () -> Unit): Boolean =
        launchCalculation {
            animateCalculating(name1, name2)
            val n1 = name1.trim()
            val n2 = name2.trim()
            preferences.saveLastNames(n1, n2)
            val percent = calculator.calculatePercent(n1, n2)
            val metrics = calculator.pairMetrics(n1, n2)
            LoveTestSession.storePairResult(n1, n2, percent, metrics)
            persistSession()
            onDone()
        }

    fun runSingleNameTest(name: String, onDone: () -> Unit): Boolean =
        launchCalculation {
            val n = name.trim()
            animateCalculating(n, n)
            preferences.saveLastNames(n, n)
            val percent = calculator.calculatePercent(n, n)
            LoveTestSession.storeLoveResult(n, n, percent)
            persistSession()
            onDone()
        }

    fun runWheelSpin(prize: String, segmentIndex: Int, onDone: () -> Unit): Boolean =
        launchCalculation {
            val label = prize.trim()
            animateCalculating(label, "")
            LoveTestSession.storeWheelResult(segmentIndex, label)
            persistSession()
            onDone()
        }

    fun runProtocolTest(name1: String, name2: String, onDone: () -> Unit): Boolean =
        launchCalculation {
            animateCalculating(name1, name2)
            val n1 = name1.trim()
            val n2 = name2.trim()
            preferences.saveLastNames(n1, n2)
            val percent = calculator.calculatePercent(n1, n2)
            val signals = calculator.protocolSignals(n1, n2)
            LoveTestSession.storeProtocolResult(n1, n2, percent, signals)
            persistSession()
            onDone()
        }

    private suspend fun persistSession() {
        preferences.saveSessionSnapshot(LoveTestSession.encodeSnapshot())
    }

    private fun launchCalculation(block: suspend () -> Unit): Boolean {
        if (!calculationRunning.compareAndSet(false, true)) return false
        viewModelScope.launch {
            try {
                _calculating.value = true
                block()
            } finally {
                _calculating.value = false
                calculationRunning.set(false)
            }
        }
        return true
    }
}
