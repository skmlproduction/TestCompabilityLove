# Design System — Love Tester (Material 3 light)

Эталонные макеты: `docs/design/screen*_love_test_*_m3.svg` · viewBox `0 0 1080 2400`.

## Цвета

| Token | Hex | Использование |
|-------|-----|----------------|
| `primary` | `#C2185B` | CTA, прогресс, акценты |
| `primaryContainer` | `#FCE4EC` | Фон карточек, chips |
| `onPrimary` | `#FFFFFF` | Текст на CTA / hero |
| `onPrimaryContainer` | `#880E4F` | Текст на container |
| `secondary` | `#E91E63` | Градиент hero, hearts |
| `surface` | `#FFFBFE` | Фон экрана |
| `onSurface` | `#1C1B1F` | Заголовки |
| `onSurfaceVariant` | `#625B71` | Body |
| `outline` | `#CAC4D0` | Обводки, неактив |
| `outlineVariant` | `#E7E0EC` | Прогресс-трек |
| `errorContainer` | `#F9DEDC` | Ошибки |
| `onErrorContainer` | `#410E0B` | Текст ошибки |
| `protocolPrimary` | `#00796B` | Протокол любви (тест №8), hub-карточка |
| `protocolContainer` | `#E0F2F1` | Фон chips протокола |

### Градиенты (SVG ids)

- `bgGlow` — фон: `#FFF0F5` → `#FFFBFE`
- `heroGradient` — hero-карточка: `#C2185B` → `#E91E63` → `#F8BBD0`
- `protocolGradient` — протокол: `#004D40` → `#00796B` → `#26A69A`

## Типографика (роли M3)

| Класс CSS в SVG | Размер | Weight | Назначение |
|-----------------|--------|--------|------------|
| `.appTitle` | 34px | 760 | Top bar название |
| `.headline` | 58px | 800 | Заголовок экрана |
| `.title` | 31px | 740 | Карточки, списки |
| `.body` | 29px | 450 | Основной текст |
| `.section` | 21px | 760, letter-spacing 3px | Kicker / small caps |
| `.caption` | 21px | 470 | Подписи |
| `.buttonText` | 29px | 760 | CTA |
| `.heroTitle` | 64px | 830 | Hero заголовок |
| `.heroBody` | 29px | 450 | Hero подзаголовок |
| `.percent` | 120px | 800 | Кольцо результата (экраны 10–11) |

Шрифт в макете: **Inter / Roboto** (в приложении — system / Roboto).

## Отступы (dp → px 1:1 в SVG)

| Token | px |
|-------|-----|
| screen horizontal | 72 (≈24dp @ xxxhd) |
| section gap | 32 |
| card padding | 48 |
| between cards | 24 |
| bottom CTA margin | 72 |

## Радиусы

| Элемент | rx |
|---------|-----|
| Hero / large card | 54 |
| Standard card | 38 |
| Button | 44 (pill) |
| Text field | 28 |
| Chip | 32 |

## Паттерны

- **Hero:** `linearGradient` primary → secondary, тень `cardShadow`, декор — круги + heart path.
- **CTA:** полная ширина (936px), высота 88px, `fill="#C2185B"`.
- **Status bar:** высота 96px, полупрозрачный `#FFFBFE`.
- **Screen label (debug):** `.section` — `ЭКРАН N / ID`.

## Иконография

- Сердце: симметричный path (не emoji raster).
- Процент: кольцо stroke 24px, дуга primary.

## Файлы

| № | SVG | Статус |
|---|-----|--------|
| 1 | `screen1_love_test_splash_brand_m3.svg` | ✅ |
| 2 | `screen2_love_test_onboarding_welcome_m3.svg` | ✅ |
| 3 | `screen3_love_test_onboarding_tests_m3.svg` | ✅ |
| 4 | `screen4_love_test_onboarding_disclaimer_m3.svg` | ✅ |
| 5 | `screen5_love_test_consent_ads_m3.svg` | ✅ |
| 6 | `screen6_love_test_hub_main_m3.svg` | ✅ **эталон хаба** |
| 7 | `screen7_love_test_hub_loading_m3.svg` | ✅ |
| 8 | `screen8_love_test_input_m3.svg` | ✅ |
| 9 | `screen9_love_test_calculating_m3.svg` | ✅ |
| 10 | `screen10_love_test_result_high_m3.svg` | ✅ |
| 11 | `screen11_love_test_result_low_m3.svg` | ✅ |
| 12 | `screen12_love_test_calculator_input_m3.svg` | ✅ |
| 13 | `screen13_love_test_calculator_result_m3.svg` | ✅ |
| 14 | `screen14_love_test_pair_input_m3.svg` | ✅ |
| 15 | `screen15_love_test_pair_result_m3.svg` | ✅ |
| 16 | `screen16_love_test_victory_input_m3.svg` | ✅ |
| 17 | `screen17_love_test_victory_result_m3.svg` | ✅ |
| 18 | `screen18_love_test_letters_input_m3.svg` | ✅ |
| 19 | `screen19_love_test_letters_result_m3.svg` | ✅ |
| 20 | `screen20_love_test_zodiac_pick_m3.svg` | ✅ |
| 21 | `screen21_love_test_zodiac_result_m3.svg` | ✅ |
| 22 | `screen22_love_test_wheel_spin_m3.svg` | ✅ |
| 23 | `screen23_love_test_wheel_result_m3.svg` | ✅ |
| 24 | `screen24_love_test_premium_paywall_m3.svg` | ✅ |
| 25 | `screen25_love_test_premium_thank_you_m3.svg` | ✅ |
| 26 | `screen26_love_test_settings_main_m3.svg` | ✅ |
| 27 | `screen27_love_test_share_card_m3.svg` | ✅ |
| 28 | `screen28_love_test_error_network_m3.svg` | ✅ |
| 29 | `screen29_love_test_ad_interstitial_m3.svg` | ✅ |

**F2 MVP:** №1–29 готовы. **Post-MVP:** протокол №30–36. **EN (Store):** №37–51 + протокол №52–56.

**Compose tokens:** `core/ui/.../theme/Color.kt` — `LovePrimary`, `LoveProtocolPrimary`, `LoveProtocolHeroGradientColors`.

**Android theme:** `Theme.LoveTester.Splash` (core-splashscreen) → `Theme.LoveTester` · `values/themes.xml`.

**Кодировка SVG:** `<?xml encoding="UTF-8"?>`, кириллица через **numeric entities** (`&#1058;` …), без emoji в `<text>` (иконки — vector).
