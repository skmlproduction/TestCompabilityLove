# Play Billing — настройка Love Tester

SKU в проекте: `remove_ads` (`gradle.properties` → `lovetest.billing.product.ids`).

## Play Console

1. **Monetize** → **Products** → **In-app products**
2. Create product:
   - Product ID: `remove_ads` (должен совпадать с `gradle.properties`)
   - Name: «Premium без рекламы» / «Premium without ads»
   - Type: **One-time** (или managed product)
   - Status: **Active**
3. **License testing** — добавьте Gmail тестеров для Internal/Closed track.

## Локальная проверка

```bash
./gradlew verifyLoveTest
# Closed track AAB + тестовый аккаунт в License testers
```

В приложении: **Premium paywall** → покупка → **Settings** → restore.

Если SKU не настроен, paywall показывает `NotConfigured` (без краша).

## Код

| Файл | Назначение |
|------|------------|
| `PremiumBillingManager.kt` | BillingClient, acknowledge |
| `PremiumRestore.kt` | Restore из Settings |
| `PremiumPaywallScreen.kt` | UI покупки |

См. также [INTERNAL_TESTING.md](./INTERNAL_TESTING.md) · [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md).
