package dev.lovetest.app.monetization

import dev.lovetest.app.BuildConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assume.assumeTrue
import org.junit.Test

class AdMobInterstitialManagerTest {

    @Test
    fun discard_clearsPreloadedAdWithoutPreload() {
        assumeTrue(BuildConfig.ADS_ENABLED)

        val consent = mockk<AdsConsentManager>()
        every { consent.canRequestAds() } returns true
        val manager = AdMobInterstitialManager(mockk(relaxed = true), consent)

        manager.discard()
        manager.discard()

        verify(exactly = 0) { consent.canRequestAds() }
    }

    @Test
    fun show_returnsFalseWhenAdsDisabled() {
        assumeTrue(!BuildConfig.ADS_ENABLED)

        val consent = mockk<AdsConsentManager>()
        val manager = AdMobInterstitialManager(mockk(relaxed = true), consent)

        val shown = manager.show(mockk(relaxed = true)) {}

        assert(!shown)
    }
}
