package dev.lovetest.app.legal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LegalDocumentsTest {

    @Test
    fun resolveExternalPrivacyUrl_empty_returnsNull() {
        assertNull(LegalDocuments.resolveExternalPrivacyUrl(""))
        assertNull(LegalDocuments.resolveExternalPrivacyUrl("   "))
    }

    @Test
    fun resolveExternalPrivacyUrl_placeholder_returnsNull() {
        assertNull(LegalDocuments.resolveExternalPrivacyUrl(LegalDocuments.PLACEHOLDER_PRIVACY_URL))
        assertNull(
            LegalDocuments.resolveExternalPrivacyUrl("HTTPS://EXAMPLE.COM/privacy"),
        )
    }

    @Test
    fun resolveExternalPrivacyUrl_realUrl_returnsTrimmed() {
        val url = "https://user.github.io/lovetest-privacy/"
        assertEquals(url, LegalDocuments.resolveExternalPrivacyUrl("  $url  "))
    }

    @Test
    fun hasExternalPrivacyPolicy_matchesBuildConfigField() {
        val configured = LegalDocuments.resolveExternalPrivacyUrl(
            dev.lovetest.app.BuildConfig.PRIVACY_POLICY_URL,
        )
        assertEquals(configured != null, LegalDocuments.hasExternalPrivacyPolicy())
    }
}
