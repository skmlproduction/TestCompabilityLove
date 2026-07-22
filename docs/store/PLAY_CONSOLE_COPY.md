# Play Console — финальные тексты листинга (v1.0)

> Копируйте в Store listing. Проверьте уникальность названия EN в Play перед публикацией.  
> Скриншоты: 7 PNG × RU/EN — `build/store-upload/listing-screenshots/`.  
> Сводка форм: [PLAY_FORMS_FILLED.md](./PLAY_FORMS_FILLED.md).

---

## Legal URLs (HTTPS, GitHub Pages)

После `git push` и workflow **Privacy GitHub Pages** (см. [GITHUB_FIRST_PUSH.md](./GITHUB_FIRST_PUSH.md)):

| Назначение | URL для Play Console / приложения |
|------------|-----------------------------------|
| **Privacy policy** (обязательно) | `https://skmlproduction.github.io/TestCompabilityLove/` |
| **Terms of use** | `https://skmlproduction.github.io/TestCompabilityLove/terms.html` |
| **Data collection** (Data safety / справка) | `https://skmlproduction.github.io/TestCompabilityLove/data-collection.html` |

В `gradle.properties`: `lovetest.privacy.policy.url=https://skmlproduction.github.io/TestCompabilityLove/`  
Terms в приложении: тот же хост + `/terms.html` (см. `LegalDocuments`).

Проверка после деплоя: `./scripts/check_legal_urls.sh`

---

## RU — App name (≤30 символов)

```
Тест совместимости · Love
```

*(29 символов; ASO: тест на совместимость, love test)*

---

## RU — Short description (≤80)

```
Love test: тест совместимости по имени — пара, зодиак, колесо. Только fun!
```

*(79 символов)*

---

## RU — Full description

```
Love Test — развлекательный тест на совместимость и любовь по имени. Узнайте игровой процент «совместимости» для пары или друзей: love test, love calculator по буквам, тест пары, знаки зодиака и колесо идей.

⚠️ Только для развлечения — не научный прогноз, не совет психолога или астролога.

Что внутри:
• Тест совместимости по двум именам (love compatibility test)
• Протокол любви — пошаговый режим с сигналами и вердиктом
• Love calculator / калькулятор букв, тест пары, зодиак, колесо идей
• Поделиться результатом карточкой
• Интерфейс на русском и английском

Premium (опционально): разовая покупка в приложении убирает рекламу в будущих версиях. Все тесты доступны бесплатно без покупки.

Версия 1.0: без рекламы в приложении. Имена хранятся на устройстве.
```

---

## EN — App name (≤30)

```
Love Test: Compatibility
```

*(24 characters)*

---

## EN — Short description (≤80)

```
Love test & name compatibility — couple, zodiac, idea wheel. Fun only!
```

*(69 characters)*

---

## EN — Full description

```
Love Test is a playful love compatibility test and name-based love calculator for couples and friends. Enter two names, get a fun “match” percent, and try extra modes: love protocol, letter calculator, pair test, zodiac signs, and a date-idea wheel.

⚠️ For entertainment only — not scientific, medical, or relationship advice.

Features:
• Classic love test / compatibility by name
• Love protocol — step-by-step signals and verdict
• Letter love calculator, pair test, zodiac, idea wheel
• Share your result card
• Russian and English UI

Optional Premium: one-time in-app purchase (remove_ads) — removes ads in future updates. All tests work free without buying.

v1.0 ships with no ads. Names stay on your device.
```

---

## Скриншоты листинга (7 × RU + EN)

| # | Файл | Что показывает | Дисклеймер / честность |
|---|------|----------------|-------------------------|
| 1 | `hub_main` | Реальный хаб, все режимы | — |
| 2 | `love_test_input` | Ввод двух имён | — |
| 3 | `love_test_result` | Результат % | ✅ «Только для развлечения» на экране |
| 4 | `protocol_input` | Протокол любви, ввод | — |
| 5 | `protocol_result` | Вердикт протокола | ✅ `result_entertainment_only` |
| 6 | `wheel_spin` | Колесо идей | ✅ «not a bet» / развлечение (note3) |
| 7 | `premium_paywall` | Опциональный Premium | ✅ «Продолжить бесплатно», не paywall-gate |

Пересъёмка: `./gradlew capturePriorityScreensLoveTest` (RU) и `-Plocale=en capturePriorityScreensLoveTest` — нужен adb + эмулятор 1080×1920.

---

## Data safety — краткий ответ (v1.0, premium-only)

```
Имена (все тесты, включая протокол любви) обрабатываются на устройстве и не отправляются на сервер разработчика.
Покупки Premium — через Google Play (product id: remove_ads).
Версия 1.0: рекламы нет, рекламный ID не собирается. Аналитика и crash-отчёты не используются.
```

---

## Release notes 1.0.0

См. [RELEASE_NOTES_v1.0.0.md](./RELEASE_NOTES_v1.0.0.md).
