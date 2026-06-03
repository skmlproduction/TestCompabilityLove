# Screenshot workflow — F5

## 1. Placeholders

```bash
./gradlew materializeScreenshotPlaceholders
```

Создаёт `docs/screenshots/ru|en/*.png` (1080×1920, ~10 KB) для inventory и CI до съёмки.

## 2. Capture (device / эмулятор)

**Автоматически (рекомендуется):**

```bash
./scripts/setup_android_sdk.sh              # проверка adb
./scripts/capture_store_local.sh both full  # AVD 1080×1920 + эмулятор + весь каталог
./gradlew verifyLoveTestBeforeStore
```

**Вручную:**

Полный pipeline:

```bash
./scripts/init_store_config.sh
./gradlew releaseGateLoveTest
./gradlew captureScreenshotCatalogRu
./gradlew captureScreenshotCatalogEn
./gradlew verifyLoveTestBeforeStore
```

Скрипт ставит debug APK, для каждого `screen_id` из CSV:

```bash
adb shell am start -n dev.lovetest.app/.debug.DebugUiPreviewActivity \
  --es lovetest.intent.extra.DEBUG_UI_PREVIEW <screen_id>
adb exec-out screencap -p > docs/screenshots/<locale>/<screen_id>.png
```

## 2b. Preflight (без adb)

```bash
./scripts/preflight_store.sh
./scripts/setup_android_sdk.sh          # где искать adb
./gradlew captureReadinessLoveTest
```

## 2c. CI-артефакт → репозиторий

Если PNG сняты на CI/другой машине как `screen_id.png`:

```bash
python3 scripts/apply_emulator_screenshot_artifact.py ./ci-screenshots --locales en
python3 scripts/apply_emulator_screenshot_artifact.py ./ci-screenshots --locales ru en
./gradlew verifyLoveTestBeforeStore
```

Съёмка в flat-каталог: `./scripts/capture_screenshot_catalog.sh en --out-dir ci-screenshots`

## 3. Verify

| Задача | Назначение |
|--------|------------|
| `verifyLoveTest` | PR: compile + lint + inventory |
| `verifyLoveTestBeforeStore` | + PNG на месте, не шаблоны |

## 4. Play Console

Загрузите 8–10 лучших кадров из `docs/screenshots/ru/` и `en/` (см. `CAPTURE_CHECKLIST.md`).

## Вспомогательные скрипты

```bash
./scripts/list_screenshot_screens.sh          # список screen_id
./scripts/open_debug_screen.sh hub_main       # открыть экран на устройстве
./scripts/adb_screenshot_preview.sh hub_main ru
```

## Troubleshooting

- **Чёрный кадр** — увеличьте `WAIT_SEC=4 ./scripts/adb_screenshot_preview.sh hub_main ru`
- **Неверная локаль** — `adb shell cmd locale get-app-locales dev.lovetest.app`
- **Onboarding вместо hub** — используйте `DEBUG_UI_PREVIEW`, не обычный cold start
