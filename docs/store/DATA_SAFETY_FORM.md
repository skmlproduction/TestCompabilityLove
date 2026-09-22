# Google Play — Data safety (v1.0, CAS.AI ads + Premium)

Сверьте с [официальной формой](https://support.google.com/googleplay/android-developer/answer/10787469) перед отправкой.  
Согласовано с: `assets/legal/data_collection.html`, `privacy_policy.html`, **MONETIZATION_DECISION** (`lovetest.ads.enabled=true`, CAS.AI mediation).

**Версия:** v1.0 Internal/Closed · **реклама вкл** (CAS.AI interstitial, consent через UMP) · SKU `remove_ads`.

---

## 1. Общие ответы (первый экран формы)

| Вопрос в Console | Ответ v1 |
|------------------|----------|
| Собирает или передаёт данные? | **Да** (минимальный набор ниже) |
| Все передаваемые данные шифруются в транзите? | **Да** (HTTPS для Billing / рекламных SDK; имена **не** передаются разработчику) |
| Предоставляете способ запросить удаление? | **Да** — имена: «Настройки → Очистить сохранённые имена»; покупки — через Google Play / аккаунт Google; рекламные данные — настройки рекламы устройства / форма UMP в настройках приложения |
| Независимая проверка безопасности | По умолчанию (без отдельного аудита) |
| **Содержит рекламу?** (App content, связано) | **Да** — CAS.AI mediation interstitial |

---

## 2. Что объявлять в Data safety (v1)

### Объявить

| Тип данных (категория Play) | Собирается | Обязательно | Передаётся третьим лицам | Цель |
|-----------------------------|------------|-------------|---------------------------|------|
| **Имя** (Personal info → Name) | Да | **Нет** (опциональный ввод) | **Нет** — только на устройстве | Функция приложения (тесты, протокол) |
| **История покупок** / In-app purchases (Financial info) | Да | **Да** (для Premium) | **Да** — **только Google Play** | Покупка / восстановление `remove_ads` |
| **Device or other IDs** (Advertising ID) | Да (рекламный SDK) | **Нет** (опционально, после consent UMP; отказ = ограниченная/неперсонализированная реклама) | **Да** — CAS.AI (CleverAdsSolutions) и рекламные партнёры (включая Google AdMob) | Реклама / маркетинг |

Для **Имени** в форме уточните: обработка **на устройстве**, не отправляется на сервер разработчика; пользователь может удалить в настройках.

Для **Покупок**: обработка Google Play Billing; приложение не хранит платёжные карты.

Для **Advertising ID**: consent через UMP-форму при первом запуске; изменить выбор — «Настройки → реклама и конфиденциальность» (форма UMP). Premium отключает рекламу полностью.

### Не объявлять в v1 (нет в коде / не активно)

| Тип | Почему |
|-----|--------|
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
| `consent_completed` | DataStore (экран consent в приложении) | Исключено |

Эти флаги **не** требуют отдельной декларации «передачи», если в форме указано, что имя не уходит с устройства. Premium-флаг — следствие покупки через Play.

---

## 3. Сеть в v1

| Канал | Когда | Данные |
|-------|-------|--------|
| Google Play Billing | Покупка / restore Premium | Purchase tokens — Google |
| CAS.AI mediation SDK (CleverAdsSolutions + партнёры) | Загрузка/показ interstitial, после consent | Advertising ID, данные показов — по политикам партнёров |
| UMP (Google) | Форма согласия / privacy options | Статус согласия (IAB TCF) |
| HTTPS (privacy policy) | Настройки → Privacy | URL в браузере, без имён |

Офлайн: все расчёты тестов (love, wheel, zodiac, protocol и др.) работают без сети (реклама просто не показывается).

---

## 4. app-ads.txt

- Файл: `app/src/main/assets/legal/app-ads.txt` → публикуется на Pages (`https://skmlproduction.github.io/TestCompabilityLove/app-ads.txt`).
- Сейчас — placeholder; **после регистрации приложения в дашборде cas.ai** заменить строками, которые выдаст CAS (и AdMob при привязке), и задеплоить до отправки на review.

---

## 5. Связанные файлы

- [PLAY_FORMS_FILLED.md](./PLAY_FORMS_FILLED.md) — сводка + чеклист «форма ↔ код»
- [IARC_QUESTIONNAIRE.md](./IARC_QUESTIONNAIRE.md)
- [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md)
- `docs/product/GOOGLE_PLAY_RELEASE_CHECKLIST.md` §5
