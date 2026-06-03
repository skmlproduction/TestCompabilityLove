# Feature Graphic — Google Play

**Размер:** 1024 × 500 px · **Формат:** PNG или JPEG · **Макс.:** 15 MB

## Исходник в репозитории

| Файл | Назначение |
|------|------------|
| `feature_graphic_love_tester_m3.svg` | Векторный макет (M3 pink + protocol teal) |
| `feature_graphic.png` | Экспорт для Console (генерируется локально) |

## Экспорт PNG

```bash
./scripts/export_feature_graphic.sh
# → docs/store/feature_graphic.png
```

Требуется `rsvg-convert` (`brew install librsvg`) или ImageMagick.

## Содержание макета

- Бренд **Love Tester**, подзаголовок RU + EN
- Карточка **Love protocol** (тест №8, teal `#00796B`)
- Дисклеймер «For entertainment only»
- Палитра: `#C2185B` → `#E91E63` (см. `docs/design/DESIGN_SYSTEM.md`)

## Альтернатива

Figma / Canva по `docs/design/screen1_love_test_splash_brand_m3.svg` — без обещаний «научной» совместимости.

**Не коммитьте** `feature_graphic.png`, если политика команды — только загрузка в Console (файл в `.gitignore`).
