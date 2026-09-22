package dev.lovetest.app.monetization

import android.app.Application
import com.cleversolutions.ads.ConsentFlow
import com.cleversolutions.ads.android.CAS
import dev.lovetest.app.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * CAS.AI mediation (CleverAdsSolutions): единая точка инициализации и состояния.
 *
 * CAS ID = package name приложения ([CAS_ID]); приложение регистрируется в дашборде
 * https://cas.ai — до регистрации release получает test/demo fill, debug всегда
 * в test ad mode ([BuildConfig.DEBUG]).
 *
 * Consent (GDPR/UMP) обрабатывается встроенным consent manager'ом CAS
 * ([ConsentFlow]); форма «Privacy options» в настройках — через UMP
 * ([AdsConsentManager]), статус согласия общий (IAB TCF).
 */
object CasAdsManager {

    /** CAS ID совпадает с applicationId (см. docs.cas.ai). */
    const val CAS_ID = "dev.lovetest.app"

    private val _canRequestAds = MutableStateFlow(false)

    /** true после успешной инициализации SDK + завершения consent flow. */
    val canRequestAds: StateFlow<Boolean> = _canRequestAds

    /** Вызывается один раз из `LoveTestApplication.onCreate`. Повторный вызов безопасно игнорируется CAS. */
    fun initialize(application: Application) {
        if (!BuildConfig.ADS_ENABLED) return
        runCatching {
            CAS.buildManager()
                .withCasId(CAS_ID)
                .withTestAdMode(BuildConfig.DEBUG)
                .withConsentFlow(
                    ConsentFlow(isEnabled = true).withDismissListener {
                        // Consent dismissed — CAS сам решает, можно ли запрашивать рекламу.
                    },
                )
                .withCompletionListener { config ->
                    _canRequestAds.value = config.error == null
                }
                .build(application)
        }.onFailure {
            _canRequestAds.value = false
        }
    }
}
