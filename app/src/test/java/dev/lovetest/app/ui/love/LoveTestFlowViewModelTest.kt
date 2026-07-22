package dev.lovetest.app.ui.love

import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoveTestFlowViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        LoveTestSession.clear()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        LoveTestSession.clear()
    }

    @Test
    fun runLoveTest_secondCallWhileRunning_returnsFalse() = runTest(testDispatcher) {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.saveLastNames(any(), any()) } returns Unit
        coEvery { prefs.saveSessionSnapshot(any()) } returns Unit
        val viewModel = LoveTestFlowViewModel(DefaultLoveScoreCalculator(), prefs)

        var doneCount = 0
        assertTrue(viewModel.runLoveTest("Anna", "Max") { doneCount++ })
        assertFalse(viewModel.runLoveTest("X", "Y") { doneCount++ })

        advanceUntilIdle()
        assertEquals(1, doneCount)
        assertTrue(LoveTestSession.hasLoveResult())
    }

    @Test
    fun runLoveTest_invalidDigits_doesNotStartOrPersist() = runTest(testDispatcher) {
        val prefs = mockk<AppPreferences>(relaxed = true)
        val viewModel = LoveTestFlowViewModel(DefaultLoveScoreCalculator(), prefs)
        var done = false

        assertFalse(viewModel.runLoveTest("123", "456") { done = true })
        advanceUntilIdle()

        assertFalse(done)
        assertFalse(LoveTestSession.hasLoveResult())
        assertFalse(viewModel.calculating.value)
    }

    @Test
    fun runPairTest_blankNames_doesNotStart() = runTest(testDispatcher) {
        val prefs = mockk<AppPreferences>(relaxed = true)
        val viewModel = LoveTestFlowViewModel(DefaultLoveScoreCalculator(), prefs)
        var done = false

        assertFalse(viewModel.runPairTest("   ", "Anna") { done = true })
        advanceUntilIdle()

        assertFalse(done)
        assertFalse(LoveTestSession.hasPairResult())
    }

    @Test
    fun runSingleNameTest_emojiOnly_doesNotStart() = runTest(testDispatcher) {
        val prefs = mockk<AppPreferences>(relaxed = true)
        val viewModel = LoveTestFlowViewModel(DefaultLoveScoreCalculator(), prefs)
        var done = false

        assertFalse(viewModel.runSingleNameTest("❤️🔥") { done = true })
        advanceUntilIdle()

        assertFalse(done)
        assertFalse(LoveTestSession.hasLoveResult())
    }

    @Test
    fun runProtocolTest_validNames_persistsResult() = runTest(testDispatcher) {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.saveLastNames(any(), any()) } returns Unit
        coEvery { prefs.saveSessionSnapshot(any()) } returns Unit
        val viewModel = LoveTestFlowViewModel(DefaultLoveScoreCalculator(), prefs)
        var done = false

        assertTrue(viewModel.runProtocolTest("Anna", "Max") { done = true })
        advanceUntilIdle()

        assertTrue(done)
        assertTrue(LoveTestSession.hasProtocolResult())
    }
}
