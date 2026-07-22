# Play Billing — настройка Love Tester

SKU в проекте: `remove_ads` (`gradle.properties` → `lovetest.billing.product.ids`).

## Имя SKU vs v1 без рекламы

| Контекст | Рекомендация |
|----------|----------------|
| **Текущий ID** | `remove_ads` — оставить в Play Console и `gradle.properties` (смена ID = новый продукт + миграция покупок). |
| **Store listing / paywall copy** | При `ADS_ENABLED=false` (MVP v1) Premium позиционируется как **поддержка разработки**, не «убрать рекламу» и не «разблокировать тесты». |
| **Будущий rename (v1.2+)** | Если включите рекламу, можно создать параллельный SKU `premium_support` или переименовать **отображаемое** имя в Console на «Premium — без рекламы»; технический ID менять только при готовности к миграции. |

См. [PRODUCT_DECISIONS.md](../product/PRODUCT_DECISIONS.md) §2 — v1 premium-only, ads off.

## Play Console

1. **Monetize** → **Products** → **In-app products**
2. Create product:
   - Product ID: `remove_ads` (должен совпадать с `gradle.properties`)
   - Name (RU): «Premium — поддержка проекта» / EN: «Premium — support the project»
   - Description: добровольная разовая покупка; все тесты бесплатны в v1
   - Type: **One-time** (managed product)
   - Status: **Active**
3. **License testing** — добавьте Gmail тестеров для Internal/Closed track.

## Локальная проверка

```bash
./gradlew verifyLoveTest
# Closed track AAB + тестовый аккаунт в License testers
```

В приложении: **Premium paywall** → покупка → **Settings** → restore.

Цена на paywall подтягивается из Play Billing (`PremiumBillingManager.queryFormattedPrice()`); fallback — `premium_price` в strings.

Если SKU не настроен, paywall показывает `NotConfigured` (без краша).

## Код

| Файл | Назначение |
|------|------------|
| `PremiumBillingManager.kt` | BillingClient, acknowledge, formatted price |
| `PremiumRestore.kt` | Restore из Settings |
| `PremiumPaywallScreen.kt` | UI покупки (copy ветвится по `ADS_ENABLED`) |

См. также [INTERNAL_TESTING.md](./INTERNAL_TESTING.md) · [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md).
