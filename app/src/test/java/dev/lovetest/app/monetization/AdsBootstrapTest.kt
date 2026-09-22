package dev.lovetest.app.monetization

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
    fun bootstrapAdsIfAllowed_preloadsWhenAllowed() = runTest {
        assumeTrue(BuildConfig.ADS_ENABLED)

        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns false
        coEvery { prefs.isConsentCompleted() } returns true
        val consent = mockk<AdsConsentManager>()
        every { consent.canRequestAds() } returns true
        val ads = mockk<CasInterstitialManager>(relaxed = true)

        bootstrapAdsIfAllowed(prefs, consent, ads)

        verify { ads.preload() }
    }

    @Test
    fun bootstrapAdsIfAllowed_skipsWhenPremium() = runTest {
        assumeTrue(BuildConfig.ADS_ENABLED)

        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns true
        val consent = mockk<AdsConsentManager>()
        val ads = mockk<CasInterstitialManager>(relaxed = true)

        bootstrapAdsIfAllowed(prefs, consent, ads)

        verify(exactly = 0) { ads.preload() }
    }

    @Test
    fun bootstrapAdsIfAllowed_skipsWithoutConsent() = runTest {
        assumeTrue(BuildConfig.ADS_ENABLED)

        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns false
        coEvery { prefs.isConsentCompleted() } returns true
        val consent = mockk<AdsConsentManager>()
        every { consent.canRequestAds() } returns false
        val ads = mockk<CasInterstitialManager>(relaxed = true)

        bootstrapAdsIfAllowed(prefs, consent, ads)

        verify(exactly = 0) { ads.preload() }
    }
}
