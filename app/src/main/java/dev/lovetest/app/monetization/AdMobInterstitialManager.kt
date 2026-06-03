package dev.lovetest.app.monetization

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dev.lovetest.app.BuildConfig

/**
 * Preloads and shows AdMob interstitial when [BuildConfig.ADS_ENABLED].
 * Falls back to [AdInterstitialPlaceholder] when load/show fails.
 */
class AdMobInterstitialManager(
    private val context: Context,
    private val consentManager: AdsConsentManager,
) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun preload() {
        if (!BuildConfig.ADS_ENABLED || !consentManager.canRequestAds()) return
        if (isLoading || interstitialAd != null) return
        isLoading = true
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            },
        )
    }

    /** Drops a preloaded ad (e.g. user bought Premium). */
    fun discard() {
        interstitialAd = null
        isLoading = false
    }

    /**
     * @return true if a loaded ad was shown; false → caller should show UI placeholder.
     */
    fun show(activity: Activity, onFinished: () -> Unit): Boolean {
        if (!BuildConfig.ADS_ENABLED || !consentManager.canRequestAds()) return false
        val ad = interstitialAd ?: return false
        interstitialAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                preload()
                onFinished()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                preload()
                onFinished()
            }
        }
        ad.show(activity)
        return true
    }
}
