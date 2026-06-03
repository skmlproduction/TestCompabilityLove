# Онбординг и юридические гейты: Love Tester

## Развлекательный disclaimer (обязательно)

Тексты Store оригинала подчёркивают: приложение **для развлечения**, не научный тест. В новом продукте:

- Слайд онбординга 3: «Результаты носят развлекательный характер и не являются советом или прогнозом».
- На экране результата — мелкая подпись `result_disclaimer` (RU/EN).
- Описание в Play Console — согласовано с `PRD.md` §1.

Строковые ключи (черновик): `onboard_disclaimer_title`, `onboard_disclaimer_body`, `result_entertainment_only`.

## Онбординг

| Параметр | Значение |
|----------|----------|
| Число слайдов | **3** |
| Слайд 1 | Приветствие + «тесты по именам» |
| Слайд 2 | Hub: несколько тестов, шаринг |
| Слайд 3 | Disclaimer + «только для fun» |
| Пропуск | Кнопка «Пропустить» на всех шагах |
| Повтор | `settings_replay_onboarding` в настройках |
| Хранение флага | DataStore `love_test_onboarding` |

**Без Lottie в MVP** — иллюстрации vector/emoji в Compose.

## Первый запуск: согласие рекламы (UMP)

Если в сборке включена реклама (как в референсе 3.0.5):

1. Экран `consent_ads_gdpr` **до** первого показа ads (или UMP form из Google).
2. Порядок: `splash` → (onboarding если нужен) → **consent** → hub.
3. Без согласия — не инициализировать mediation SDK.

Если MVP **без** ads — экран consent **не показывается** (`BuildConfig.ADS_ENABLED=false`); см. `StartupNavigation.kt`.

## ToS / Privacy

| Элемент | Реализация |
|---------|------------|
| Privacy Policy | URL в `BuildConfig` (`lovetest.privacy.policy.url`); fallback — `assets/legal/privacy_policy.html` |
| Data collection | `assets/legal/data_collection.html` (Settings) |
| Terms | Ссылка на тот же хостинг или раздел на сайте — опционально |
| Data collection summary | `settings_data_collection_summary` (RU/EN) — основа Data safety |

**Имена пользователя:** кэш последней пары имён в DataStore (`last_name1` / `last_name2`); только на устройстве, без сервера.

## Premium / Billing

- Честная формулировка: «убрать рекламу», не «улучшить точность теста».
- Restore purchases в paywall и settings.
- Соответствие [Play Billing policies](https://play.google.com/about/monetization-ads/).

## Возраст и контент

- Рейтинг: без откровенного контента; «колесо фантазий» — формулировки **мягкие**, без 18+ (сверить по скриншотам референса при F2).
- Семейная политика: не targeting children under 13 with ads personalization без COPPA flow.

## Товарные знаки

- Не использовать «Love Maker» / точное название оригинала в брендинге без проверки.
- Рабочее EN: **Love Tester**; листинг RU: **Тест на совместимость и любовь**.

## Локализация юридических строк

- `values/strings.xml` + `values-en/strings.xml` — парность через `verifyUiInventory`.

## Связанные документы

- `PRD.md` — user stories US-11, US-20.
- `GOOGLE_PLAY_RELEASE_CHECKLIST.md` — Data safety черновик.
