# Love Tester: инвентарь экранов, состояний и навигация

Индекс: [SCREEN_INVENTORY_INDEX.md](./SCREEN_INVENTORY_INDEX.md).  
Каталог скриншотов: [screens_catalog.csv](./screens_catalog.csv) (**29** `screen_id`).  
Матрица переходов: [nav_matrix.csv](./nav_matrix.csv).

Документ описывает **целевую** архитектуру MVP+ (до появления Kotlin в F3). При расхождении с кодом — обновлять этот файл первым.

---

## 1. Компоненты (целевой манифест F3)

| Тип | Класс | `exported` | Назначение |
|-----|--------|-------------|------------|
| Application | `LoveTestApplication` | — | Koin, тема |
| Activity | `MainActivity` | да | Compose `LoveTestNavHost` |
| Activity (debug) | `DebugUiPreviewActivity` | нет | Съёмка `DEBUG_UI_PREVIEW` (F5) |

**Не в MVP:** FGS, виджет, overlay (в отличие от LockDraw).

**Queries:** `BILLING`, `INTERNET`, при ads — mediation SDK.

---

## 2. Граф навигации Compose

**Старт:** `Routes.Splash` → по флагам → `Onboarding` | `Consent` | `Hub`.

| `route_path` | `Routes` const | Composable (план) | Top bar / ключевые строки |
|--------------|----------------|-------------------|---------------------------|
| `splash` | `Splash` | `SplashScreen` | `splash_*` |
| `onboarding` | `Onboarding` | `OnboardingScreen` | `onboarding_*` (3 pager-страницы = 3 `screen_id`) |
| `consent` | `Consent` | `ConsentScreen` | `consent_*` (если `ADS_ENABLED`) |
| `hub` | `Hub` | `HubScreen` | `hub_*`, `app_name` |
| `love_test/input` | `LoveTestInput` | `LoveTestInputScreen` | `love_test_*` |
| `love_test/calculating` | `LoveTestCalculating` | `LoveTestCalculatingScreen` | анимация |
| `love_test/result` | `LoveTestResult` | `LoveTestResultScreen` | % + share; low/high = 2 `screen_id` |
| `calculator/input` | `CalculatorInput` | `CalculatorInputScreen` | `calculator_*` |
| `calculator/result` | `CalculatorResult` | `CalculatorResultScreen` | |
| `pair/input` | `PairInput` | `PairInputScreen` | `pair_*` |
| `pair/result` | `PairResult` | `PairResultScreen` | |
| `victory/input` | `VictoryInput` | `VictoryInputScreen` | `victory_*` |
| `victory/result` | `VictoryResult` | `VictoryResultScreen` | |
| `letters/input` | `LettersInput` | `LettersInputScreen` | `letters_*` |
| `letters/result` | `LettersResult` | `LettersResultScreen` | |
| `zodiac/pick` | `ZodiacPick` | `ZodiacPickScreen` | `zodiac_*` |
| `zodiac/result` | `ZodiacResult` | `ZodiacResultScreen` | |
| `wheel/spin` | `WheelSpin` | `WheelSpinScreen` | `wheel_*` |
| `wheel/result` | `WheelResult` | `WheelResultScreen` | |
| `premium/paywall` | `Premium` | `PremiumPaywallScreen` | `premium_*` |
| `premium/thank_you` | `Premium` | `PremiumThankYouScreen` | |
| `settings` | `Settings` | `SettingsScreen` | `settings_*` |

`Routes.allDestinations()` должен включать все const с NavHost `composable`, кроме `N/A` в CSV (`share_result_card`, `ad_interstitial_placeholder` — overlay/debug, не отдельный route).

---

## 3. Состояния по экранам

### `hub`

| screen_id | Условие |
|-----------|---------|
| `hub_main` | Сетка карточек тестов |
| `hub_loading` | Инициализация ads/remote config |
| `error_network` | Баннер «нет сети» (опционально) |

### `love_test`

| screen_id | Условие |
|-----------|---------|
| `love_test_input` | Пустая/заполненная форма |
| `love_test_calculating` | Progress, сердца |
| `love_test_result` | Процент ≥ порога (напр. 50%) |
| `love_test_result_low` | Процент < порога |

### `onboarding`

| screen_id | Pager |
|-----------|-------|
| `onboarding_welcome` | 1 |
| `onboarding_tests` | 2 |
| `onboarding_disclaimer` | 3 |

### `premium`

| screen_id | Условие |
|-----------|---------|
| `premium_paywall` | Каталог загружен |
| `premium_thank_you` | Покупка OK |

---

## 4. Внешние Intent

| Источник | Intent | Заметки |
|----------|--------|---------|
| `love_test_result` | `ACTION_SEND` | Текст + опционально bitmap карточки |
| `settings_main` | `ACTION_VIEW` https | Privacy policy URL |
| Launcher | `MAIN` / `LAUNCHER` | → `splash` |

---

## 5. Соглашение `screen_id` и скриншотов

- **Формат:** `snake_case`, стабильный для adb и Store.
- **Файлы Store:** `docs/screenshots/{ru,en}/<screen_id>.png` (1080×1920 после F5).
- **Референс оригинала:** `reference/screenshots/device/` — см. [INDEX.md](../../reference/screenshots/INDEX.md).
- **Debug:** `adb shell am start -n dev.lovetest.app/.MainActivity --es debug_start_route love_test/result` (имя extra уточнить в F3, по образцу LockDraw `NavIntents`).

---

## 6. Сводка маршрутов NavHost (план)

Уникальные `route_path` в CSV: **20** (+ splash, hub variants share `hub`).

```
splash → onboarding | consent | hub
hub → love_test/* | calculator/* | pair/* | victory/* | letters/* | zodiac/* | wheel/* | settings | premium/*
* → hub (back)
```

**Число `screen_id`:** 29 (визуально различимых состояний для PNG RU+EN → до 58 файлов).

---

## 7. Связанные документы

- `PRD.md` — MVP scope
- `screens_catalog_DRAFT.md` — устаревший черновик; источник правды — **CSV**
- `REFERENCE_SOURCES.md` — фичи референса 3.0.5
