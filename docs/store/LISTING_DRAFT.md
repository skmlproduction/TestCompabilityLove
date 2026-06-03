# Google Play — черновик листинга (Love Tester)

Перед публикацией перепишите тексты; не копируйте дословно референс `info/description.rtf`.

## RU

**Название:** Тест на совместимость и любовь

**Краткое описание (80 символов):**  
Развлекательные тесты совместимости по именам — весело и бесплатно.

**Полное описание (черновик):**  
Узнайте процент «совместимости» по именам в игровом формате. Love Tester — набор лёгких тестов для пары и друзей: классический love test, **протокол любви** (3 шага → сигналы → вердикт), калькулятор букв, пара, знаки зодиака, колесо идей и другие режимы.

Результаты носят **развлекательный характер** и не являются научным прогнозом или советом.

**Что внутри:**
- Тест совместимости по двум именам
- **Протокол любви** — пошаговый режим (тест №8)
- Несколько игровых режимов в одном приложении
- Поделиться результатом с друзьями
- Русский и английский интерфейс
- Premium без рекламы (опционально)

## EN

**Title:** Love Tester

**Short description:**  
Fun name-based compatibility tests for couples and friends.

**Full description (draft):**  
Discover a playful “compatibility” percent from two names. Love Tester bundles lighthearted modes: classic love test, **love protocol** (3 steps → signals → verdict), letter calculator, pair test, zodiac signs, idea wheel, and more.

Results are **for entertainment only** — not scientific advice.

## Data safety (кратко)

См. `assets/legal/data_collection.html` и форму в Play Console. Имена обрабатываются на устройстве; реклама и покупки — по политикам Google.

Privacy URL: `lovetest.privacy.policy.url` в `gradle.properties` (см. `STORE_UPLOAD.md`, `./scripts/export_privacy_for_hosting.sh`).

## Скриншоты (готовы ✅)

После `./scripts/pack_store_upload.sh`:

```
build/store-upload/listing-screenshots/ru/   # 7 PNG для листинга
build/store-upload/listing-screenshots/en/
```

Полный каталог: **67** PNG в `docs/screenshots/` ( `./gradlew verifyLoveTestBeforeStore` ✅ ).
