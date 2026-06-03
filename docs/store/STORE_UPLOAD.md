# Загрузка в Google Play — пошагово

## 1. Подготовка (локально)

```bash
./scripts/onboard_release.sh USER REPO   # первый раз: конфиг + health
./scripts/project_health.sh
cp gradle.properties.example gradle.properties   # или ./scripts/init_store_config.sh
# lovetest.privacy.policy.url=https://ваш-домен/privacy   # обязательно для Play
# lovetest.billing.product.ids=remove_ads                 # опционально
# lovetest.ads.enabled=true                               # если нужна реклама

cp keystore.properties.example keystore.properties
# storeFile, пароли, keyAlias

./gradlew verifyLoveTest                 # unit + lint + test inventory (CI)
./scripts/run_route_smoke_tests.sh       # 7 route smoke (adb, быстро)
./scripts/run_compose_ui_tests.sh        # 49 Compose UI (adb)

./scripts/export_privacy_for_hosting.sh   # опционально: HTML для GitHub Pages
./gradlew preflightLoveTestStore          # privacy, PNG stats, keystore, adb
./gradlew releaseGateLoveTest             # verify + audit P0 + store WARN
./scripts/finalize_store_release.sh       # финальный pack + validate
```

### Privacy URL (Play Console)

| Способ | Действие |
|--------|----------|
| Свой сайт | Залить `assets/legal/privacy_policy.html` на HTTPS |
| GitHub Pages | [PRIVACY_HOSTING.md](./PRIVACY_HOSTING.md) · workflow `pages-privacy.yml` |
| Быстрая настройка | `./scripts/set_privacy_url.sh https://ВАШ-ДОМЕН/privacy` |
| Без URL (dev) | Bundled WebView в приложении — **не подходит** для финального листинга |

В `gradle.properties`: `lovetest.privacy.policy.url=https://…` — тот же URL в Console → Store listing → Privacy policy.

## 2. Скриншоты Store ✅

**67/67 PNG готовы** (1080×1920). Повторная съёмка:

```bash
./scripts/capture_store_local.sh both full
./gradlew verifyLoveTestBeforeStore
```

Приоритетные для листинга: `build/store-upload/listing-screenshots/` (после `./scripts/pack_store_upload.sh`).

## 3. AAB

```bash
./gradlew bundleRelease
```

Файл: `app/build/outputs/bundle/release/app-release.aab`

Mapping (R8): `app/build/outputs/mapping/release/mapping.txt` — загрузить в Console.

## 4. Play Console

| Раздел | Документ |
|--------|----------|
| Листинг RU/EN | `LISTING_DRAFT.md` |
| Data safety | `DATA_SAFETY_FORM.md`, `assets/legal/data_collection.html` |
| Feature graphic | `FEATURE_GRAPHIC.md` | `./scripts/export_feature_graphic.sh` |
| Release notes | `RELEASE_NOTES_v1.0.0.md` |
| Полный чеклист | `../product/GOOGLE_PLAY_RELEASE_CHECKLIST.md` |

## 5. Треки

1. **Internal testing** — первый AAB, smoke test.
2. **Closed testing** — billing + consent (`lovetest.ads.enabled=true`).
3. **Production** — после `verifyLoveTestBeforeStore` и заполнения Data safety.
