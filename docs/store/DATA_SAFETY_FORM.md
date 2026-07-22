# Google Play — Data safety (v1.0, premium-only)

Сверьте с [официальной формой](https://support.google.com/googleplay/android-developer/answer/10787469) перед отправкой.  
Согласовано с: `assets/legal/data_collection.html`, `privacy_policy.html`, **PRODUCT_DECISIONS §2** (`lovetest.ads.enabled=false`).

**Версия:** v1.0 Internal/Closed · **реклама выкл** · SKU `remove_ads`.

---

## 1. Общие ответы (первый экран формы)

| Вопрос в Console | Ответ v1 |
|------------------|----------|
| Собирает или передаёт данные? | **Да** (минимальный набор ниже) |
| Все передаваемые данные шифруются в транзите? | **Да** (HTTPS для Billing / будущей рекламы; имена **не** передаются разработчику) |
| Предоставляете способ запросить удаление? | **Да** — имена: «Настройки → Очистить сохранённые имена»; покупки — через Google Play / аккаунт Google |
| Независимая проверка безопасности | По умолчанию (без отдельного аудита) |
| **Содержит рекламу?** (App content, связано) | **Нет** — см. IARC / Ads declaration |

---

## 2. Что объявлять в Data safety (v1)

### Объявить

| Тип данных (категория Play) | Собирается | Обязательно | Передаётся третьим лицам | Цель |
|-----------------------------|------------|-------------|---------------------------|------|
| **Имя** (Personal info → Name) | Да | **Нет** (опциональный ввод) | **Нет** — только на устройстве | Функция приложения (тесты, протокол) |
| **История покупок** / In-app purchases (Financial info) | Да | **Да** (для Premium) | **Да** — **только Google Play** | Покупка / восстановление `remove_ads` |

Для **Имени** в форме уточните: обработка **на устройстве**, не отправляется на сервер разработчика; пользователь может удалить в настройках.

Для **Покупок**: обработка Google Play Billing; приложение не хранит платёжные карты.

### Не объявлять в v1 (нет в коде / не активно)

| Тип | Почему |
|-----|--------|
| **Device or other IDs** (Advertising ID, Android ID для ads) | `ADS_ENABLED=false`; `AD_ID` permission **не** в release-манифесте; AdMob **не** инициализируется |
| **Analytics** (Firebase, GA, Amplitude и т.д.) | Нет SDK в `app/build.gradle.kts` |
| **Crash logs** (Crashlytics, Sentry) | Нет SDK |
| **Location** | Нет |
| **Photos, videos, audio** | Нет |
| **Contacts, SMS, call logs** | Нет |
| **Email, address, phone** | Нет |
| **Health, fitness** | Нет |
| **Messages / UGC на сервер** | Нет чата и бэкенда |
| **Search history, browsing history** | Нет |
| **Performance diagnostics** (если не Crashlytics) | Нет отдельного сбора |
| **Files and docs** | Нет загрузки на сервер |

### Локально на устройстве (не передаётся разработчику)

| Данные | Хранение | Backup |
|--------|----------|--------|
| `last_name1`, `last_name2` | DataStore `love_test_prefs` | **Исключено** (`backup_rules.xml`) |
| `session_snapshot_v1` | DataStore | Исключено |
| `onboarding_completed`, `premium_active` | DataStore | Исключено |
| `consent_completed` | DataStore (v1 экран consent **не показывается**) | Исключено |

Эти флаги **не** требуют отдельной декларации «передачи», если в форме указано, что имя не уходит с устройства. Premium-флаг — следствие покупки через Play.

---

## 3. Сеть в v1

| Канал | Когда | Данные |
|-------|-------|--------|
| Google Play Billing | Покупка / restore Premium | Purchase tokens — Google |
| HTTPS (privacy policy) | Настройки → Privacy | URL в браузере, без имён |
| AdMob / UMP | **Не используется** в v1 | — |

Офлайн: все расчёты тестов (love, wheel, zodiac, protocol и др.) работают без сети.

---

## 4. После включения рекламы (v1.2+)

Пересдать Data safety и IARC:

- **Device or other IDs** → Advertising ID (при consent UMP)
- **App content → Contains ads** → Yes
- Обновить `data_collection.html` и privacy (уже есть блок «если включена реклама»)

---

## 5. Связанные файлы

- [PLAY_FORMS_FILLED.md](./PLAY_FORMS_FILLED.md) — сводка + чеклист «форма ↔ код»
- [IARC_QUESTIONNAIRE.md](./IARC_QUESTIONNAIRE.md)
- [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md)
- `docs/product/GOOGLE_PLAY_RELEASE_CHECKLIST.md` §5
