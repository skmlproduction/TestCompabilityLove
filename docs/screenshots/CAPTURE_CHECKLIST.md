# Capture checklist — Love Tester Store PNG

Целевой размер: **1080×1920** (портрет). Пути — `docs/product/screens_catalog.csv`.

## Перед съёмкой

- [ ] `./gradlew verifyLoveTest` — OK
- [ ] `./gradlew captureReadinessLoveTest` — 34/34 DEBUG preview, adb статус
- [ ] Эмулятор API 34+, 1080×1920, light theme
- [ ] `adb devices` — одно устройство
- [ ] Debug APK: `./gradlew :app:installDebug`

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

| screen_id | № |
|-----------|---|
| `hub_main` | 6 |
| `love_test_input` | 8 |
| `love_test_result` | 10 |
| `protocol_input` | 31 |
| `protocol_result` | 33 |
| `wheel_spin` | 22 |
| `premium_paywall` | 24 |

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
