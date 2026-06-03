package dev.lovetest.app.monetization

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dev.lovetest.app.BuildConfig

/**
 * Google UMP wrapper for GDPR/consent before AdMob.
 * No-op when [BuildConfig.ADS_ENABLED] is false.
 */
class AdsConsentManager(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(appContext)

    fun requestConsentInfoUpdate(activity: Activity, onComplete: () -> Unit) {
        if (!BuildConfig.ADS_ENABLED) {
            onComplete()
            return
        }
        val params = consentRequestParameters(activity)
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            { onComplete() },
            { onComplete() },
        )
    }

    /**
     * Shows UMP form when required. [onFinished] receives whether ads may be requested.
     */
    fun gatherConsent(activity: Activity, onFinished: (Boolean) -> Unit) {
        if (!BuildConfig.ADS_ENABLED) {
            onFinished(true)
            return
        }
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { _ ->
            onFinished(canRequestAds())
        }
    }

    fun showPrivacyOptions(activity: Activity, onComplete: () -> Unit) {
        if (!BuildConfig.ADS_ENABLED) {
            onComplete()
            return
        }
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { _ ->
            onComplete()
        }
    }

    fun canRequestAds(): Boolean {
        if (!BuildConfig.ADS_ENABLED) return false
        return consentInformation.canRequestAds()
    }

    fun isPrivacyOptionsRequired(): Boolean {
        if (!BuildConfig.ADS_ENABLED) return false
        return consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    private fun consentRequestParameters(activity: Activity): ConsentRequestParameters {
        val builder = ConsentRequestParameters.Builder()
        if (BuildConfig.DEBUG) {
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .build()
            builder.setConsentDebugSettings(debugSettings)
        }
        return builder.build()
    }
}
