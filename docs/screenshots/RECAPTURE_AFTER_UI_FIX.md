# Пересъёмка Store PNG после UI-фиксов (промпт 3/7)

Цель: **34 RU + 34 EN** в `docs/screenshots/{ru,en}/` и **7 listing PNG × 2** (1080×1920) с актуальной debug-сборкой (NoActionBar, hero `heightIn`, `statusBarsPadding`, muted low %).

## Статус в репозитории (агент без adb)

| Проверка | Результат |
|----------|-----------|
| Пересъёмка на исправленной сборке | **не выполнялась** (нет `adb` в среде агента) |
| Старые PNG подменены | **нет** — файлы не трогались |
| Текущие `docs/screenshots/ru|en/*.png` | **34+34**, 1080×1920, ≥32 KB — дата съёмки **~2026-05-24** (до UI-правок **2026-06-04**) |
| PNG gate (`verify_ui_inventory --fail-on-placeholders`) | OK на **существующих** кадрах |
| `validate_store_upload.sh` | OK на **существующем** `build/store-upload/` |

**Для Play Store после UI-фиксов нужна новая съёмка** — старые кадры формально проходят gate, но не отражают исправленный UI.

## Быстрый pipeline (одна команда)

```bash
cd /path/to/TestCompabilityLove
source ./scripts/android_sdk_env.sh
./scripts/start_capture_emulator.sh    # если нет device
./scripts/recapture_store_screenshots.sh
```

Скрипт: `assembleDebug` → `installDebug` → каталог RU → каталог EN → `pack_store_upload` → PNG gate → `validate_store_upload` → `docs/screenshots/CAPTURE_BUILD_STAMP.txt`.

Флаги: `--skip-gates` — только съёмка и pack.

## Пошагово (вручную)

```bash
./gradlew assembleDebug
./gradlew :app:installDebug

# Полный каталог (34 экрана × локаль)
./scripts/capture_screenshot_catalog.sh ru
./scripts/capture_screenshot_catalog.sh en
# или:
./gradlew captureScreenshotCatalogRu captureScreenshotCatalogEn

# Listing 7 (те же файлы, копируются в upload-пакет)
./scripts/pack_store_upload.sh

# Gates
python3 scripts/verify_ui_inventory.py --require-screenshots --fail-on-placeholders
./scripts/validate_store_upload.sh
# или:
./gradlew verifyLoveTestBeforeStore
```

## 7 listing PNG — что проверить глазами

| screen_id | Дисклеймер / честность |
|-----------|------------------------|
| `hub_main` | Один заголовок приложения, отступ от status bar |
| `love_test_input` | Ввод имён |
| `love_test_result` | **Дисклеймер** «только для развлечения» на результате |
| `protocol_input` | Ввод протокола |
| `protocol_result` | **Дисклеймер** на результате |
| `wheel_spin` | Подпись fun / not a bet (note3) |
| `premium_paywall` | IAP optional |

Пути после pack: `build/store-upload/listing-screenshots/ru|en/*.png`.

## Требования к эмулятору

- API **34+**, портрет **1080×1920**, light theme
- `adb devices` — ровно одно `device`
- RU: `ru-RU`, EN: `en-US` (скрипт каталога переключает `cmd locale set-app-locales`)

## CI-артефакт (без локального adb)

```bash
python3 scripts/apply_emulator_screenshot_artifact.py ./ci-screenshots --locales ru en
./scripts/pack_store_upload.sh
./gradlew verifyLoveTestBeforeStore
```

Съёмка в flat-dir: `./scripts/capture_screenshot_catalog.sh en --out-dir ci-screenshots`

## Не делать

- Не копировать старые PNG «для прохождения gate» без новой съёмки
- Не коммитить placeholder &lt; 32 KB
- Не использовать `--no-daemon` для тяжёлых gradle-задач (см. `AGENT_STATUS.md`)
