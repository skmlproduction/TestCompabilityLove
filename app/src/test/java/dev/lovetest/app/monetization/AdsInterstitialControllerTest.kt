package dev.lovetest.app.monetization

import dev.lovetest.app.BuildConfig
import dev.lovetest.app.prefs.AppPreferences
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

class AdsInterstitialControllerTest {

    @Test
    fun shouldShow_falseWhenAdsDisabled() = runTest {
        assumeTrue(!BuildConfig.ADS_ENABLED)

        val prefs = mockk<AppPreferences>()
        val consent = mockk<AdsConsentManager>()

        assertFalse(AdsInterstitialController.shouldShow(prefs, consent))
    }

    @Test
    fun shouldShow_falseWhenPremiumOrConsentMissing() = runTest {
        assumeTrue(BuildConfig.ADS_ENABLED)

        val consent = mockk<AdsConsentManager>()
        every { consent.canRequestAds() } returns true

        val premiumPrefs = mockk<AppPreferences>()
        coEvery { premiumPrefs.isPremium() } returns true
        coEvery { premiumPrefs.isConsentCompleted() } returns true
        assertFalse(AdsInterstitialController.shouldShow(premiumPrefs, consent))

        val noConsentPrefs = mockk<AppPreferences>()
        coEvery { noConsentPrefs.isPremium() } returns false
        coEvery { noConsentPrefs.isConsentCompleted() } returns false
        assertFalse(AdsInterstitialController.shouldShow(noConsentPrefs, consent))
    }

    @Test
    fun shouldShow_falseWhenUmpBlocksAds() = runTest {
        assumeTrue(BuildConfig.ADS_ENABLED)

        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns false
        coEvery { prefs.isConsentCompleted() } returns true

        val consent = mockk<AdsConsentManager>()
        every { consent.canRequestAds() } returns false

        assertFalse(AdsInterstitialController.shouldShow(prefs, consent))
    }

    @Test
    fun shouldShow_trueWhenAllGatesPass() = runTest {
        assumeTrue(BuildConfig.ADS_ENABLED)

        val prefs = mockk<AppPreferences>()
        coEvery { prefs.isPremium() } returns false
        coEvery { prefs.isConsentCompleted() } returns true

        val consent = mockk<AdsConsentManager>()
        every { consent.canRequestAds() } returns true

        assertTrue(AdsInterstitialController.shouldShow(prefs, consent))
    }

    @Test
    fun pendingOnHub_roundTrip() {
        AdsInterstitialController.consume()
        assertFalse(AdsInterstitialController.pendingOnHub.value)

        AdsInterstitialController.requestShowOnHub()
        assertTrue(AdsInterstitialController.pendingOnHub.value)

        AdsInterstitialController.consume()
        assertFalse(AdsInterstitialController.pendingOnHub.value)
    }
}
