# AGENT_STATUS — TestCompabilityLove (Love Test, dev.lovetest.app)

> Живой статус для центрального агента-оркестратора и пользователя.
> Обновляй ПОСЛЕ каждого осмысленного изменения. Новые записи журнала — сверху.

## Снимок
- **Стадия:** UI-полировка в git; **Store PNG** — пересъёмка на исправленной сборке (`recapture_store_screenshots.sh`); Production NO-GO до prod keystore + Closed smoke.
- **Пакет:** `build/store-upload/` · ZIP `build/love-tester-store-upload.zip` (~18 MB) · `validate_store_upload` OK (listing PNG пока **до** UI-фиксов).
- **AAB:** `app-release.aab` (~8.8 MB) + `mapping.txt` — подпись **debug upload key** (prod keystore — локально).
- **Git / CI / Pages:** push и живой privacy — по желанию до Production.
- **Следующий шаг:** `./scripts/recapture_store_screenshots.sh` → Internal testing AAB.
- **Последняя сборка:** `assembleDebug` OK · `lintDebug` (app + core:ui) OK (2026-06-04).

## Дисциплина сборки
- `./gradlew` без `--no-daemon`; одна R8/bundle за раз — ждать `[cursor-build-lock]`.
- `org.gradle.java.home` — только локально (закомментирован в git для CI ubuntu).

## Журнал
- 2026-06-04 — Промпт 4/7: коммит UI-полировки (NoActionBar, insets, hero heightIn, muted low %, экраны hub/splash/onboarding/settings + results + pair/letters/victory input); скрипты пересъёмки PNG; `.gitignore` — секреты не в staging.
- 2026-06-04 — Промпт 3/7: пересъёмка Store PNG **не выполнена** (нет adb/device); `docs/screenshots/` **не изменены** (34+34 от ~05-24, до UI-фиксов). PNG gate + `validate_store_upload` OK на **старых** кадрах. Добавлены `recapture_store_screenshots.sh`, `RECAPTURE_AFTER_UI_FIX.md`.
- 2026-06-04 — Промпт 2/7: adb нет в среде агента — runtime QA не прогонялся; статический аудит фиксов OK; `scripts/capture_visual_qa_ru.sh` + `*_result_low` в DebugUiPreview для пересъёмки muted hero.
- 2026-06-04 — Промпт 1/7: `assembleDebug` OK (~3m39s, daemon, build-lock); compile-фиксов не потребовалось (`LoveScoreCalculator.isHighScore` уже в companion). `lintDebug` app + core:ui OK; замечаний lint по изменённым UI/Kotlin файлам нет. APK: `app/build/outputs/apk/debug/app-debug.apk` (~25 MB).
- 2026-06-04 — Промпт 10: `PRODUCTION_ROLLOUT.md`, `POST_RELEASE_PLAN.md`, `V2_BACKLOG.md`; go/no-go NO-GO prod до signing/closed.
- 2026-06-04 — Промпт 8 (продолжение): AAB собран; upload-пакет полный, validate OK. Prod keystore: mismatch пароля → временно debug key.
- 2026-06-03 — Промпт 7: ASO-тексты RU/EN в PLAY_CONSOLE_COPY; 7 listing PNG честны (1080×1920, disclaimer на result/protocol/wheel); PNG gate OK; adb нет — пересъёмка не делалась.
- 2026-06-03 — Промпт 6: Data safety + IARC сверены с кодом (v1 без ads/аналитики); `PLAY_FORMS_FILLED.md`, обновлены DATA_SAFETY_FORM, IARC_QUESTIONNAIRE, PLAY_CONSOLE_COPY.
- 2026-06-03 — Промпт 5: аудит Billing 8.3 (`remove_ads`, acknowledge, restore); ads off (`ADS_ENABLED=false`, AD_ID manifest не мержится); DATA_SAFETY уточнён (v1 без AD ID). `verifyLoveTest` — в очереди gradlew lock.
- 2026-06-03 — Промпт 4: проверен `.gitignore` + `validate_git_staging`; commit `8638dcc` (AGENT_STATUS, orchestration.mdc, gradlew lock, CI-safe gradle.properties, `.kotlin/` в ignore). **Push failed** (no GitHub credentials). Инструкция push — пользователю.
- 2026-06-03 (вечер) — AGENT_STATUS, privacy URL, prod keystore, AAB не собран.
- 2026-06-03 — GO_LIVE_PLAN, монетизация v1, git init + 3 commits, remote настроен.
