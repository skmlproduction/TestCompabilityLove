# Capture checklist — Love Tester Store PNG

Целевой размер: **1080×1920** (портрет). Пути — `docs/product/screens_catalog.csv`.

## Пересъёмка после UI-фиксов (2026-06-04)

Старые PNG **не подменять** без adb. Инструкция: [RECAPTURE_AFTER_UI_FIX.md](RECAPTURE_AFTER_UI_FIX.md) · скрипт: `./scripts/recapture_store_screenshots.sh`

## Перед съёмкой

- [ ] `./gradlew verifyLoveTest` — OK
- [ ] `./gradlew captureReadinessLoveTest` — 34/34 DEBUG preview, adb статус
- [ ] Эмулятор API 34+, 1080×1920, light theme
- [ ] `adb devices` — одно устройство
- [ ] Debug APK: `./gradlew :app:installDebug`

## Visual QA (фиксы шапки / hero / Settings / muted low %)

Без adb — только код-ревью; с эмулятором:

```bash
./scripts/setup_android_sdk.sh
./scripts/start_capture_emulator.sh   # при необходимости
./scripts/capture_visual_qa_ru.sh     # PNG → docs/screenshots/qa/ru/
./scripts/capture_visual_qa_ru.sh --check-only
```

| Критерий | Экраны в скрипте |
|----------|------------------|
| Одна шапка, без серой ActionBar | `splash_brand`, `hub_main` |
| RU hero не обрезается | `pair_input`, `letters_input`, `victory_input`, onboarding ×3 |
| «Назад» не под часами | `settings_main` |
| Low % = серый hero везде | `*_result_low` (love, protocol, pair, letters, victory, calculator) |

## Команды

```bash
# Шаблоны (до первой съёмки)
./gradlew materializeScreenshotPlaceholders

# Весь каталог
./gradlew captureScreenshotCatalogRu
./gradlew captureScreenshotCatalogEn

# Приоритет для листинга (7 экранов, быстрее)
./scripts/capture_priority_screens.sh ru
./gradlew -Plocale=en capturePriorityScreensLoveTest

# Один экран
./scripts/adb_screenshot_preview.sh hub_main ru
./scripts/adb_screenshot_preview.sh love_test_result en
```

## Приоритет для листинга Play (RU + EN)

| screen_id | № | Честность / дисклеймер |
|-----------|---|----------------------|
| `hub_main` | 6 | Реальный хаб |
| `love_test_input` | 8 | Ввод имён |
| `love_test_result` | 10 | **Дисклеймер** на результате |
| `protocol_input` | 31 | Протокол, ввод |
| `protocol_result` | 33 | **«Только для развлечения»** |
| `wheel_spin` | 22 | **Not a bet** / fun (note3) |
| `premium_paywall` | 24 | IAP optional, не блокирует тесты |

Опционально **8-й** кадр: `onboarding_disclaimer` — если нужен явный disclaimer в галерее Store.

## Debug preview (`DEBUG_UI_PREVIEW`)

| screen_id | Примечание |
|-----------|------------|
| `onboarding_tests` | страница 2 |
| `onboarding_protocol` | страница 3 |
| `onboarding_disclaimer` | страница 4 |
| `protocol_input` | ввод имён |
| `protocol_calculating` | расчёт |
| `protocol_result` | высокий % |
| `protocol_result_low` | низкий % |
| `love_test_result_low` | 23% Anna+Max |
| `share_result_card` | sheet на result |
| `hub_loading` / `error_network` | overlay на hub |
| `ad_interstitial_placeholder` | fullscreen ad mock |

## Проверка перед Store

```bash
./gradlew verifyLoveTestBeforeStore
```

Требует реальные PNG (не розовые шаблоны < 32 KB).
