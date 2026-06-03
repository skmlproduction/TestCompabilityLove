package dev.lovetest.app.monetization

import android.content.Context
import com.google.android.gms.ads.MobileAds
import dev.lovetest.app.BuildConfig

object AdMobInitializer {

    @Volatile
    private var initialized = false

    fun initializeIfNeeded(context: Context) {
        if (!BuildConfig.ADS_ENABLED || initialized) return
        synchronized(this) {
            if (initialized) return
            MobileAds.initialize(context.applicationContext) {}
            initialized = true
        }
    }
}
