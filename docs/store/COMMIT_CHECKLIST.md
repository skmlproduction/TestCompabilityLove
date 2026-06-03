# Git — что коммитить перед Play Console

## Обязательно в репозитории

| Путь | Зачем |
|------|--------|
| `docs/screenshots/ru/*.png` | Store listing RU (67 PNG каталога) |
| `docs/screenshots/en/*.png` | Store listing EN |
| `docs/store/feature_graphic_love_tester_m3.svg` | Feature graphic (источник) |
| `docs/product/screens_catalog.csv` | Inventory + CI gate |
| `app/src/main/assets/legal/*.html` | Privacy / Data safety |

CI проверяет PNG gate: `.github/workflows/ci.yml`, `store-png-gate.yml`.

**Тесты (локально, adb):**

| Команда | Что |
|---------|-----|
| `./gradlew verifyLoveTest` | 36 unit + inventory (CI) |
| `./scripts/run_route_smoke_tests.sh` | 7 route smoke (~1 мин) |
| `./scripts/run_compose_ui_tests.sh` | 49 Compose UI |

Workflow: `compose-ui-tests.yml`.

## Не коммитить (.gitignore)

| Путь | Причина |
|------|---------|
| `keystore.properties` | Секреты подписи |
| `*.jks` / `build/keystore/` | Upload keys |
| `local.properties` | Локальный SDK |
| `build/store-upload/` | Генерируется `./scripts/pack_store_upload.sh` |
| `Love Test - Compatibility Test_*_Decompiler.com/` | Референс 245MB — не в git |
| `.vscode/` | Локальные настройки IDE |
| `core/domain/bin/` | Артефакты Kotlin compile |

## Первый commit + push

```bash
./scripts/first_push.sh USER REPO          # remote + suggest + prepare (без commit)
# или по шагам:
./scripts/onboard_release.sh USER REPO
./scripts/validate_git_staging_cached.sh   # нет build/secrets в staging
./scripts/suggest_first_commit.sh          # dry-run + validate
./scripts/prepare_git_push.sh              # gate перед commit
git add .
git commit -m "Love Tester — store ready"
git push -u origin main
./scripts/post_push.sh                       # Pages → privacy → keystore → upload
```

`first_push.sh` не делает commit — только remote + validate + prepare. Флаг `--fast` пропускает медленный `verifyLoveTestBeforeStore`.

## После смены privacy URL

```bash
./scripts/post_privacy_setup.sh https://USER.github.io/REPO/
git add gradle.properties   # только если URL не секрет
./gradlew bundleReleaseLoveTest
```

## Рекомендуемый commit перед upload

```bash
./scripts/setup_github_remote.sh USER REPO   # первый раз
./scripts/prepare_git_push.sh
git add .
git status
git commit -m "Love Tester — store ready"
git push -u origin main
```

См. [PLAY_READY.md](./PLAY_READY.md).
