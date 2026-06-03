package dev.lovetest.app.monetization

import android.content.Context
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.prefs.AppPreferences
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assume.assumeTrue
import org.junit.Test

class AdsBootstrapTest {

    @Test
    fun bootstrapAdsIfAllowed_noOpWhenAdsDisabled() = runTest {
        assumeTrue(!BuildConfig.ADS_ENABLED)

        val context = mockk<Context>(relaxed = true)
        val prefs = mockk<AppPreferences>()
        val consent = mockk<AdsConsentManager>()
        val adMob = mockk<AdMobInterstitialManager>(relaxed = true)

        bootstrapAdsIfAllowed(context, prefs, consent, adMob)

        verify(exactly = 0) { adMob.preload() }
    }

    @Test
    fun bootstrapAdsIfAllowed_preloadsWhenAllowed() = runTest {
        assumeTrue(BuildConfig.ADS_ENABLED)

        val context = mockk<Context>(relaxed = true)
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns false
        coEvery { prefs.isConsentCompleted() } returns true
        val consent = mockk<AdsConsentManager>()
        every { consent.canRequestAds() } returns true
        val adMob = mockk<AdMobInterstitialManager>(relaxed = true)

        bootstrapAdsIfAllowed(context, prefs, consent, adMob)

        verify { adMob.preload() }
    }

    @Test
    fun bootstrapAdsIfAllowed_skipsWhenPremium() = runTest {
        assumeTrue(BuildConfig.ADS_ENABLED)

        val context = mockk<Context>(relaxed = true)
        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns true
        val consent = mockk<AdsConsentManager>()
        val adMob = mockk<AdMobInterstitialManager>(relaxed = true)

        bootstrapAdsIfAllowed(context, prefs, consent, adMob)

        verify(exactly = 0) { adMob.preload() }
    }
}
