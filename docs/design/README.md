# Love Tester — design references (M3)

SVG-макеты **Material 3 light**, primary `#C2185B`, surface `#FFFBFE`.  
Индекс соответствует [screens_catalog.csv](../product/screens_catalog.csv).

**Статус:** фаза F2 — рисуем **по одному экрану** (№1 → №29). Дизайн-система: [DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md).

## System & hub (1–7)

| SVG (план) | screen_id |
|------------|-----------|
| `screen1_love_test_splash_brand_m3.svg` ✅ | `splash_brand` |
| `screen2_love_test_onboarding_welcome_m3.svg` ✅ | `onboarding_welcome` |
| `screen3_love_test_onboarding_tests_m3.svg` ✅ | `onboarding_tests` |
| `screen4_love_test_onboarding_disclaimer_m3.svg` ✅ | `onboarding_disclaimer` |
| `screen5_love_test_consent_ads_m3.svg` ✅ | `consent_ads_gdpr` |
| `screen6_love_test_hub_main_m3.svg` ✅ | `hub_main` |
| `screen7_love_test_hub_loading_m3.svg` ✅ | `hub_loading` |

## Love test core (8–11)

| SVG | screen_id |
|-----|-----------|
| `screen8_love_test_input_m3.svg` ✅ | `love_test_input` |
| `screen9_love_test_calculating_m3.svg` ✅ | `love_test_calculating` |
| `screen10_love_test_result_high_m3.svg` ✅ | `love_test_result` |
| `screen11_love_test_result_low_m3.svg` ✅ | `love_test_result_low` |

## Other tests (12–23)

| SVG | screen_id |
|-----|-----------|
| `screen12_love_test_calculator_input_m3.svg` ✅ | `calculator_input` |
| `screen13_love_test_calculator_result_m3.svg` ✅ | `calculator_result` |
| `screen14_love_test_pair_input_m3.svg` ✅ | `pair_input` |
| `screen15_love_test_pair_result_m3.svg` ✅ | `pair_result` |
| `screen16_love_test_victory_input_m3.svg` ✅ | `victory_input` |
| `screen17_love_test_victory_result_m3.svg` ✅ | `victory_result` |
| `screen18_love_test_letters_input_m3.svg` ✅ | `letters_input` |
| `screen19_love_test_letters_result_m3.svg` ✅ | `letters_result` |
| `screen20_love_test_zodiac_pick_m3.svg` ✅ | `zodiac_pick` |
| `screen21_love_test_zodiac_result_m3.svg` ✅ | `zodiac_result` |
| `screen22_love_test_wheel_spin_m3.svg` ✅ | `wheel_spin` |
| `screen23_love_test_wheel_result_m3.svg` ✅ | `wheel_result` |

## Monetization & settings (24–29)

| SVG | screen_id |
|-----|-----------|
| `screen24_love_test_premium_paywall_m3.svg` ✅ | `premium_paywall` |
| `screen25_love_test_premium_thank_you_m3.svg` ✅ | `premium_thank_you` |
| `screen26_love_test_settings_main_m3.svg` ✅ | `settings_main` |
| `screen27_love_test_share_card_m3.svg` ✅ | `share_result_card` |
| `screen28_love_test_error_network_m3.svg` ✅ | `error_network` |
| `screen29_love_test_ad_interstitial_m3.svg` ✅ | `ad_interstitial_placeholder` |

## Протокол (№30–34 в CSV) + варианты дизайна

| SVG | screen_id | Kotlin |
|-----|-----------|--------|
| `screen30_love_test_protocol_input_m3.svg` ✅ | `protocol_input` | `ProtocolInputScreen.kt` |
| `screen31_love_test_protocol_result_m3.svg` ✅ | `protocol_result` | `ProtocolResultScreen.kt` |
| `screen32_love_test_protocol_calculating_m3.svg` ✅ | `protocol_calculating` | `LoveTestCalculatingScreen` (Protocol) |
| `screen34_love_test_protocol_result_low_m3.svg` ✅ | `protocol_result_low` | `ProtocolResultScreen.kt` |
| `screen35_love_test_onboarding_protocol_m3.svg` ✅ | `onboarding_protocol` | `OnboardingScreen.kt` (стр. 3/4) |
| `screen33_love_test_hub_protocol_card_m3.svg` ✅ | `hub_main` | `HubScreen` — карточка протокола |
| `screen36_love_test_onboarding_disclaimer_4_m3.svg` ✅ | `onboarding_disclaimer` | стр. 4/4 onboarding |

## Store / локали (№37+)

| SVG | screen_id | Локаль |
|-----|-----------|--------|
| `screen37_love_test_hub_main_en_m3.svg` ✅ | `hub_main` | EN (values-en) |
| `screen38_love_test_result_high_en_m3.svg` ✅ | `love_test_result` | EN (values-en) |
| `screen39_love_test_splash_brand_en_m3.svg` ✅ | `splash_brand` | EN (values-en) |
| `screen40_love_test_onboarding_welcome_en_m3.svg` ✅ | `onboarding_welcome` | EN (values-en) |
| `screen41_love_test_onboarding_tests_en_m3.svg` ✅ | `onboarding_tests` | EN (values-en) |
| `screen42_love_test_onboarding_disclaimer_en_m3.svg` ✅ | `onboarding_disclaimer` | EN (values-en) |
| `screen43_love_test_input_en_m3.svg` ✅ | `love_test_input` | EN (values-en) |
| `screen44_love_test_premium_paywall_en_m3.svg` ✅ | `premium_paywall` | EN (values-en) |
| `screen45_love_test_calculating_en_m3.svg` ✅ | `love_test_calculating` | EN (values-en) |
| `screen46_love_test_settings_main_en_m3.svg` ✅ | `settings_main` | EN (values-en) |
| `screen47_love_test_share_card_en_m3.svg` ✅ | `share_result_card` | EN (values-en) |
| `screen48_love_test_result_low_en_m3.svg` ✅ | `love_test_result` (low) | EN (values-en) |
| `screen49_love_test_premium_thank_you_en_m3.svg` ✅ | `premium_thank_you` | EN (values-en) |
| `screen50_love_test_pair_input_en_m3.svg` ✅ | `pair_input` | EN (values-en) |
| `screen51_love_test_pair_result_en_m3.svg` ✅ | `pair_result` | EN (values-en) |

## Protocol EN (№52–56)

| SVG | screen_id | Локаль |
|-----|-----------|--------|
| `screen52_love_test_protocol_input_en_m3.svg` ✅ | `protocol_input` | EN |
| `screen53_love_test_protocol_calculating_en_m3.svg` ✅ | `protocol_calculating` | EN |
| `screen54_love_test_protocol_result_en_m3.svg` ✅ | `protocol_result` | EN |
| `screen55_love_test_protocol_result_low_en_m3.svg` ✅ | `protocol_result_low` | EN |
| `screen56_love_test_onboarding_protocol_en_m3.svg` ✅ | `onboarding_protocol` | EN |

Генерация из RU-источников: `python3 scripts/generate_protocol_en_svgs.py`

## Прогресс

| Готово SVG | MVP RU | Post-MVP | EN |
|------------|--------|----------|-----|
| 56 | 29 | 7 | 20 |

**F2 MVP (№1–29 RU) завершена.** EN: core Store set (15 экранов).

## F3 Scaffold (Android)

| Компонент | Статус |
|-----------|--------|
| `:app` + `:core:ui` + `:core:domain` | ✅ |
| `LoveTestNavHost` — 25 route (+ протокол) | ✅ |
| `LoveScoreCalculator` + unit-тесты | ✅ |
| Splash → Onboarding → Consent → Hub | ✅ UI |
| Love test flow (input → calculating → result) | ✅ UI + расчёт |
| Остальные тесты / premium / settings | ✅ каркас UI + навигация |
| `DebugUiPreview` + `verifyLoveTest` | ✅ |

**F4 UI 1:1:** `hub_main` ✅ · `splash_brand` ✅ · love-test flow ✅ · onboarding (2–4) ✅ · `consent_ads_gdpr` ✅ (screen5) · `calculator_*` ✅ · `pair_*` ✅ · `victory_*` ✅ · `letters_*` ✅ (screen18–19) · `zodiac_*` ✅ (screen20–21) · `wheel_*` ✅ (screen22–23) · `premium_*` ✅ (screen24–25) · `settings_main` ✅ (screen26) · `share_result_card` ✅ (screen27) · `ad_interstitial_placeholder` ✅ (screen29).

**F4 завершена** (overlay: screen27, screen29; `hub_loading` / `error_network` — в `HubScreen`).

**F5 Store PNG (инфраструктура):** `materializeScreenshotPlaceholders` · `captureScreenshotCatalogRu|En` · `verifyLoveTestBeforeStore` · `docs/screenshots/WORKFLOW.md`.

**F5 монетизация / шаринг / legal:** `PremiumBillingManager` (Play Billing 8 + acknowledge) · share-sheet на result-экранах · `assets/legal/*.html` + `LegalDocumentActivity` · `keystore.properties.example` · см. `MONETIZATION.md`, `ONBOARDING_AND_LEGAL.md`.

**F5 (код):** ✅ инфраструктура съёмки, billing, legal, store-доки. **Осталось локально:** `captureScreenshotCatalogRu|En` → `verifyLoveTestBeforeStore`.

**F6 аудит:** `./gradlew auditLoveTestScreens` → `AUDIT_REPORT.md` (типично P0=0, P1=placeholder PNG, P2=privacy URL).

**Upload:** `docs/store/STORE_UPLOAD.md`
