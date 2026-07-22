# Go-live plan — Love Tester (остаток до Play)

> **2026-07-22:** Privacy **200** · AAB/pack **готовы** · P1 shipped · `print_store_checklist` без блокеров.  
> **Сейчас:** [INTERNAL_UPLOAD_NOW.md](./INTERNAL_UPLOAD_NOW.md) → Play Internal → [CLOSED_IAP_SMOKE.md](./CLOSED_IAP_SMOKE.md).

Обновлено: **2026-06-03** (финальный аудит) + статус 2026-07-22 сверху.  
Монетизация v1: **[PRODUCT_DECISIONS.md](../product/PRODUCT_DECISIONS.md) §2** — premium-only, реклама выкл.

Легенда: **код** = локально / репозиторий · **консоль** = Google Play Console · **календарь** = внешние зависимости (время, люди).

---

## Что уже готово (не в остатке)

| Область | Статус |
|---------|--------|
| Код, NavHost, тесты | ✅ `verifyLoveTest` · `storeReadyLoveTest` (2026-06-03) |
| Store PNG 67/67, feature graphic | ✅ |
| Legal export | ✅ `exportPrivacyForHosting` |
| Audit P0/P1, upload-тексты | ✅ в `docs/store/` |
| AdMob/UMP в коде | ✅ за флагом `lovetest.ads.enabled=false` |

---

## Упорядоченный остаток

### Фаза A — репозиторий и privacy (блокирует листинг)

| # | Шаг | Тип | Команда / артефакт |
|---|-----|-----|-------------------|
| A1 | Первый commit + GitHub remote | **код** | `./scripts/first_push.sh USER REPO` → `git add . && git commit` → `git push -u origin main` |
| A2 | GitHub Pages для privacy | **код** + **календарь** | Settings → Pages → Actions → workflow `pages-privacy.yml` |
| A3 | Реальный Privacy HTTPS URL | **код** | `./scripts/post_privacy_setup.sh https://USER.github.io/REPO/` (сейчас: `example.com`) |
| A4 | Синхрон URL в сборке | **код** | `lovetest.privacy.policy.url` в `gradle.properties` ≠ example.com |

*Без A3–A4 Play не примет Store listing / Privacy policy.*

### Фаза B — подпись и артефакт загрузки

| # | Шаг | Тип | Команда / артефакт |
|---|-----|-----|-------------------|
| B1 | Production upload keystore | **код** + **календарь** | `LOVETEST_KEYSTORE_PASS=… ./scripts/generate_upload_keystore.sh` · backup `build/keystore/lovetest-upload.jks` |
| B2 | Release AAB + mapping | **код** | `./gradlew bundleReleaseLoveTest` |
| B3 | Upload-пакет | **код** | `./scripts/finalize_store_release.sh` → `build/store-upload/` или `build/love-tester-store-upload.zip` |

*Сейчас: debug keystore, AAB не собран, upload pack пустой (`print_store_checklist.sh`).*

### Фаза C — Play Console (первый трек: Internal testing)

| # | Шаг | Тип | Документ |
|---|-----|-----|----------|
| C1 | Create app (free, App) | **консоль** | [INTERNAL_TESTING.md](./INTERNAL_TESTING.md) §1 |
| C2 | Store listing RU + EN | **консоль** | `PLAY_CONSOLE_COPY.md` · скрины `listing-screenshots/` |
| C3 | Privacy policy URL | **консоль** | = A3 |
| C4 | **Ads declaration: No** | **консоль** | v1 `ads.enabled=false` — см. PRODUCT_DECISIONS §2 |
| C5 | Data safety | **консоль** | `DATA_SAFETY_FORM.md` — без AD ID в v1 |
| C6 | IARC / Content rating | **консоль** | `IARC_QUESTIONNAIRE.md` — Entertainment, **без рекламы** в v1 |
| C7 | Target audience, App access | **консоль** | 13+, full access |
| C8 | Internal testing release | **консоль** | Upload `app-release.aab` + `mapping.txt` · `RELEASE_NOTES_v1.0.0.md` |
| C9 | Smoke на устройстве | **календарь** | Чеклист INTERNAL_TESTING §5 |

### Фаза D — после Internal (не блокирует первую заливку)

| # | Шаг | Тип | Примечание |
|---|-----|-----|------------|
| D1 | Closed testing + Billing | **консоль** | SKU `remove_ads`, license testers |
| D2 | Включить рекламу (опционально) | **код** + **консоль** | `lovetest.ads.enabled=true` · UMP · **пересдать** Data safety + IARC + Ads: Yes |
| D3 | Production | **консоль** + **календарь** | После closed + `releaseGateLoveTest` |

---

## Автопроверки (эталон на дату аудита)

```bash
./scripts/project_health.sh          # OK, кроме privacy/keystore/git/AAB/pack
./scripts/print_store_checklist.sh   # 3 блокера Play (см. ниже)
./scripts/play_console_next.sh       # шаг 1/5: git push
./gradlew verifyLoveTest storeReadyLoveTest   # OK (повтор при OOM daemon — без --no-daemon)
```

| Проверка | Результат 2026-06-03 |
|----------|----------------------|
| Tests | 49 unit · 65 instrumented (56 Compose UI + 9 route smoke) |
| Store PNG | 67/67 ✅ |
| Privacy URL | ✅ `https://skmlproduction.github.io/TestCompabilityLove/` (2026-07-22) |
| Keystore | ✅ production upload key (`print_upload_cert_sha.sh`) |
| Release AAB / upload pack | ✅ `build/store-upload/` refreshed 2026-07-22 |
| git | ✅ `skmlproduction/TestCompabilityLove` |

---

## Что блокирует первую заливку в Internal testing

**Жёсткие блокеры (в порядке выполнения):**

1. **Privacy HTTPS URL** — сейчас placeholder; без него нельзя завершить Store listing. *(код → A2–A4)*
2. **Подписанный release AAB** — нужен production upload key (debug key не для продакшен-трека). *(код → B1–B2)*
3. **Upload-пакет** — собрать после B2. *(код → B3)*
4. **Play Console** — создать приложение, листинг, App content (Data safety, IARC, **Ads: No**), загрузить AAB на Internal. *(консоль → C1–C8)*

**Не блокирует Internal AAB, но нужно до публикации листинга:**

- **git push** — для автоматического privacy через GitHub Pages (A1–A2); альтернатива — свой HTTPS-хостинг privacy без push.
- **Реальная покупка Premium** — closed testing (D1), не Internal.
- **AdMob** — v1.2 / closed (D2), не Internal.

**Не блокирует (уже сделано):** код, тесты, скриншоты, feature graphic, тексты форм.

---

## Ссылки

| Документ | Назначение |
|----------|------------|
| [PLAY_READY.md](./PLAY_READY.md) | Сводка готовности |
| [INTERNAL_TESTING.md](./INTERNAL_TESTING.md) | Первый релиз |
| [STORE_UPLOAD.md](./STORE_UPLOAD.md) | Полный pipeline |
| [PRODUCT_DECISIONS.md](../product/PRODUCT_DECISIONS.md) | Монетизация v1 |
