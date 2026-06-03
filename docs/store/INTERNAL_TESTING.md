# Internal testing — первый релиз в Play Console

Чеклист для трека **Internal testing** (smoke test перед closed/production).

## Перед загрузкой (локально)

```bash
./scripts/onboard_release.sh USER REPO   # первый раз
./scripts/project_health.sh
./scripts/print_store_checklist.sh
./gradlew verifyLoveTestBeforeStore    # PNG gate
./gradlew releaseGateLoveTest
./scripts/finalize_store_release.sh    # pack + validate + ZIP
# или: ./gradlew zipStoreUploadLoveTest
```

**Тесты (без Play upload):**

```bash
./gradlew verifyLoveTest               # unit + lint + inventory (CI)
./scripts/run_route_smoke_tests.sh     # 7 route smoke (adb, ~1 мин)
./scripts/run_compose_ui_tests.sh      # 49 Compose UI (adb, дольше)
```

Ожидаемо:

| Пункт | Статус |
|-------|--------|
| AAB signed | keystore.properties |
| mapping.txt | R8 |
| 67 Store PNG | verifyLoveTestBeforeStore |
| Privacy HTTPS | не example.com |
| feature_graphic.png | 1024×500 |

## Play Console — по порядку

### 1. Create app

- App name: **Love Tester** / **Тест на совместимость и любовь**
- Default language: Russian или English
- App / Game: **App**
- Free

### 2. Store listing (RU + EN)

Тексты: `PLAY_CONSOLE_COPY.md`, `LISTING_DRAFT.md`

Скриншоты (минимум 2, рекомендуем 8):

```
build/store-upload/listing-screenshots/ru/
build/store-upload/listing-screenshots/en/
```

Feature graphic: `build/store-upload/feature_graphic.png`

Privacy policy URL: тот же, что в `gradle.properties`

### 3. App content

- **Privacy policy** — HTTPS URL
- **Ads** — No (MVP `lovetest.ads.enabled=false`) или Yes + UMP
- **App access** — All functionality available without restrictions
- **Content rating** — IARC questionnaire (Entertainment, no violence/gambling)
- **Target audience** — 13+ / not for children
- **Data safety** — `DATA_SAFETY_FORM.md`

### 4. Release

1. **Testing** → **Internal testing** → Create new release
2. Upload `build/store-upload/app-release.aab`
3. Upload `mapping.txt` (App bundle explorer → Deobfuscation)
4. Release notes: `RELEASE_NOTES_v1.0.0.md`
5. **Review release** → roll out to internal testers

### 5. Smoke test на устройстве

- [ ] Cold start, onboarding, hub
- [ ] Love test flow + protocol test
- [ ] Settings → Privacy opens HTTPS (не example.com)
- [ ] Premium paywall (test license on closed track)
- [ ] RU / EN locale switch

## Следующий трек

**Closed testing** — billing, ads consent (`lovetest.ads.enabled=true`), real purchase test.

**Production** — после closed testing + финальный `releaseGateLoveTest`.
