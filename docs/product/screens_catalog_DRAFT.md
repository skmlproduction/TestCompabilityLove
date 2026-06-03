# Черновик screen_id (архив)

**Источник правды:** [screens_catalog.csv](./screens_catalog.csv) (**29** screen_id, F1).  
Уточняй по скриншотам в `reference/screenshots/` и обновляй CSV + `SCREEN_INVENTORY_AND_NAVIGATION.md`.

## Hub и системное

| screen_id | Описание | SVG (план) |
|-----------|----------|------------|
| `splash_brand` | Splash / загрузка | screen1 |
| `hub_main` | Главное меню: карточки тестов | screen2 |
| `consent_ads_gdpr` | Первый запуск: согласие / реклама / UMP | screen3 |

## Тест на любовь (core)

| screen_id | Описание | SVG |
|-----------|----------|-----|
| `love_test_input` | Два имени + CTA «Рассчитать» | screen4 |
| `love_test_calculating` | Анимация расчёта | screen5 |
| `love_test_result` | Процент + текст + сердца | screen6 |
| `love_test_result_low` | Низкий % (вариант копирайта) | screen7 |

## Калькулятор / пара / победа

| screen_id | Описание | SVG |
|-----------|----------|-----|
| `calculator_input` | Калькулятор любви | screen8 |
| `calculator_result` | Результат калькулятора | screen9 |
| `pair_input` | Совместимость пары | screen10 |
| `pair_result` | Результат пары | screen11 |
| `victory_input` | Победа в любви — ввод | screen12 |
| `victory_result` | Победа — результат | screen13 |

## Доп. тесты

| screen_id | Описание | SVG |
|-----------|----------|-----|
| `letters_input` | Тест по буквам | screen14 |
| `letters_result` | Результат по буквам | screen15 |
| `zodiac_pick` | Выбор знаков | screen16 |
| `zodiac_result` | Астрология результат | screen17 |
| `wheel_spin` | Колесо фантазий | screen18 |
| `wheel_result` | Результат колеса | screen19 |

## Монетизация и настройки

| screen_id | Описание | SVG |
|-----------|----------|-----|
| `premium_paywall` | Premium / убрать рекламу | screen20 |
| `premium_thank_you` | Покупка успешна | screen21 |
| `settings_main` | Настройки, язык, политика | screen22 |
| `share_result_card` | Превью карточки шаринга (опц.) | screen23 |

## Состояния

| screen_id | Описание | SVG |
|-----------|----------|-----|
| `hub_loading` | Загрузка конфига/рекламы | screen24 |
| `error_network` | Нет сети (если нужен API) | screen25 |
| `ad_interstitial_splash` | Межстраничная реклама (опц.) | screen26 |

**Итого черновик:** ~26 screen_id → **~52 PNG** (RU+EN) после съёмки.

Алгоритм совместимости в MVP может быть **локальным** (без сервера) — зафиксируй в PRD.
