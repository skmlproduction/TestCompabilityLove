# Релиз-инженерия: Love Tester

Фаза 0 — политики; реализация Gradle — **фаза F3**.

## Версии

- `versionName` / `versionCode` в `app/build.gradle.kts`.
- Старт: `1.0.0` / `1` (не привязываться к 3.0.5 референса).
- Перед каждым upload в Play — монотонный `versionCode++`.

## Идентификаторы

| Поле | Значение |
|------|----------|
| `applicationId` | `dev.lovetest.app` |
| Namespace | `dev.lovetest.app` |

## Keystore и Play App Signing

- Upload key вне git; `keystore.properties` в корне (`.gitignore`).
- Шаблон: `keystore.properties.example`; при наличии файла `app/build.gradle.kts` подключает `signingConfigs.release`.
- Локальные шаги: [LOCAL_RELEASE.md](./LOCAL_RELEASE.md).
- Play App Signing — включить в Console при первой загрузке.

## Секреты

| Секрет | Где |
|--------|-----|
| `sdk.dir` | `local.properties` (локально) |
| Ads app ids | `gradle.properties` или CI secrets, **не** в git с prod keys |
| Billing SKU | `love_test.billing.product.ids` в `gradle.properties` |
| Privacy URL | `love_test.privacy.policy.url` → `BuildConfig` |

## ProGuard / R8

- `minifyEnabled true` для release.
- Keep rules для Billing, ads SDK (когда подключены), `Parcelable` Nav args.
- После изменений SDK — прогон release APK на устройстве; mapping в Play Console.

## Задачи проверки (целевые имена)

```bash
./gradlew verifyLoveTest              # PR-цикл: inventory + compile + lint
./gradlew verifyLoveTestRelease       # + assembleRelease + bundleRelease
./gradlew verifyLoveTestBeforeStore   # + проверка реальных PNG Store (F5)
```

Зависимости задач — по образцу `NEWlockscreen/build.gradle.kts`: release-сборки `mustRunAfter(verifyLoveTest)`.

## CI

| Workflow | Триггер | Действие |
|----------|---------|----------|
| `ci.yml` | push/PR | JVM inventory + Android `verifyLoveTest` |
| `release-assemble.yml` | manual | `verifyLoveTestRelease` + AAB/APK artifacts |
| `emulator-screenshots.yml` | manual | Черновик PNG (F5) |

`permissions`: `contents: read`; для artifacts — `actions: write`.

## App Links

- **v2:** deep link `https://lovetest.app/...` — только с Digital Asset Links и политикой домена.
- MVP: не требуется.

## Скрипты (скопировать из NEWlockscreen в F3)

- `scripts/verify_ui_inventory.py`
- `scripts/write_screenshot_placeholders.py`
- `scripts/adb_screenshot_preview.sh`
- `scripts/capture_screenshot_catalog.sh`
- `scripts/check_pr.sh` → вызывает `verifyLoveTest`

## Permissions (целевой манифест MVP)

Минимум:

- `INTERNET` — billing, ads, privacy page (если WebView не используется — только внешний браузер)
- `ACCESS_NETWORK_STATE`
- `com.android.vending.BILLING`
- `com.google.android.gms.permission.AD_ID` — **только если** ads в релизе

**Не** запрашивать в MVP без необходимости:

- `WRITE_EXTERNAL_STORAGE` (использовать Share cache / MediaStore API 29+)
- `POST_NOTIFICATIONS` — пока нет push

## Production rollout

- [PRODUCTION_ROLLOUT.md](../store/PRODUCTION_ROLLOUT.md) — staged 10→50→100%, go/no-go
- [POST_RELEASE_PLAN.md](../store/POST_RELEASE_PLAN.md) — vitals 72 ч, отзывы, hotfix
- [V2_BACKLOG.md](./V2_BACKLOG.md) — roadmap после v1.0

## Связанные документы

- `GOOGLE_PLAY_RELEASE_CHECKLIST.md`
- `STACK.md`
