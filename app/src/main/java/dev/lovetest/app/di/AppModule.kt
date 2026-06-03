package dev.lovetest.app.di

import dev.lovetest.app.monetization.AdMobInterstitialManager
import dev.lovetest.app.monetization.AdsConsentManager
import dev.lovetest.app.monetization.PremiumBillingManager
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.ui.hub.HubViewModel
import dev.lovetest.app.ui.love.LoveTestFlowViewModel
import dev.lovetest.app.ui.splash.SplashViewModel
import dev.lovetest.core.domain.DefaultLoveScoreCalculator
import dev.lovetest.core.domain.LoveScoreCalculator
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<LoveScoreCalculator> { DefaultLoveScoreCalculator() }
    single { AppPreferences(androidContext()) }
    single { PremiumBillingManager(androidContext()) }
    single { AdsConsentManager(androidContext()) }
    single { AdMobInterstitialManager(androidContext(), get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { HubViewModel() }
    viewModel { LoveTestFlowViewModel(get(), get()) }
}
