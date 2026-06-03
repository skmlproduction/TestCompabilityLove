package dev.lovetest.app.navigation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RequiresConsentForAdsTest {

    @Test
    fun adsDisabled_neverRequiresConsent() {
        assertFalse(requiresConsentForAds(adsEnabled = false, consentCompleted = false))
        assertFalse(requiresConsentForAds(adsEnabled = false, consentCompleted = true))
    }

    @Test
    fun adsEnabled_requiresConsentUntilCompleted() {
        assertTrue(requiresConsentForAds(adsEnabled = true, consentCompleted = false))
        assertFalse(requiresConsentForAds(adsEnabled = true, consentCompleted = true))
    }
}
