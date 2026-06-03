# Стек: Love Tester (`dev.lovetest.app`)

Документ фазы 0. Детали Gradle появятся в F3 (scaffold).

## Язык и UI

| Компонент | Выбор |
|-----------|--------|
| Язык | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 (**light**, dynamic color **off** — фиксированная романтическая палитра) |
| Навигация | Navigation Compose, type-safe routes (`Routes` object) |
| Min/Target SDK | 26 / 35 |
| Тема | `core:ui` — `LoveTestTheme`, цвета из `docs/design/DESIGN_SYSTEM.md` (после F2) |

**Primary seed:** `#C2185B` → M3 `ColorScheme` через `ColorScheme.fromSeed` или явные токены.

## Модули (Gradle)

```
:app              — MainActivity, NavHost, feature screens, ViewModels
:core:ui          — Theme, Typography, общие компоненты (кнопки, hero, percent ring)
:core:domain      — (F3+) LoveScoreCalculator, модели TestInput/Result
:core:data        — (опционально) DataStore prefs, billing wrapper
```

**MVP без `:core:network`** — расчёты локальные. Модуль `:core:network` добавить только при появлении API (v2).

## Архитектура

- **MVVM:** `*ViewModel` + `StateFlow` UI state; Composable — stateless по возможности.
- **DI:** Koin (как LockDraw) — модули `appModule`, `domainModule`.
- **Корутины:** `viewModelScope` для «расчёта» с delay под анимацию.

## Навигация и debug

- `LoveTestNavHost` — все `route_path` из `screens_catalog.csv`.
- `NavIntents`: `DEBUG_START_ROUTE`, `DEBUG_UI_PREVIEW` для съёмки скриншотов (release — игнор).
- `DebugUiPreviewActivity` (debug manifest) — опционально в F5.

## Монетизация и SDK (поэтапно)

| SDK | Фаза | Примечание |
|-----|------|------------|
| Play Billing 7.x | MVP каркас | Product id в `gradle.properties` / `BuildConfig` |
| UMP (User Messaging Platform) | Перед ads в production | См. `ONBOARDING_AND_LEGAL.md` |
| Ads mediation | v2 или late MVP | Референс: AppLovin + Unity Ads — **не** копировать конфиг; выбрать один stack |
| Firebase | Опционально | Analytics/Crashlytics — только с обновлением Data safety |

## Тестирование

| Уровень | Инструмент |
|---------|------------|
| Unit | JUnit5 + `LoveScoreCalculator` тесты |
| UI | Compose UI tests для hub + love_test flow (после F4) |
| Inventory | `scripts/verify_ui_inventory.py` + `verifyUiInventory` Gradle |
| Сборка | `verifyLoveTest` = JVM checks + Android lint/compile (паттерн LockDraw) |

## CI (skeleton, F3)

- `.github/workflows/ci.yml`: `verifyLoveTestJvm`, `verifyUiInventory`, `verifyLoveTestAndroid`.
- `release-assemble.yml`: `workflow_dispatch` → `verifyLoveTestRelease` + AAB artifact.

## Строки и ассеты

- `app/src/main/res/values/strings.xml` (RU default)
- `app/src/main/res/values-en/strings.xml`
- Иконки: adaptive launcher, vector hearts — без растра из Unity.

## Именование задач Gradle

| LockDraw | Love Test |
|----------|-----------|
| `verifyLockDraw` | `verifyLoveTest` |
| `verifyLockDrawRelease` | `verifyLoveTestRelease` |
| `verifyLockDrawBeforeStore` | `verifyLoveTestBeforeStore` |
| `captureScreenshotCatalogRu` | то же имя или `captureLoveTestScreenshotsRu` |

## Зависимости (ориентир версий, уточнить в F3)

- Compose BOM (актуальный stable)
- `androidx.navigation:navigation-compose`
- `io.insert-koin:koin-android`
- `com.android.billingclient:billing-ktx`
- Без Unity, без React Native

## Связанные документы

- `RELEASE_ENGINEERING.md` — R8, подпись, CI.
- `WORK_PLAN.md` — когда подключать каждый модуль.
