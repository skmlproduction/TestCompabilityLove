package dev.lovetest.app.legal

import android.content.Context
import android.content.Intent
import android.net.Uri
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.R

object LegalDocuments {
    const val PRIVACY_ASSET = "legal/privacy_policy.html"
    const val DATA_COLLECTION_ASSET = "legal/data_collection.html"
    const val PLACEHOLDER_PRIVACY_URL = "https://example.com/privacy"

    /** Публичный HTTPS для Play; null → bundled assets/legal. */
    fun resolveExternalPrivacyUrl(raw: String): String? {
        val url = raw.trim()
        if (url.isEmpty() || url.equals(PLACEHOLDER_PRIVACY_URL, ignoreCase = true)) {
            return null
        }
        return url
    }

    fun hasExternalPrivacyPolicy(): Boolean =
        resolveExternalPrivacyUrl(BuildConfig.PRIVACY_POLICY_URL) != null

    fun Context.openPrivacyPolicy() {
        val external = resolveExternalPrivacyUrl(BuildConfig.PRIVACY_POLICY_URL)
        if (external != null) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(external)))
        } else {
            startActivity(
                LegalDocumentActivity.intent(
                    context = this,
                    assetPath = PRIVACY_ASSET,
                    title = getString(R.string.settings_privacy_open),
                ),
            )
        }
    }

    fun Context.openDataCollectionSummary() {
        startActivity(
            LegalDocumentActivity.intent(
                context = this,
                assetPath = DATA_COLLECTION_ASSET,
                title = getString(R.string.settings_data_collection),
            ),
        )
    }
}
