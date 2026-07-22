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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class InterstitialLoadState {
    Idle,
    Loading,
    Ready,
    Failed,
}

/**
 * Preloads and shows AdMob interstitial when [BuildConfig.ADS_ENABLED].
 * Does not fall back to debug UI placeholder in production paths.
 */
class AdMobInterstitialManager(
    private val context: Context,
    private val consentManager: AdsConsentManager,
) {
    private var interstitialAd: InterstitialAd? = null

    private val _loadState = MutableStateFlow(InterstitialLoadState.Idle)
    val loadState: StateFlow<InterstitialLoadState> = _loadState.asStateFlow()

    fun preload() {
        if (!BuildConfig.ADS_ENABLED || !consentManager.canRequestAds()) return
        if (_loadState.value == InterstitialLoadState.Loading || interstitialAd != null) return
        _loadState.value = InterstitialLoadState.Loading
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    _loadState.value = InterstitialLoadState.Ready
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    _loadState.value = InterstitialLoadState.Failed
                }
            },
        )
    }

    /** Drops a preloaded ad (e.g. user bought Premium). */
    fun discard() {
        interstitialAd = null
        _loadState.value = InterstitialLoadState.Idle
    }

    /**
     * @return true if a loaded ad was shown; false if not ready / ads disabled.
     */
    fun show(activity: Activity, onFinished: () -> Unit): Boolean {
        if (!BuildConfig.ADS_ENABLED || !consentManager.canRequestAds()) return false
        val ad = interstitialAd ?: return false
        interstitialAd = null
        _loadState.value = InterstitialLoadState.Idle
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
