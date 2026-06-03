# Love Tester

Kotlin · Jetpack Compose · Material 3 · `dev.lovetest.app`

Пересборка «теста совместимости» как нативное Android-приложение (оригинал — Unity; см. `docs/product/REFERENCE_SOURCES.md`).

## Быстрый старт

```bash
./gradlew :app:installDebug
./gradlew verifyLoveTest          # unit + lint + UI/test inventory
./gradlew countTestsLoveTest      # 36 unit · 56 instrumented (49+7)
./scripts/project_health.sh       # быстрая сводка без полной сборки
```

Debug-превью экрана:

```bash
./scripts/open_debug_screen.sh hub_main
```

## Play Console

**Готовность:** [docs/store/PLAY_READY.md](docs/store/PLAY_READY.md)

```bash
./scripts/first_push.sh USER REPO        # remote + gate до commit (--fast = без полной сборки)
./scripts/post_push.sh                   # после git push: Pages → privacy → keystore
./scripts/onboard_release.sh USER REPO   # первый раз: конфиг + health
./scripts/suggest_first_commit.sh        # dry-run + validate staging
./scripts/project_health.sh              # быстрая сводка
./scripts/print_store_checklist.sh
./scripts/play_console_next.sh           # один следующий шаг
./scripts/check_pr.sh                       # как CI локально
./gradlew checkPrLoveTest                   # то же через Gradle
./scripts/prepare_git_push.sh               # gate перед первым push
./gradlew finalizeStoreReleaseLoveTest
```

| Блок | Статус |
|------|--------|
| UI 34 экрана, RU+EN | ✅ |
| Тесты 36 unit · 49 Compose UI + 7 route | ✅ |
| Store PNG 67/67 | ✅ |
| AAB + mapping | ✅ |
| Privacy HTTPS URL | ⏳ |
| Production keystore | ⏳ |
| git remote + first commit | ⏳ `./scripts/suggest_first_commit.sh` |

## Store upload

```bash
./scripts/finalize_store_release.sh          # финальный gate
# → build/store-upload/ (AAB, скрины, тексты)
```

См. [docs/store/STORE_UPLOAD.md](docs/store/STORE_UPLOAD.md) · [INTERNAL_TESTING.md](docs/store/INTERNAL_TESTING.md)

## Документация

| Документ | Назначение |
|----------|------------|
| [PROJECT_STATUS.md](docs/product/PROJECT_STATUS.md) | Статус фаз |
| [PLAY_READY.md](docs/store/PLAY_READY.md) | Чеклист Play |
| [DESIGN_SYSTEM.md](docs/design/DESIGN_SYSTEM.md) | M3 токены |
| [WORKFLOW.md](docs/screenshots/WORKFLOW.md) | Съёмка PNG |

## Конфигурация

`gradle.properties` (см. `gradle.properties.example`):

- `lovetest.privacy.policy.url` — HTTPS для Play
- `lovetest.billing.product.ids` — SKU через запятую
- `lovetest.ads.enabled` — реклама (MVP: `false`)

Подпись: `keystore.properties` · `./scripts/generate_upload_keystore.sh`
