# Go / No-Go — Love Tester v1.0.0

Package: `dev.lovetest.app` · `versionCode` 1 · IAP `remove_ads` · **Ads: No** (v1)

Сводка форм: [PLAY_FORMS_FILLED.md](./PLAY_FORMS_FILLED.md) · Заливка Internal: [INTERNAL_TESTING_RUNBOOK.md](./INTERNAL_TESTING_RUNBOOK.md)

---

## Вердикт (2026-07-22 — legal Pages live)

| Трек | Вердикт | Комментарий |
|------|---------|-------------|
| **Internal testing** | **GO** | Legal **200×3** на `skmlproduction.github.io`; critical 11/11; P1 QA 12/12; store pack ✅ |
| **Closed testing** | **GO после Internal** | + license testers, purchase/restore `remove_ads` — см. [CLOSED_IAP_SMOKE.md](./CLOSED_IAP_SMOKE.md) |
| **Production 100%** | **NO-GO** | Closed IAP smoke ещё не подтверждён |

## Вердикт (2026-07-20 — Full App Recheck)

| Трек | Вердикт | Комментарий |
|------|---------|-------------|
| **Internal testing** | **GO** | P1 UI ✅; critical suite **11/11 ✅** + P1 QA **12/12 FRESH** API34 (2026-07-20); store 34/34 RU+EN; Xiaomi QA 34/34; `validate_store_upload` ✅ |
| **Closed testing** | **GO после Internal** | + license testers, purchase/restore `remove_ads`, **privacy HTTP 200** |
| **Production 100%** | **NO-GO** | Legal URLs **404** — GitHub repo **404**; SSH key есть локально, **не добавлен в GitHub** (`Permission denied (publickey)`); Closed IAP smoke не подтверждён |

## Вердикт (2026-07-18 — stylish redesign v2)

| Трек | Вердикт | Комментарий |
|------|---------|-------------|
| **Internal testing** | **GO** | Design v2 + quality pass; RU+EN store PNG **34/34 @1080×1920**; Xiaomi QA **34/34**; `verifyLoveTestBeforeStore` ✅; **critical connected 42/42 ✅** (2026-07-18) |
| **Closed testing** | **GO после Internal** | + license testers, purchase/restore `remove_ads`, **privacy HTTP 200** |
| **Production 100%** | **NO-GO** | Legal URLs **404** — GitHub repo ещё **404** (создать + `git push`); агент: нет SSH/`gh auth`; Closed IAP smoke не подтверждён |

## Вердикт (2026-07-14 — production completion audit, архив)

| Трек | Вердикт | Комментарий |
|------|---------|-------------|
| **Internal testing** | **GO** | R8 release AAB + upload-пакет; RU+EN PNG **34/34 @1080×1920**; gates OK |
| **Closed testing** | **GO после Internal** | + license testers, purchase/restore `remove_ads`, **privacy HTTP 200** |
| **Production 100%** | **NO-GO** | Legal URLs **404** (git push + Pages); Closed IAP smoke не подтверждён |

---

## Вердикт (2026-06-04, архив)

## Уже готово (агент / репо)

| Область | Статус |
|---------|--------|
| Release AAB + `mapping.txt` | ✅ R8/minify ON · `build/store-upload/app-release.aab` (~8.4 MB) |
| Upload cert SHA-256 | ✅ см. `AGENT_STATUS.md` / `print_upload_cert_sha.sh` |
| `validate_store_upload` | ✅ |
| Тексты листинга RU/EN | ✅ `PLAY_CONSOLE_COPY.md` |
| 7 listing PNG × RU/EN | ✅ 1080×1920 post-polish (эмулятор, 2026-07-14) |
| Store PNG catalog | ✅ RU+EN 34/34 @1080×1920 (EN full AVD recapture 2026-07-18; архив `qa/emulator/{ru,en}`) · `audit_screens_matrix` P0=0 |
| Xiaomi device QA | ✅ `docs/screenshots/qa/xiaomi/ru` **34/34** full refresh 2026-07-18 (post-polish) |
| Store upload pack | ✅ `build/love-tester-store-upload.zip` · `validate_store_upload` OK |
| `verifyLoveTestBeforeStore` | ✅ (2026-07-18) — lint C1 fixed via `~/.gradle/gradle.properties` `-XX:-TieredCompilation` |
| Critical connected (11 classes / 42 tests) | ✅ hitchhike AVD `emulator-5560` (2026-07-18 evening) |
| Full recheck P1 critical (4 classes) | ✅ LoveTester_Capture API34 (2026-07-20 ~06:04) — Consent 2/2 + Calc/Protocol/Zodiac |
| Full recheck critical suite (11 classes) | ✅ LoveTester_Capture + `emp_ready_p1` (2026-07-20 ~06:51) — incl. A11y 9, DebugStart 20, Share, Wheel, Love |
| Full `connectedDebugAndroidTest` (~115) | 🟡 hardening landed (Koin `@Before` + scroll); re-run blocked by host RAM OOM |
| Data Safety / IARC ответы | ✅ `DATA_SAFETY_FORM.md`, `IARC_QUESTIONNAIRE.md`, `PLAY_FORMS_FILLED.md` |
| Ads v1 | ✅ **No** в коде и формах |
| Wheel ≠ gambling | ✅ IARC: Entertainment, no simulated gambling |
| Legal HTML + URL в конфиге | ✅ `gradle.properties` → GitHub Pages URL |
| UI-полировка | ✅ фазы 0–10 (2026-07-14): edge-to-edge, LoveFeatureTopBar, typography tokens |
| `check_legal_urls` | ✅ HTTP 200 ×3 (`skmlproduction.github.io/TestCompabilityLove`, 2026-07-22) |

---

## Внешний чеклист (только вы / CI)

```bash
# Legal уже live (2026-07-22): ./scripts/check_legal_urls.sh → 200×3
# Пакет: docs/store/INTERNAL_UPLOAD_NOW.md

# 1. Internal testing (Play Console)
# Upload build/store-upload/app-release.aab + mapping.txt
# См. INTERNAL_TESTING_RUNBOOK.md · INTERNAL_UPLOAD_NOW.md

# 2. Closed testing (после Internal smoke)
# License testers → purchase + restore remove_ads на устройстве
# См. CLOSED_IAP_SMOKE.md
```

---

## Вручную вам (блокеры / обязательно)

| # | Задача | Зачем |
|---|--------|-------|
| 1 | ~~Создать repo + Pages~~ | ✅ Legal **200×3** (`skmlproduction`) |
| 2 | **Создать приложение** в Play Console (если ещё нет) | Первый upload |
| 3 | Заполнить **App content**: Privacy URL, **Ads No**, IAP Yes, **13+** | Policy status green |
| 4 | **Data safety** — по `DATA_SAFETY_FORM.md` | Блокер релиза |
| 5 | **IARC** — по `IARC_QUESTIONNAIRE.md` (рулетка **нет**, wheel = fun) | Блокер релиза |
| 6 | **Monetize** → `remove_ads` **Active** | Closed: тест покупки |
| 7 | **Internal testers** (Gmail, до 100) | Установка с трека |
| 8 | Upload **AAB** + **mapping.txt** | См. [INTERNAL_UPLOAD_NOW.md](./INTERNAL_UPLOAD_NOW.md) |
| 9 | **App integrity** — upload certificate SHA | Совпадение с `print_upload_cert_sha.sh` |
| 10 | Smoke на устройстве (Internal link) | Onboarding, tests, Settings→Privacy HTTPS |
| 11 | *(Closed)* License testers + покупка/restore `remove_ads` | Перед Production |

---

## Пошагово: Internal testing

### Шаг 0 — Локально (опционально, если пересобираете)

```bash
cd /path/to/TestCompabilityLove
./gradlew verifyLoveTestBeforeStore
./gradlew :app:bundleRelease
./scripts/pack_store_upload.sh
./scripts/validate_store_upload.sh
```

### Шаг 1 — GitHub Pages (до заливки или сразу после)

```bash
git push -u origin main
# Actions → Privacy GitHub Pages → Run workflow
./scripts/check_legal_urls.sh
```

### Шаг 2 — Play Console: приложение и контент

1. [play.google.com/console](https://play.google.com/console) → **Create app** (App, Free).
2. **Store listing** — тексты и 7 PNG из `build/store-upload/listing-screenshots/{ru,en}/`, feature graphic.
3. **App content** (см. [PLAY_FORMS_FILLED.md](./PLAY_FORMS_FILLED.md)):
   - Privacy: `https://skmlproduction.github.io/TestCompabilityLove/`
   - Terms (если поле есть): `…/terms.html`
   - **Ads: No**
   - Target audience: **13+**, not for children under 13
4. **Data safety** — [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md): Name (optional, device), Purchase history (Play); **нет** Ad ID, analytics, crash SDK.
5. **Content rating (IARC)** — [IARC_QUESTIONNAIRE.md](./IARC_QUESTIONNAIRE.md):
   - Category: **Entertainment**
   - Gambling / simulated gambling: **No** (колесо — идеи, не ставки)
   - Purchases: **Yes** · Ads: **No**
6. **Monetize** → In-app product **`remove_ads`** → One-time → **Active**.

### Шаг 3 — Internal release

1. **Testing** → **Internal testing** → **Create new release**.
2. Upload `build/store-upload/app-release.aab`.
3. Release notes — `RELEASE_NOTES_v1.0.0.md`.
4. **Start rollout to Internal testing**.
5. **Testers** → добавить Gmail.
6. После обработки bundle: **App bundle explorer** → upload `mapping.txt`.

### Шаг 4 — Проверки после заливки

| Где | Что |
|-----|-----|
| Pre-launch report | Нет critical (crash, broken APK) |
| Policy status | Data safety + rating complete |
| App bundle explorer | versionCode 1, prod upload key |
| Устройство | Install → hub → love/protocol/wheel → Settings Privacy **200** → Premium «Продолжить бесплатно» |

---

## Чеклист перед Closed testing

Пошаговый smoke: **[CLOSED_IAP_SMOKE.md](./CLOSED_IAP_SMOKE.md)**.

- [ ] Internal smoke **OK** (≥1 устройство, RU; желательно + EN)
- [ ] **Privacy / terms / data-collection** — `check_legal_urls` → HTTP 200
- [ ] **Policy status** в Console без красных блокеров
- [ ] **Pre-launch** на Internal release без critical
- [ ] **License testing** — Gmail тестеров в Monetize → License testing
- [ ] SKU **`remove_ads`** Active, цена и локали заданы
- [ ] На Closed: покупка Premium → thank you; **Restore purchases**
- [ ] Скриншоты Store актуальны (после UI-фиксов, если переснимали)
- [ ] Backup **`lovetest-upload.jks`** вне репо (1Password / диск)
- [ ] Upload certificate SHA в App integrity совпадает с AGENT_STATUS

**Не требуется для Closed v1:** Ads UMP, AdMob, gambling declaration, crash SDK.

---

## Ссылки

- Production staged rollout: [PRODUCTION_ROLLOUT.md](./PRODUCTION_ROLLOUT.md)
- После релиза 72h: [POST_RELEASE_PLAN.md](./POST_RELEASE_PLAN.md)
- First push Pages: [GITHUB_FIRST_PUSH.md](./GITHUB_FIRST_PUSH.md)
