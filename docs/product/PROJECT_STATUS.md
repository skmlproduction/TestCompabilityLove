# Статус проекта — Love Tester

Обновлено: 2026-05-24 (Store PNG ✅, session persist, Compose UI tests)

## Готово к Play (privacy + keystore + AAB ✅ — 2026-07-22)

См. **[docs/store/PLAY_READY.md](../store/PLAY_READY.md)**

**Качество:** 36 unit tests · 56 instrumented (49 Compose UI + 7 route smoke) · session snapshot restore

## Готово

| Фаза | Содержание |
|------|------------|
| F0–F1 | PRD, каталог экранов, nav matrix |
| F2 | **56 SVG** (29 MVP RU + протокол + **20 EN** incl. protocol №52–56) |
| F3 | `:app`, `:core:ui`, `:core:domain`, Koin, DataStore, NavHost (**25 routes**) |
| F4 | UI 1:1 для screen_id 1–34, DebugUiPreview, share sheets |
| F5 (код + PNG) | Screenshot infra, **67/67 PNG**, upload pack |
| F6 | audit, CI gate; **session persist**, a11y, Compose tests |

## Команды

```bash
./scripts/check_pr.sh              # PR: audit (P0) + verifyLoveTest
./gradlew storeReadyLoveTest       # preflight + verify (без PNG gate)
./gradlew releaseGateLoveTest      # verify + audit P0 + store checklist
./scripts/init_store_config.sh     # gradle.properties + debug keystore если placeholder
./scripts/generate_debug_upload_keystore.sh  # локальный upload key (smoke-test)
./scripts/setup_android_sdk.sh     # подсказка по adb PATH
./gradlew bundleReleaseLoveTest    # verify + bundleRelease
./gradlew verifyLoveTest           # compile, lint, unit tests
./gradlew verifyLoveTestRelease    # + release AAB/APK
./gradlew -PscreenId=protocol_input openLoveTestDebugScreen
./gradlew printStoreChecklistLoveTest   # статус Play Console
./gradlew finalizeStoreReleaseLoveTest  # финальный gate Play
./gradlew packStoreUploadLoveTest       # build/store-upload/
./gradlew capturePriorityScreensLoveTest  # 7 экранов для листинга (adb)
./gradlew captureReadinessLoveTest       # DEBUG preview + PNG stats + adb
./gradlew captureScreenshotCatalogRu   # нужен adb + эмулятор
./gradlew captureScreenshotCatalogEn
./scripts/capture_store_local.sh both priority  # AVD + эмулятор + съёмка
./gradlew captureStoreLocalLoveTest             # Gradle-обёртка
./gradlew verifyLoveTestBeforeStore      # gate: PNG без placeholder
./gradlew exportPrivacyForHosting       # build/legal-host/ для GitHub Pages
python3 scripts/generate_protocol_en_svgs.py  # EN SVG протокола из RU
./gradlew playConsoleNextLoveTest        # один следующий шаг Play
./gradlew countTestsLoveTest             # unit / Compose UI inventory
./gradlew projectHealthLoveTest          # быстрая сводка без сборки
./gradlew firstPushLoveTest -PgithubUser=USER -PgithubRepo=REPO  # до первого push
./gradlew postPushLoveTest                 # после git push
./scripts/post_push.sh
./gradlew checkPrLoveTest                # как CI (перед PR)
./scripts/check_pr.sh
./gradlew prepareGitPushLoveTest         # gate перед первым push
./gradlew runRouteSmokeTestsLoveTest      # route smoke (adb, быстро)
./gradlew runComposeUiTestsLoveTest       # Compose UI (эмулятор)
```

Store: `docs/store/README.md` · `docs/store/STORE_UPLOAD.md`

## До Play Console

1. ~~Privacy URL~~ ✅ `https://skmlproduction.github.io/TestCompabilityLove/`
2. ~~Скриншоты~~ ✅ **67/67** реальных PNG
3. ~~Подпись / AAB~~ ✅ upload key + `build/store-upload/`
4. ~~Git / Pages~~ ✅ `skmlproduction/TestCompabilityLove`
5. **Console** — создать app, listing, Data safety, IARC, SKU `remove_ads`, Internal upload — см. [INTERNAL_UPLOAD_NOW.md](../store/INTERNAL_UPLOAD_NOW.md) · [BILLING_SETUP.md](../store/BILLING_SETUP.md)

## Аудит (типично)

- P0: 0
- P1: 0 (Store PNG готовы)
- P2: 0 (privacy URL → `https://skmlproduction.github.io/TestCompabilityLove/`; Pages live 2026-07-22)

## CI

| Workflow | Назначение |
|----------|------------|
| `ci.yml` | audit P0 → `verifyLoveTest` → PNG gate |
| `compose-ui-tests.yml` | ручной Compose UI (adb) |
| `release-assemble.yml` | `verifyLoveTestRelease` + AAB/APK |
| `emulator-screenshots.yml` | ручная съёмка PNG (adb) |
| `pages-privacy.yml` | deploy privacy на GitHub Pages |

## v2 (не в MVP)

- AdMob + UMP SDK (production)
- Deep links / App Links
