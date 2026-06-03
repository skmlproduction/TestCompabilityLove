# IARC — черновик ответов (Love Tester)

Сверьте с [IARC questionnaire](https://support.google.com/googleplay/android-developer/answer/9853738) перед отправкой в Play Console.

## Профиль приложения

| Вопрос | Рекомендуемый ответ |
|--------|---------------------|
| Категория | **Entertainment** / Lifestyle |
| Целевая аудитория | **13+** (не для детей до 13) |
| Пользовательский контент (UGC) | **Нет** — имена не публикуются |
| Онлайн-взаимодействие | **Нет** — нет чата/мультиплеера |
| Азартные игры | **Нет** |
| Насилие | **Нет** |
| Сексуальный контент | **Нет** (романтика/«любовь» — cartoon, без explicit) |
| Алкоголь / наркотики / табак | **Нет** |
| Покупки | **Да** — in-app (Premium remove ads) |
| Реклама | **Нет в MVP** (`lovetest.ads.enabled=false`) или **Да** после AdMob v2 |

## Особые режимы

| Режим | Примечание для IARC |
|-------|---------------------|
| Love test / protocol | Развлекательный «процент совместимости», не медицина |
| Wheel of ideas | Случайные подсказки; при наличии «18+» тем — отметить **Mature humor** если добавите |
| Zodiac | Астрология как entertainment, не fortune-telling с деньгами |

## Store listing alignment

- Full description: «Results are for entertainment only»
- In-app disclaimer: onboarding №4, result screens
- См. [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md), [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md)

## После прохождения

1. Сохраните IARC certificate ID в Play Console
2. При добавлении ads — **перепройти** questionnaire (Advertising ID)
3. При смене target audience — обновить Family policy declarations
