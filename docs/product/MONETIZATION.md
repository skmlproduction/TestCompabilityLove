# Монетизация — Love Tester (MVP)

## Premium

- Покупка на `premium_paywall` сохраняет флаг `premium_active` в DataStore.
- Premium отключает межстраничную рекламу (AdMob и UI placeholder).
- Billing SDK (`billing-ktx` 8.x): `PremiumBillingManager` — purchase, restore, acknowledge.
- Пустой `lovetest.billing.product.ids` → mock purchase **только в DEBUG**; в release — toast, Premium не выдаётся.
- Задан SKU → реальный flow; покупки подтверждаются через `acknowledgePurchase`.

## Реклама

- `BuildConfig.ADS_ENABLED` ← `lovetest.ads.enabled=true` в `gradle.properties`.
- После завершения теста и перехода на hub: сначала **AdMob interstitial**, при ошибке — `AdInterstitialPlaceholder` (screen29).
- Не показывается: без consent, без UMP `canRequestAds()`, с Premium, при `ADS_ENABLED=false`.
- Preload/init: `bootstrapAdsIfAllowed()` — только после consent и UMP.
- Smoke-сборка с ads: `./gradlew verifyAdsBuildLoveTest`.

## Consent (UMP)

- Экран `consent_ads_gdpr` (screen5) только при `ADS_ENABLED=true`; флаг `consent_completed` обязателен для показа рекламы.
- `AdsConsentManager` — Google UMP SDK (`user-messaging-platform`).
- Accept → UMP form → Hub; Manage → privacy options / consent form.
- Повторный выбор: **Настройки → Настройки рекламы** (UMP forms, без сброса onboarding).
- DEBUG: `DEBUG_GEOGRAPHY_EEA` для тестирования формы вне EEA.

## Конфигурация

См. `gradle.properties.example` (AdMob IDs, UMP message в AdMob Console).
