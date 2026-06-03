# AGENT_STATUS — TestCompabilityLove (Love Test, dev.lovetest.app)

> Живой статус для центрального агента-оркестратора и пользователя.
> Обновляй ПОСЛЕ каждого осмысленного изменения. Новые записи журнала — сверху.

## Снимок
- **Стадия:** #1 в очереди релизов; код/Store-артефакты готовы; до Internal testing — push + живой privacy + release AAB + upload-пакет.
- **Готовность к Play:** ~85%. Код/тесты ✅ (49 unit · 65 instrumented: 56 Compose UI + 9 route smoke), Store PNG 67/67 ✅, audit P0/P1/P2 ✅, feature graphic ✅.
- **Монетизация v1:** premium-only (`lovetest.ads.enabled=false`); AdMob/UMP в коде, выкл в release. См. `docs/product/PRODUCT_DECISIONS.md` §2.
- **Privacy:** URL в `gradle.properties` → `https://maksimsokolov.github.io/TestCompabilityLove/` (не example.com). **Pages ещё 404** — нужны `git push` + workflow «Privacy GitHub Pages». Legal HTML/export ✅ (`exportPrivacyForHosting`).
- **Подпись:** production upload key ✅ `build/keystore/lovetest-upload.jks` + `keystore.properties` (в `.gitignore`). **Backup `.jks` обязателен** (потеря = нельзя обновлять приложение).
- **Блокеры:** 1) git push на `origin` (remote настроен, 3+ локальных commit); 2) GitHub Pages не задеплоен (curl 404); 3) **release AAB не собран** (`app/build/outputs/bundle/release/` пусто); 4) upload-пакет `build/store-upload/` пустой.
- **Следующий шаг:** `git push -u origin main` → Pages (Settings → Pages → GitHub Actions → Run workflow) → `./scripts/post_privacy_setup.sh https://maksimsokolov.github.io/TestCompabilityLove/` → `./gradlew bundleReleaseLoveTest` (лок gradlew, **без** `--no-daemon`) → `./scripts/finalize_store_release.sh` → Internal testing.
- **Последняя сборка:** `bundleReleaseLoveTest` / `:app:bundleRelease` — **неуспешно** (прервано/OOM, exit 137; очередь `[cursor-build-lock]`). `storeReadyLoveTest` / `verifyLoveTest` ранее — OK. Свежего `app-release.aab` и `mapping.txt` нет.

## Дисциплина сборки (напоминание)
- Только `./gradlew` **без** `--no-daemon`; одна тяжёлая сборка (R8/bundle) за раз — ждать лок, не дублировать.
- JDK 17: `org.gradle.java.home` в `gradle.properties` — не менять.

## Журнал
- 2026-06-03 (вечер) — Обновлён AGENT_STATUS по факту: privacy URL в конфиге, audit P2 закрыт; prod keystore создан; Pages 404; AAB не собран (сборка убита/прервана). Остаток: push → Pages → bundleReleaseLoveTest → pack → Play Internal.
- 2026-06-03 — Промпт 1–2: `GO_LIVE_PLAN.md`, монетизация v1 зафиксирована; legal HTML (v1 без рекламы); git init + commits; remote `maksimsokolov/TestCompabilityLove`; `set_privacy_url` на GitHub Pages.
- 2026-06-03 — Сид оркестратором: проект почти готов; остаток = privacy + keystore + push.
