package dev.lovetest.app.monetization

import android.app.Activity
import android.content.Context
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.screen.CASInterstitial
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback
import com.cleversolutions.ads.AdError
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
 * Preloads and shows CAS.AI mediation interstitial when [BuildConfig.ADS_ENABLED].
 * Consent проверяется через UMP ([AdsConsentManager]) — CAS ConsentFlow и UMP
 * делят общий статус согласия (IAB TCF).
 * Does not fall back to debug UI placeholder in production paths.
 */
class CasInterstitialManager(
    private val context: Context,
    private val consentManager: AdsConsentManager,
) {
    private var interstitial: CASInterstitial? = null

    private val _loadState = MutableStateFlow(InterstitialLoadState.Idle)
    val loadState: StateFlow<InterstitialLoadState> = _loadState.asStateFlow()

    fun preload() {
        if (!BuildConfig.ADS_ENABLED || !consentManager.canRequestAds()) return
        if (_loadState.value == InterstitialLoadState.Loading || interstitial != null) return
        _loadState.value = InterstitialLoadState.Loading
        val ad = CASInterstitial(CasAdsManager.CAS_ID)
        ad.isAutoloadEnabled = false
        ad.contentCallback = object : ScreenAdContentCallback() {
            override fun onAdLoaded(adInfo: AdContentInfo) {
                interstitial = ad
                _loadState.value = InterstitialLoadState.Ready
            }

            override fun onAdFailedToLoad(format: AdFormat, error: AdError) {
                interstitial = null
                _loadState.value = InterstitialLoadState.Failed
            }
        }
        ad.load(context)
    }

    /** Drops a preloaded ad (e.g. user bought Premium). */
    fun discard() {
        interstitial = null
        _loadState.value = InterstitialLoadState.Idle
    }

    /**
     * @return true if a loaded ad was shown; false if not ready / ads disabled.
     */
    fun show(activity: Activity, onFinished: () -> Unit): Boolean {
        if (!BuildConfig.ADS_ENABLED || !consentManager.canRequestAds()) return false
        val ad = interstitial ?: return false
        interstitial = null
        _loadState.value = InterstitialLoadState.Idle
        ad.contentCallback = object : ScreenAdContentCallback() {
            override fun onAdDismissed(adInfo: AdContentInfo) {
                preload()
                onFinished()
            }

            override fun onAdFailedToShow(format: AdFormat, error: AdError) {
                preload()
                onFinished()
            }
        }
        ad.show(activity)
        return true
    }
}
