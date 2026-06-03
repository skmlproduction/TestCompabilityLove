# Playbook: Love Test (пересборка)

Промпты для Cursor по методу **LockDraw** (`../NEWlockscreen/docs/product/CURSOR_NEW_APP_PLAYBOOK.md`).  
Универсальные шаблоны с плейсхолдерами — в том файле; здесь — **константы проекта**.

**Общие правила** (KeyboardThemes / Love Test / EmojiMakerPlus): [CROSS_PROJECT_PIPELINE.md](./CROSS_PROJECT_PIPELINE.md).  
**Решения продукта** (бренд, ads, локали): [PRODUCT_DECISIONS.md](./PRODUCT_DECISIONS.md).

| Плейсхолдер | Значение |
|-------------|----------|
| `{{APP_NAME}}` | Love Tester |
| `{{APP_NAME_RU}}` | Тест на совместимость и любовь |
| `{{APP_SLUG}}` | `love_test` |
| `{{PACKAGE}}` | `dev.lovetest.app` |
| `{{ONE_LINER}}` | Развлекательные тесты совместимости и «любви» по именам, буквам и знакам зодиака — для пары и друзей |
| `{{PRIMARY}}` | `#C2185B` (M3 seed; акцент `#E91E63`) |
| Gradle verify | `verifyLoveTest` (аналог `verifyLockDraw`) |

**Референс UX:** `info/description.rtf`, `docs/product/REFERENCE_SOURCES.md`, скриншоты в `reference/screenshots/`.  
**Не копировать:** Unity/C# из декомпила, trademark оригинала.

---

## Фаза 0 — Объёмный план и продукт ✅

**Статус:** выполнена в репозитории (см. оглавление ниже).

Промпт (для повторного прогона):

```
Ты product + tech lead. Спроектируй Android-приложение «Love Tester» (RU: Тест на совместимость и любовь) с нуля.

Контекст: развлекательные тесты совместимости по именам; локальный расчёт в MVP; реклама + IAP premium как в референсе 3.0.5, но новая реализация.

Прочитай: info/description.rtf, docs/product/REFERENCE_SOURCES.md, docs/product/screens_catalog_DRAFT.md.

Создай/обнови docs/product: PRD.md, STACK.md, RELEASE_ENGINEERING.md, ONBOARDING_AND_LEGAL.md, WORK_PLAN.md, GOOGLE_PLAY_RELEASE_CHECKLIST.md.
Создай reference/screenshots/{play,device}/ и INDEX.md.

Primary M3: #C2185B. Пакет: dev.lovetest.app. Без production Kotlin на этой фазе.

В конце: оглавление файлов и порядок F1→F6.
```

---

## Фаза 1 — Инвентарь экранов

Подставь в шаблон **Фаза 1** из `CURSOR_NEW_APP_PLAYBOOK.md`:

- `{{APP_NAME}}` → Love Tester  
- `verifyLockDraw` → `verifyLoveTest`  
- Учти черновик `screens_catalog_DRAFT.md` (~26 screen_id)  
- После съёмки `reference/screenshots/` — уточни hub и названия тестов

---

## Фаза 2 — Дизайн SVG

**2A:** `DESIGN_SYSTEM.md` + `screen1_love_test_hub_main_m3.svg`  
**2B:** пакеты по 4–6 screen_id из `screens_catalog.csv`  
**2C:** аудит 100% SVG ↔ CSV  

Романтическая M3: surface `#FFFBFE`, primary `#C2185B`, сердца/проценты в hero.

---

## Фаза 3 — Scaffold

`verifyLoveTest`, модули `app` + `core:ui`, NavHost по CSV, Theme из DESIGN_SYSTEM, DEBUG_UI_PREVIEW.

Скопировать из **NEWlockscreen** и переименовать: `scripts/verify_ui_inventory.py`, placeholder-скрипты, задачи Gradle.

---

## Фаза 4 — UI 1:1

Очередь (после F1 CSV):

1. `splash_brand`, `hub_main`, `onboarding_*`
2. `love_test_input` → `love_test_result` (+ `calculating`, `result_low`)
3. Остальные тесты, `premium_*`, `settings_main`
4. Состояния `hub_loading`, `error_network`

Один `screen_id` за итерацию; промпт — `UI_IMPLEMENTATION_PROMPT.md` (создать в F2/F3).

---

## Фаза 5–6 — Store и аудит

Скриншоты `docs/screenshots/ru|en/`, `verifyLoveTestBeforeStore`, полный аудит CSV ↔ SVG ↔ Composable ↔ PNG.

---

## Оглавление артефактов (фаза 0)

| Файл | Назначение |
|------|------------|
| [PRD.md](./PRD.md) | Vision, user stories, MVP/v2, NFR |
| [STACK.md](./STACK.md) | Kotlin, Compose, M3, модули, Koin |
| [RELEASE_ENGINEERING.md](./RELEASE_ENGINEERING.md) | Версии, R8, CI, verifyLoveTest |
| [ONBOARDING_AND_LEGAL.md](./ONBOARDING_AND_LEGAL.md) | Онбординг, UMP, disclaimer |
| [WORK_PLAN.md](./WORK_PLAN.md) | Фазы F1–F6, риски, отложено |
| [GOOGLE_PLAY_RELEASE_CHECKLIST.md](./GOOGLE_PLAY_RELEASE_CHECKLIST.md) | Data safety, треки |
| [REFERENCE_SOURCES.md](./REFERENCE_SOURCES.md) | Референс 3.0.5 |
| [screens_catalog_DRAFT.md](./screens_catalog_DRAFT.md) | Черновик screen_id |

---

## Рекомендуемая последовательность чатов

| # | Режим | Действие |
|---|--------|----------|
| 1 | Plan/Agent | Фаза 0 ✅ |
| 2 | Agent | **Фаза 1** — инвентарь + CSV + verify скрипт |
| 3 | Agent | Фаза 2A → 2B → 2C |
| 4 | Agent | Фаза 3 |
| 5 | Agent × N | Фаза 4 по screen_id |
| 6–7 | Agent | Фаза 5–6 |

**Правило в `.cursor/rules` (добавить при F3):** UI только из `docs/design/*.svg`; один screen_id за задачу; `verifyLoveTest` после шага.
