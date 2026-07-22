# V2 Backlog — Love Tester

Обновлено: **2026-07-19** · Основа: [PRD.md](./PRD.md) §7, [PRODUCT_DECISIONS.md](./PRODUCT_DECISIONS.md), [PROJECT_STATUS.md](./PROJECT_STATUS.md), [RELEASE_ENGINEERING.md](./RELEASE_ENGINEERING.md).

**Статус v1:** не начинать V2 (AdMob/history/deeplinks) до **Production GO**. Сейчас блокеры: legal Pages HTTP 200 + Closed IAP smoke.

**Легенда приоритета:** P0 = критично для дохода/стабильности · P1 = высокий эффект · P2 = рост/полировка · P3 = nice-to-have.

**Оценка эффекта:** 💰 доход · 🔄 retention · 📈 acquisition · 🛡 stability/ops.

---

## Сводка (по эффекту на доход + retention)

| Приоритет | Эпик | 💰 | 🔄 | Усилие | Версия |
|-----------|------|----|----|--------|--------|
| **P0** | AdMob + UMP (prod IDs) | ●●● | ● | M | **v1.1** |
| **P0** | Billing hardening + Closed→Prod IAP | ●●● | ● | S | **v1.0.x** |
| **P1** | История результатов (локально) | ● | ●●● | M | **v1.2** |
| **P1** | Deep links / App Links | ● | ●● | L | **v1.2** |
| **P2** | Firebase Crashlytics (+ opt. Analytics) | — | ● | S | **v1.1** |
| **P2** | Rewarded ad (опционально) | ●● | ● | M | **v1.3** |
| **P2** | Тёмная тема M3 | — | ● | M | **v1.3** |
| **P3** | Виджет / shortcuts | — | ● | M | **v2.x** |
| **P3** | A/B тексты результатов | ● | ● | L | **v2.x** |
| **P3** | Доп. языки (ES, DE, …) | ● | ● | L | **v2.x** |
| **P3** | Backend / аккаунты | ● | ●● | XL | **отложено** |

---

## P0 — доход (первый после стабильного v1.0)

### V2-001 · Production AdMob + UMP

| Поле | Значение |
|------|----------|
| **Цель** | Монетизация interstitial между сессиями (код уже есть) |
| **Действия** | `lovetest.ads.enabled=true`; prod AdMob app/unit IDs в **локальном** `gradle.properties` (не коммитить); UMP message в AdMob Console; `verifyAdsBuildLoveTest` |
| **Store** | Data safety + IARC + **Ads: Yes**; privacy update |
| **UX** | Consent до загрузки рекламы (EEA); Premium отключает ads |
| **Риск** | APK size, policy consent |
| **💰🔄** | Высокий доход при DAU; умеренный retention risk если агрессивно |

### V2-002 · IAP production readiness

| Поле | Значение |
|------|----------|
| **Цель** | Реальные покупки `remove_ads` на Production |
| **Действия** | SKU Active; license testers на Closed; smoke purchase + restore; убрать DEBUG mock premium в release |
| **Store** | Data safety Purchases уже заявлены |
| **💰🔄** | Прямой доход; низкий retention impact |

---

## P1 — retention + acquisition

### V2-003 · История последних результатов (локально)

| Поле | Значение |
|------|----------|
| **Цель** | Возврат пользователей — список прошлых пар имён + % |
| **Действия** | Расширить DataStore / Room; экран «История» в Settings или Hub |
| **Без** | Сервера и аккаунтов (согласовано PRD) |
| **💰🔄** | Косвенно retention; слабо acquisition |

### V2-004 · Deep links / Android App Links

| Поле | Значение |
|------|----------|
| **Цель** | Шаринг ссылки «открыть тест с именами» |
| **Действия** | Домен + `assetlinks.json`; NavHost deep link routes; [RELEASE_ENGINEERING.md](./RELEASE_ENGINEERING.md) |
| **Пример** | `https://lovetest.app/test?n1=…&n2=…` (домен TBD) |
| **💰🔄** | Acquisition ●●; retention ● |

---

## P2 — ops и полировка монетизации

### V2-005 · Firebase Crashlytics (рекомендуется до масштабирования ads)

| Поле | Значение |
|------|----------|
| **Цель** | Быстрее находить P0 после rollout |
| **Действия** | SDK + Data safety update; opt-out Analytics если не нужен |
| **💰🔄** | Stability → косвенно удерживает доход |

### V2-006 · Rewarded ad (опционально)

| Поле | Значение |
|------|----------|
| **Цель** | Доп. ARPU без блокировки core flow |
| **Действия** | «Посмотреть рекламу → +1 спин колеса» и т.п.; только после V2-001 |
| **💰🔄** | 💰●●; не в первом ads release |

### V2-007 · Тёмная тема Material 3

| Поле | Значение |
|------|----------|
| **Цель** | UX ожидание 2025+; [PRODUCT_DECISIONS.md](./PRODUCT_DECISIONS.md) отложено в v1 |
| **💰🔄** | Retention ● слабо |

---

## P3 — отложено

| ID | Эпик | Примечание |
|----|------|------------|
| V2-008 | App widget / shortcuts | PRD v2 optional |
| V2-009 | A/B текстов результатов | Нужен трафик |
| V2-010 | Локали ES/DE/… | После стабилизации RU/EN ASO |
| V2-011 | Полная медиация (AppLovin/Unity) | [WORK_PLAN.md](./WORK_PLAN.md) — вес SDK |
| V2-012 | Backend / sync / social | Вне scope MVP |

---

## Уже в v1.0 (не дублировать в v2)

- Все основные тесты на Hub (love, protocol, calculator, pair, victory, letters, zodiac, wheel)
- Premium paywall UI + Billing SDK
- RU + EN, share, onboarding + disclaimer
- 67 Store PNG, Compose UI tests

---

## Рекомендуемая последовательность релизов

```
v1.0.0  Production (ads off, IAP live)     ← текущий
v1.0.1  Hotfix при vitals (при необходимости)
v1.1    Crashlytics + AdMob/UMP (Closed → staged Prod)
v1.2    History + Deep links
v1.3    Rewarded + Dark theme
v2.x    Widget, A/B, языки
```

---

## Связанные документы

- [MONETIZATION.md](./MONETIZATION.md)
- [PRODUCTION_ROLLOUT.md](../store/PRODUCTION_ROLLOUT.md)
- [POST_RELEASE_PLAN.md](../store/POST_RELEASE_PLAN.md)
- [GO_LIVE_PLAN.md](../store/GO_LIVE_PLAN.md)
