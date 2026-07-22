# Play Console — заполненные формы (сводка v1.0)

Дата сверки: **2026-06-03**  
Пакет: `dev.lovetest.app` · Решение монетизации: **premium-only**, реклама **выкл** ([PRODUCT_DECISIONS.md](../product/PRODUCT_DECISIONS.md) §2).

Используйте этот файл как **единый чеклист** перед Internal testing. Детали: [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md), [IARC_QUESTIONNAIRE.md](./IARC_QUESTIONNAIRE.md), тексты листинга: [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md).

---

## A. App content (связанные переключатели)

| Поле Console | Значение v1 |
|--------------|-------------|
| Privacy policy URL | `https://skmlproduction.github.io/TestCompabilityLove/` (после деплоя Pages) |
| Ads | **No** |
| In-app purchases | **Yes** (`remove_ads`, one-time) |
| Target audience | **13+**, not designed for children under 13 |
| App access | All functionality available (no login wall) |
| Content rating | IARC — см. раздел B |
| Data safety | См. раздел C |

---

## B. IARC / Content rating — итоговые ответы

| Тема | Ответ |
|------|-------|
| Категория | Entertainment |
| Gambling / real money | **No** |
| Simulated gambling | **No** (wheel = random ideas, no bets) |
| Violence / sexual / drugs | **No** |
| UGC / social | **No** |
| Purchases | **Yes** (Premium) |
| Ads | **No** |
| Wheel / zodiac | Random **for fun only**, no payouts |

---

## C. Data safety — итоговые ответы

| Декларировать | Не декларировать (v1) |
|---------------|------------------------|
| **Name** — optional, on-device, not shared with developer | Advertising ID |
| **Purchase history** — via Google Play, for Premium | Analytics |
| | Crash logs |
| | Location, contacts, photos, email, health, messages |

**Сбор данных:** Yes (limited). **Шифрование в транзите:** Yes (для сетевых вызовов). **Удаление:** Yes (имена в приложении).

---

## D. Store listing (копипаст)

См. [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md) — short/full RU+EN, фраза *«Results are for entertainment only»*.

**Data safety declaration text (кратко):**

```
Names entered in tests (including love protocol) stay on the device; we do not receive them on our servers.
Purchases are processed by Google Play. Version 1.0 has no ads and no advertising ID collection.
```

---

## E. Чеклист «форма ↔ код»

| # | Утверждение в форме | Код / конфиг | Статус |
|---|---------------------|--------------|--------|
| 1 | Нет Firebase Analytics | Нет зависимости в `app/build.gradle.kts` | ✅ |
| 2 | Нет Crashlytics / Sentry | Нет SDK | ✅ |
| 3 | Нет Advertising ID в v1 | `lovetest.ads.enabled=false`; `src/ads/AndroidManifest.xml` не мержится; `AdMobInitializer` no-op | ✅ |
| 4 | Нет показа рекламы в v1 | `AdsInterstitialController.shouldShow()` → false; consent route skipped | ✅ |
| 5 | Имена только локально | `AppPreferences` DataStore; нет HTTP API | ✅ |
| 6 | Имена можно удалить | `clearLastNames()` → Settings | ✅ |
| 7 | Имена не в backup | `backup_rules.xml` exclude `love_test_prefs` | ✅ |
| 8 | Покупки через Play | `PremiumBillingManager`, SKU `remove_ads`, acknowledge | ✅ |
| 9 | Premium не блокирует тесты | Paywall optional; hub/tests без paywall gate | ✅ |
| 10 | Wheel не gambling | Random segment text; `wheel_spin_note3` «not a bet»; no currency | ✅ |
| 11 | Zodiac — entertainment | Локальный контент, без ставок | ✅ |
| 12 | Нет UGC/чата | Нет сетевого social layer | ✅ |
| 13 | Disclaimer в приложении | Onboarding + result screens | ✅ |
| 14 | Legal HTML согласован | `privacy_policy.html`, `data_collection.html` (v1 no ads) | ✅ |
| 15 | **Ads = No** в Console | Согласовано с PRODUCT_DECISIONS §2 | ✅ |
| 16 | При v1.2 + ads | Пересдать Data safety + IARC + Ads Yes | ⏳ будущее |

---

## F. Порядок в Console

1. Create app → Store listing (RU/EN)  
2. **App content** → Privacy URL, Ads **No**, IAP **Yes**, Target audience **13+**  
3. **Data safety** — по разделу C  
4. **Content rating** — IARC по [IARC_QUESTIONNAIRE.md](./IARC_QUESTIONNAIRE.md)  
5. **Monetize** → Product `remove_ads` Active + License testers  
6. **Internal testing** → upload AAB  

---

## G. Документы в репозитории

| Файл | Назначение |
|------|------------|
| [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md) | Пошаговые ответы Data safety |
| [IARC_QUESTIONNAIRE.md](./IARC_QUESTIONNAIRE.md) | IARC / content rating |
| [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md) | Тексты листинга |
| [BILLING_SETUP.md](./BILLING_SETUP.md) | SKU `remove_ads` |
| [INTERNAL_TESTING.md](./INTERNAL_TESTING.md) | Первый релиз |
