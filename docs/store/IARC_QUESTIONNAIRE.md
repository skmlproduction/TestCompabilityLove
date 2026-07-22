# IARC — content rating (v1.0, premium-only)

Сверьте с [IARC questionnaire](https://support.google.com/googleplay/android-developer/answer/9853738) в Play Console перед публикацией.

**Монетизация v1:** IAP `remove_ads` — **да**; реклама — **нет** (`lovetest.ads.enabled=false`).  
**Характер приложения:** развлекательные тесты совместимости, не медицина и не финансовый совет.

---

## 1. Профиль приложения

| Вопрос IARC (типичная формулировка) | Ответ v1 | Обоснование |
|-------------------------------------|----------|-------------|
| Категория / жанр | **Entertainment** (допустимо Lifestyle) | Тесты, share-карточки, игровые режимы |
| Target audience | **13+** / Teen | Не для детей до 13; без детского режима |
| Made for kids / Families | **Нет** | Нет COPPA/Family targeting |
| User-generated content публикуется другим | **Нет** | Имена не публикуются, нет ленты/чата |
| Online interaction между пользователями | **Нет** | Нет мультиплеера, чата, голосовых комнат |
| Shares location with other users | **Нет** | — |

---

## 2. Контент и темы

| Тема | Ответ v1 | Примечание |
|------|----------|------------|
| Насилие | **Нет** | Cartoon UI, без gore |
| Сексуальный / explicit контент | **Нет** | Романтика/«любовь» в игровом стиле, без nudity |
| Плохие слова / hate | **Нет** в контенте приложения | Пользователь вводит свои имена — не модерируется на сервере (нет сервера) |
| Алкоголь, табак, наркотики | **Нет** | — |
| Страшный / horror | **Нет** / Minimal | — |
| Gambling / betting | **Нет** | См. §3 (wheel / zodiac) |
| Simulated gambling | **Нет** | Колесо — идеи для свидания, без ставок и валюты |
| Contests / sweepstakes | **Нет** | — |
| Mature / suggestive humor | **Нет** (или Minimal) | В v1 нет «18+» сегментов на колесе; при добавлении — пересмотреть |
| Unrestricted web access | **Нет** | Privacy открывается в браузере по ссылке из настроек |
| Purchases digital goods | **Да** | One-time IAP Premium (`remove_ads`) |
| Ads | **Нет** | v1: без AdMob в release |

---

## 3. Wheel, zodiac, проценты — не gambling

| Режим | Что делает код | Ответ IARC |
|-------|----------------|------------|
| **Love test / protocol / calculator / pair / letters / victory** | Локальный детерминированный %, развлечение | Не gambling; не medical |
| **Wheel of ideas** (`WheelSpinScreen`) | Случайная **текстовая подсказка** (идея свидания); строка UI: *«not a bet»* (`wheel_spin_note3`) | **Не** азартная игра: нет ставок, нет выигрыша денег/призов, нет вывода средств |
| **Zodiac** | Выбор знака → развлекательный текст совместимости | Entertainment / horoscope style, **не** fortune-telling с реальными деньгами |

Если анкета спрашивает **«random elements»** / **«chance-based»**: **Да**, но только для развлечения без денежных ставок → классификация остаётся Entertainment, **не** Real gambling.

---

## 4. Монетизация в анкете

| Вопрос | v1 |
|--------|-----|
| In-app purchases | **Yes** — non-consumable Premium |
| Subscription | **No** |
| Contains ads | **No** |
| Loot boxes / gacha | **No** |
| Paid random items | **No** |

---

## 5. Ожидаемый рейтинг

Типично для такого профиля: **PEGI 3 / Everyone / 3+** или **12+** в зависимости от формулировок romance — IARC выдаст конкретные региональные метки после анкеты.

**Важно:** не заявляйте gambling или ads, если в сборке v1 они отключены.

---

## 6. Store listing alignment

- Short/full description: «только развлечение» — [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md)
- In-app: onboarding disclaimer (№4), результаты тестов
- Data safety: без Advertising ID — [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md)

---

## 7. После изменений продукта

| Изменение | Действие |
|-----------|----------|
| Включить AdMob (`ads.enabled=true`) | Перепройти IARC + Data safety; **Contains ads: Yes** |
| Добавить 18+ темы на колесе | Уточнить Mature / suggestive themes |
| Смена target audience на &lt;13 | Family policy, отдельная анкета |
| Подписка / loot boxes | Новые ответы в IAP / gambling секциях |

Сохраните **IARC certificate ID** в Play Console после прохождения.
