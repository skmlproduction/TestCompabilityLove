# Audit report — Love Tester (F6)

Дата: 2026-05-24

## Сводка

| Уровень | Количество |
|---------|------------|
| P0 | 0 |
| P1 | 0 |
| P2 | 1 |

## Матрица экранов

| № | screen_id | Kotlin | SVG | PNG RU | PNG EN | Debug |
|---|-----------|--------|-----|--------|--------|-------|
| 1 | `splash_brand` | ✅ | ✅ | ok | ok | ✅ |
| 2 | `onboarding_welcome` | ✅ | ✅ | ok | ok | ✅ |
| 3 | `onboarding_tests` | ✅ | ✅ | ok | ok | ✅ |
| 4 | `onboarding_disclaimer` | ✅ | ✅ | ok | ok | ✅ |
| 5 | `consent_ads_gdpr` | ✅ | ✅ | ok | ok | ✅ |
| 6 | `hub_main` | ✅ | ✅ | ok | ok | ✅ |
| 7 | `hub_loading` | ✅ | ✅ | ok | ok | ✅ |
| 8 | `love_test_input` | ✅ | ✅ | ok | ok | ✅ |
| 9 | `love_test_calculating` | ✅ | ✅ | ok | ok | ✅ |
| 10 | `love_test_result` | ✅ | ✅ | ok | ok | ✅ |
| 11 | `love_test_result_low` | ✅ | ✅ | ok | ok | ✅ |
| 12 | `calculator_input` | ✅ | ✅ | ok | ok | ✅ |
| 13 | `calculator_result` | ✅ | ✅ | ok | ok | ✅ |
| 14 | `pair_input` | ✅ | ✅ | ok | ok | ✅ |
| 15 | `pair_result` | ✅ | ✅ | ok | ok | ✅ |
| 16 | `victory_input` | ✅ | ✅ | ok | ok | ✅ |
| 17 | `victory_result` | ✅ | ✅ | ok | ok | ✅ |
| 18 | `letters_input` | ✅ | ✅ | ok | ok | ✅ |
| 19 | `letters_result` | ✅ | ✅ | ok | ok | ✅ |
| 20 | `zodiac_pick` | ✅ | ✅ | ok | ok | ✅ |
| 21 | `zodiac_result` | ✅ | ✅ | ok | ok | ✅ |
| 22 | `wheel_spin` | ✅ | ✅ | ok | ok | ✅ |
| 23 | `wheel_result` | ✅ | ✅ | ok | ok | ✅ |
| 24 | `premium_paywall` | ✅ | ✅ | ok | ok | ✅ |
| 25 | `premium_thank_you` | ✅ | ✅ | ok | ok | ✅ |
| 26 | `settings_main` | ✅ | ✅ | ok | ok | ✅ |
| 27 | `share_result_card` | ✅ | ✅ | ok | ok | ✅ |
| 28 | `error_network` | ✅ | ✅ | ok | ok | ✅ |
| 29 | `ad_interstitial_placeholder` | ✅ | ✅ | ok | n/a | ✅ |
| 30 | `onboarding_protocol` | ✅ | ✅ | ok | ok | ✅ |
| 31 | `protocol_input` | ✅ | ✅ | ok | ok | ✅ |
| 32 | `protocol_calculating` | ✅ | ✅ | ok | ok | ✅ |
| 33 | `protocol_result` | ✅ | ✅ | ok | ok | ✅ |
| 34 | `protocol_result_low` | ✅ | ✅ | ok | ok | ✅ |

## P0

_Нет._

## P1

_Нет._

## P2

- —: lovetest.privacy.policy.url — placeholder example.com (замените на публичный HTTPS)

## Качество (автопроверка)

| Метрика | Значение |
|---------|----------|
| Unit tests | 36 (`app/src/test`) |
| Compose UI tests | 49 (24 ComposeTest-класса) |
| Route smoke (instrumented) | 7 (Hub, Consent, Onboarding, LoveTestInput, ProtocolInput, PremiumPaywall, Settings) |
| Store PNG | 67/67 |
| Audit P0/P1 | 0 |

Проверка: `./scripts/count_tests.sh` · `./gradlew verifyLoveTest`

## Рекомендуемые действия

1. `./scripts/setup_github_remote.sh USER REPO` → `./scripts/prepare_git_push.sh` → commit → push
2. GitHub Pages → `./scripts/post_privacy_setup.sh https://USER.github.io/REPO/`
3. Production keystore → `./gradlew bundleReleaseLoveTest`
4. `./gradlew finalizeStoreReleaseLoveTest` → upload `build/store-upload/`
5. Internal track → Closed testing → Production
