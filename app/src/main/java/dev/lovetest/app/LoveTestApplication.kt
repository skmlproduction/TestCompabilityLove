package dev.lovetest.app

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.lovetest.app.di.appModule
import dev.lovetest.app.monetization.AdMobInterstitialManager
import dev.lovetest.app.monetization.AdsConsentManager
import dev.lovetest.app.monetization.bootstrapAdsIfAllowed
import dev.lovetest.app.monetization.PremiumBillingManager
import dev.lovetest.app.monetization.syncPremiumOnStartup
import dev.lovetest.app.prefs.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class LoveTestApplication : Application(), KoinComponent {

    private val billingManager: PremiumBillingManager by inject()
    private val adMobManager: AdMobInterstitialManager by inject()
    private val adsConsentManager: AdsConsentManager by inject()
    private val preferences: AppPreferences by inject()
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val billingLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            billingManager.destroy()
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LoveTestApplication)
            modules(appModule)
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(billingLifecycleObserver)
        appScope.launch {
            syncPremiumOnStartup(preferences, billingManager)
            bootstrapAdsIfAllowed(this@LoveTestApplication, preferences, adsConsentManager, adMobManager)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onTerminate() {
        billingManager.destroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(billingLifecycleObserver)
        super.onTerminate()
    }
}
