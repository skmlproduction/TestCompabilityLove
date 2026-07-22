# Internal testing — ранбук заливки (Love Tester v1.0)

Пакет: `build/store-upload/` · ZIP: `build/love-tester-store-upload.zip`  
Package: `dev.lovetest.app` · Монетизация: premium-only, **Ads: No**, IAP `remove_ads`.

---

## A. Локально — собрать upload-пакет

```bash
cd /path/to/TestCompabilityLove

# 1. Gate (без --no-daemon)
./gradlew verifyLoveTestBeforeStore
./gradlew bundleRelease                    # или bundleReleaseLoveTest

# 2. Финальный pack
./scripts/pack_store_upload.sh
./scripts/validate_store_upload.sh

# Полный gate (долго):
./gradlew finalizeStoreReleaseLoveTest
# = ./scripts/finalize_store_release.sh
```

### Ожидаемое содержимое `build/store-upload/`

| Файл / папка | Назначение |
|--------------|------------|
| `app-release.aab` | Upload в Internal testing |
| `mapping.txt` | Deobfuscation (R8) |
| `feature_graphic.png` | Store listing 1024×500 |
| `listing-screenshots/ru/` | 7 PNG |
| `listing-screenshots/en/` | 7 PNG |
| `PLAY_CONSOLE_COPY.md` | Тексты листинга |
| `DATA_SAFETY_FORM.md` | Data safety |
| `PLAY_FORMS_FILLED.md` | Сводка форм (добавьте в pack вручную при необходимости) |
| `RELEASE_NOTES_v1.0.0.md` | Release notes |
| `INTERNAL_TESTING.md` | Краткий чеклист |
| `UPLOAD_MANIFEST.txt` | Размеры файлов |

**Backup:** `build/keystore/lovetest-upload.jks` — потеря = нельзя обновлять приложение.

### ⚠️ Keystore (2026-06-04)

- **Production** `lovetest-upload.jks`: если Gradle `bad padding` — в `keystore.properties` **`keyPassword` должен совпадать с `storePassword`** (см. `build/keystore/CREDENTIALS.local.txt`). Проверка SHA: `./scripts/print_upload_cert_sha.sh`
- **Текущий AAB** в `build/store-upload/` подписан **production upload key** (`lovetest-upload.jks`). SHA-256 для Play Console → App integrity: см. `AGENT_STATUS.md`.

---

## B. Play Console — создать приложение

1. [play.google.com/console](https://play.google.com/console) → **Create app**
2. **App name:** EN `Love Test: Compatibility` · RU listing отдельно
3. **Default language:** Russian или English
4. **App or game:** App · **Free**
5. Declarations (ads, policies) — по мастеру

---

## C. Store listing (до или после Internal)

| Поле | Источник |
|------|----------|
| App name RU/EN | [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md) |
| Short / Full description | Там же |
| Screenshots RU | `listing-screenshots/ru/*.png` (7 шт.) |
| Screenshots EN | `listing-screenshots/en/` |
| Feature graphic | `feature_graphic.png` |
| Privacy policy URL | `https://skmlproduction.github.io/TestCompabilityLove/` (после Pages) |

---

## D. App content (Промпт 6)

| Раздел | Ответ v1 |
|--------|----------|
| **Privacy policy** | HTTPS URL (как в `gradle.properties`) |
| **Ads** | **No** |
| **App access** | All functionality available without special access |
| **Content rating** | Start questionnaire → [IARC_QUESTIONNAIRE.md](./IARC_QUESTIONNAIRE.md) · Entertainment, no gambling, purchases Yes |
| **Target audience** | 13+, not designed for children under 13 |
| **Data safety** | [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md) / [PLAY_FORMS_FILLED.md](./PLAY_FORMS_FILLED.md) |
| **News app / COVID / etc.** | No, если не применимо |

---

## E. Monetize — SKU `remove_ads`

1. **Monetize** → **Products** → **In-app products** → **Create**
2. **Product ID:** `remove_ads` (как в `gradle.properties`)
3. **Type:** One-time · **Active**
4. **Setup** → **License testing** — Gmail тестеров (для проверки покупки позже на Closed)

На **Internal** покупка может не тестироваться без license tester — это нормально для smoke.

---

## F. Internal testing — загрузка AAB

1. **Testing** → **Internal testing**
2. **Create new release** (или Manage track → Create release)
3. **Upload** → `build/store-upload/app-release.aab`
4. **Release name:** `1.0.0 (1)` internal
5. **Release notes** — из `RELEASE_NOTES_v1.0.0.md`
6. **Review release** → **Start rollout to Internal testing**
7. **Testers** → Internal testers list (до 100) — добавьте Gmail

### Mapping (deobfuscation)

После обработки bundle:

**Release** → выберите release → **App bundle explorer** → версия → **Downloads** / **Deobfuscation file** → upload `mapping.txt`

---

## G. После заливки — что проверить

### В Console

| Проверка | Где |
|----------|-----|
| **Pre-launch report** | Release → Pre-launch report (краши, совместимость, broken APK) |
| **Warnings** на release (жёлтые) | Signing, 64-bit, target API, privacy |
| **Policy status** | Dashboard → Policy status (Data safety, Ads, Content rating complete?) |
| **App bundle explorer** | Version code 1, signed with upload key |
| **Countries/regions** | Track доступен тестерам |

### На устройстве (Internal link / Play)

- [ ] Установка с Internal testing
- [ ] Cold start → onboarding → hub
- [ ] Love test + protocol + wheel
- [ ] Settings → Privacy → **HTTPS** (не example.com)
- [ ] Premium paywall открывается; «Продолжить бесплатно»
- [ ] RU / EN locale

### Типичные предупреждения (не всегда блокеры)

- Privacy URL 404 до деплоя Pages — исправить до Production
- Data safety incomplete — заполнить форму
- No testers — добавить internal testers
- Billing SKU inactive — Active для Closed testing

---

## H. Чеклист перед Closed testing

После успешного Internal smoke:

- [ ] Pre-launch report (Internal release) — без **critical**
- [ ] Policy status: Data safety + Content rating **complete**
- [ ] `./scripts/check_legal_urls.sh` — privacy, terms, data-collection → **HTTP 200**
- [ ] Settings → Privacy на устройстве открывает **HTTPS** (не example.com / 404)
- [ ] **License testing** — Gmail тестеров (Monetize → License testing)
- [ ] IAP **`remove_ads`** — **Active**, цена задана
- [ ] Closed track: покупка Premium + **Restore purchases** проверены
- [ ] Backup `build/keystore/lovetest-upload.jks` сохранён вне git
- [ ] App integrity → upload cert SHA = `AGENT_STATUS.md` (prod key)
- [ ] *(Рекомендуется)* Store PNG пересняты после UI-фиксов (`recapture_store_screenshots.sh`)

**v1 не нужно:** Ads declaration Yes, UMP, IARC gambling Yes, crash/analytics SDK.

---

## I. Go / No-Go и сводка

Финальный вердикт и «готово / вручную»: **[GO_NO_GO_V1.md](./GO_NO_GO_V1.md)**

См. также [STORE_UPLOAD.md](./STORE_UPLOAD.md) · [GO_LIVE_PLAN.md](./GO_LIVE_PLAN.md).
