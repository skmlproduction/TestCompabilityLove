package dev.lovetest.app.ui.splash

import dev.lovetest.app.navigation.Routes
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.session.LoveTestSession
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

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
    fun init_restoresSessionSnapshotBeforeNavigate() = runTest(testDispatcher) {
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.loadSessionSnapshot() } returns "v1;love;Anna;Max;42"
        coEvery { prefs.isOnboardingCompleted() } returns true
        coEvery { prefs.isConsentCompleted() } returns true

        val viewModel = SplashViewModel(prefs)
        advanceUntilIdle()
        advanceTimeBy(2_000)

        assertTrue(LoveTestSession.hasLoveResult())
        assertEquals("Anna", LoveTestSession.name1)
        assertEquals(42, LoveTestSession.percent)
        val dest = viewModel.destination.value
        assertTrue(dest is SplashDestination.Navigate)
        assertEquals(Routes.Hub, (dest as SplashDestination.Navigate).route)
    }
}
