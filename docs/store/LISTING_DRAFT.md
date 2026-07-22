# Google Play — листинг Love Tester (финал v1.0)

Канонические тексты для Console: **[PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md)**.

## ASO-ключи (естественно в текстах)

| RU | EN |
|----|-----|
| тест на совместимость, love test, тест любви | love test, compatibility test |
| love calculator, калькулятор любви, по имени | love calculator, name compatibility |
| тест пары, имя, зодиак, колесо | couple test, zodiac, idea wheel |

## Честность скриншотов (7 приоритетных)

**Статус 2026-06-03:** RU/EN — 7/7 файлов в `docs/screenshots/{ru,en}/`, **1080×1920**, **270–545 KB** (не placeholder).  
`build/store-upload/listing-screenshots/` — копии для upload-пакета.

| screen_id | Дисклеймер в UI |
|-----------|-----------------|
| `love_test_result` | Да — длинный disclaimer на результате |
| `protocol_result` | Да — «только для развлечения» |
| `wheel_spin` | Да — «not a bet» / entertainment (EN/RU strings) |
| `premium_paywall` | Опциональная покупка, кнопка «бесплатно» |

**Не в первых 7 кадрах:** `onboarding_disclaimer` — при желании добавьте **8-й** скриншот в Console для усиления доверия.

Пересъёмка (если меняли UI):

```bash
./scripts/capture_store_local.sh both priority
# или
./gradlew capturePriorityScreensLoveTest
./gradlew -Plocale=en capturePriorityScreensLoveTest
./gradlew verifyLoveTestBeforeStore
./scripts/pack_store_upload.sh
```

## Feature graphic

См. [FEATURE_GRAPHIC.md](./FEATURE_GRAPHIC.md) — дисклеймер «For entertainment only» на макете.

## Privacy & монетизация

- Privacy URL: `gradle.properties` → GitHub Pages  
- v1: **без рекламы**; IAP `remove_ads` optional — [PLAY_FORMS_FILLED.md](./PLAY_FORMS_FILLED.md)

## Gate перед Store

```bash
python3 scripts/verify_ui_inventory.py --require-screenshots --fail-on-placeholders
./gradlew verifyLoveTestBeforeStore   # + verifyLoveTest
```

Ожидаемо: **67/67** реальных PNG, **0** placeholder.
