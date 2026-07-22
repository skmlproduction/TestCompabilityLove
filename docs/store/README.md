# Store — документация Love Tester

| Файл | Назначение |
|------|------------|
| [PLAY_READY.md](./PLAY_READY.md) | **Сводка готовности к Play** |
| [INTERNAL_UPLOAD_NOW.md](./INTERNAL_UPLOAD_NOW.md) | **Сейчас:** пути AAB, Privacy URL, cert SHA |
| [STORE_UPLOAD.md](./STORE_UPLOAD.md) | Пошаговая загрузка AAB и скриншотов |
| [LISTING_DRAFT.md](./LISTING_DRAFT.md) | Тексты листинга RU/EN |
| [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md) | Черновик ответов Data safety |
| [FEATURE_GRAPHIC.md](./FEATURE_GRAPHIC.md) | Feature graphic 1024×500 + SVG |
| [RELEASE_NOTES_v1.0.0.md](./RELEASE_NOTES_v1.0.0.md) | Release notes 1.0.0 |
| [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md) | Готовые блоки текста для вставки |
| [INTERNAL_TESTING.md](./INTERNAL_TESTING.md) | Первый релиз Internal track |
| [BILLING_SETUP.md](./BILLING_SETUP.md) | SKU remove_ads в Play Console |
| [CLOSED_IAP_SMOKE.md](./CLOSED_IAP_SMOKE.md) | **Closed:** purchase + restore smoke (ручной) |
| [IARC_QUESTIONNAIRE.md](./IARC_QUESTIONNAIRE.md) | Черновик Content rating (IARC) |
| [PRIVACY_HOSTING.md](./PRIVACY_HOSTING.md) | GitHub Pages / HTTPS privacy |

Скрипты:
- `./scripts/setup_github_remote.sh USER REPO` · `./gradlew -PgithubUser= -PgithubRepo= setupGithubRemoteLoveTest`
- `./scripts/onboard_release.sh [USER REPO]` · `./gradlew onboardReleaseLoveTest`
- `./scripts/post_push.sh` · `./gradlew postPushLoveTest` — после git push
- `./scripts/first_push.sh USER REPO [--fast]` · `./gradlew firstPushLoveTest`
- `./scripts/list_git_staging.sh` — один dry-run git add
- `./scripts/validate_git_staging_cached.sh` — validate staging (1× dry-run)
- `./scripts/check_pr.sh` · `./gradlew checkPrLoveTest` — как CI (audit + staging + verify)
- `./scripts/validate_git_staging.sh [--paths-file FILE]`
- `./scripts/suggest_first_commit.sh` · `./gradlew suggestFirstCommitLoveTest` — dry-run первого commit
- `./scripts/project_health.sh` · `./gradlew projectHealthLoveTest` — быстрая сводка
- `./scripts/count_tests.sh` — unit / Compose UI inventory
- `./scripts/verify_test_inventory.sh` · `./gradlew verifyTestInventoryLoveTest`
- `./gradlew countTestsLoveTest`
- `./scripts/run_route_smoke_tests.sh` · `./gradlew runRouteSmokeTestsLoveTest` — быстрый adb smoke
- `./scripts/run_compose_ui_tests.sh` · `./gradlew runComposeUiTestsLoveTest`
- `./scripts/play_console_next.sh` · `./gradlew playConsoleNextLoveTest` — следующий шаг до Play
- `./scripts/init_git_for_github.sh USER REPO` — git init + подсказка privacy URL
- `./scripts/init_store_config.sh` · `./gradlew initStoreConfigLoveTest`
- `./scripts/generate_debug_upload_keystore.sh` · `./gradlew generateDebugUploadKeystoreLoveTest`
- `./scripts/post_privacy_setup.sh https://…` · `./gradlew -PprivacyUrl=https://… postPrivacySetupLoveTest`
- `./scripts/generate_upload_keystore.sh` · production keystore
- `./scripts/print_store_checklist.sh` · `./gradlew printStoreChecklistLoveTest`
- `./scripts/pack_store_upload.sh` · `./gradlew packStoreUploadLoveTest`
- `./scripts/zip_store_upload.sh` · `./gradlew zipStoreUploadLoveTest`
- `./scripts/capture_priority_screens.sh` · `./gradlew capturePriorityScreensLoveTest`
- `./scripts/release_gate.sh` · `./gradlew releaseGateLoveTest`
- `./scripts/finalize_store_release.sh` · `./gradlew finalizeStoreReleaseLoveTest`

См. также [../product/GOOGLE_PLAY_RELEASE_CHECKLIST.md](../product/GOOGLE_PLAY_RELEASE_CHECKLIST.md).
