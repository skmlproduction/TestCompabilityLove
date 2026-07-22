package dev.lovetest.app.navigation

import dev.lovetest.app.monetization.AdMobInterstitialManager
import dev.lovetest.app.monetization.AdsConsentManager
import dev.lovetest.app.monetization.InterstitialLoadState
import dev.lovetest.app.monetization.PremiumBillingManager
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.ui.hub.HubViewModel
import dev.lovetest.app.ui.love.LoveTestFlowViewModel
import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import dev.lovetest.core.domain.LoveScoreCalculator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun navHostTestModule(
    preferences: AppPreferences = mockk(relaxed = true) {
        coEvery { getLastNames() } returns Pair("", "")
        coEvery { saveLastNames(any(), any()) } returns Unit
        coEvery { saveSessionSnapshot(any()) } returns Unit
        every { isPremiumFlow } returns flowOf(false)
    },
): Module {
    val billing = mockk<PremiumBillingManager>(relaxed = true)
    val consent = mockk<AdsConsentManager>(relaxed = true) {
        every { canRequestAds() } returns false
    }
    val ads = mockk<AdMobInterstitialManager>(relaxed = true) {
        every { loadState } returns MutableStateFlow(InterstitialLoadState.Idle).asStateFlow()
    }
    return module {
        single<LoveScoreCalculator> { DefaultLoveScoreCalculator() }
        single { preferences }
        single { billing }
        single { consent }
        single { ads }
        viewModel { HubViewModel() }
        viewModel { LoveTestFlowViewModel(get(), get()) }
    }
}
