# Общий pipeline (3 новых проекта + LockDraw)

Эталон: **NEWlockscreen** (LockDraw) — уже с кодом, 24 экрана, SVG, CI.  
Новые: **KeyboardThemes**, **TestCompabilityLove**, **EmojiMakerPlus**.

Универсальный playbook: `../NEWlockscreen/docs/product/CURSOR_NEW_APP_PLAYBOOK.md` (фазы **0→6**).

---

## 1. Один метод

| Шаг | Что делать |
|-----|------------|
| 0 | План работ + **нумерованные экраны** (№1…N) → PRD, STACK, legal |
| 1 | `screens_catalog.csv` (`screen_no` = тот же №) |
| 2 | SVG `screen{N}_{slug}_m3.svg` — **один экран за итерацию** |
| 3 | Kotlin scaffold, `verify*` |
| 4 | UI 1:1 по SVG, один `screen_id` за чат |
| 5–6 | Store PNG, аудит |

В каждой папке — свой `CURSOR_*_PLAYBOOK.md` / phase-0 prompt.

**Love Test:** [CURSOR_LOVE_TEST_PLAYBOOK.md](./CURSOR_LOVE_TEST_PLAYBOOK.md) · [SCREENS_MASTER_PLAN.md](./SCREENS_MASTER_PLAN.md) (№1–30).

---

## 2. Референс ≠ код

| Можно из декомпила | Нельзя |
|--------------------|--------|
| flows, список экранов, permissions | Java/Kotlin UI из APK |
| тексты Store (`description.rtf`) | Unity-сцены, бинарные assets |
| манифест (ads, billing) | Копипаст SDK-конфигов конкурента |

**Love Test:** Unity 3.0.5 — только сценарии. Новое: **Compose + локальный % + disclaimer**.

---

## 3. Скриншоты с устройства

`reference/screenshots/` / `screenshotsfromapp/` **часто пустые** → F2 по Store-тексту + layout, не «пиксель в пиксель» с APK.

**Рекомендация:** пройти APK на телефоне → PNG в `reference/screenshots/device/` + [INDEX.md](../../reference/screenshots/INDEX.md).

---

## 4. Юридическое

- Свои названия, иконки, иллюстрации, формулировки Store.
- Не копировать 1:1 бренд конкурента.

**Love Test:** не «Love Maker» / trademark без проверки. EN: Love Tester (рабочее).

---

## 5. Номера экранов — заморозить после фазы 0

```
SCREEN_PLAN №N  →  screens_catalog.screen_no  →  screen{N}_*_m3.svg  →  docs/screenshots/{ru,en}/*.png
```

Менять № после F2/F4 = путаница в CI и Store.

**Love Test:** зафиксировано **№1–29** (+ №30 протокольный — TBD). Не перенумеровывать.

---

## 6. Что скопировать с LockDraw (фаза F3+)

Из `NEWlockscreen`:

- `scripts/verify_ui_inventory.py`, `adb_screenshot_preview.sh`, placeholder-скрипты
- Gradle: `verifyUiInventory`, `verify*Release`, `captureScreenshotCatalog*`
- `DebugUiPreview`, `NavIntents` extras
- `screens_catalog.csv` — **паттерн**, не содержимое

Свои: `applicationId`, `screen_id`, имя задачи (`verifyLoveTest`).

---

## 7. Store и монетизация (решить в PRD)

Заранее зафиксировать: ads / IAP / premium / UMP / Data safety.  
В декомпилях — тяжёлая реклама; в новых — **минимум SDK**, не «как в оригинале».

**Love Test:** см. [PRODUCT_DECISIONS.md](./PRODUCT_DECISIONS.md) — черновик решений.

---

## 8. Работа в Cursor

- **Один чат ≈ одна фаза** или **один `screen_id` / один № SVG**.
- Не «сделай всё приложение» одним сообщением.

**SVG:** `<?xml encoding="UTF-8"?>`, кириллица через `&#…;` entities, без emoji в `<text>`.

---

## 9. Отличия по проекту

| Проект | Референс | Главная сложность | Фокус |
|--------|----------|-------------------|--------|
| **KeyboardThemes** | 3 конкурента | IME, много экранов | Одно приложение, не 3 клона; first — самый полный |
| **TestCompabilityLove** | 1 APK, **Unity** | Простой продукт, **не Unity** | Compose, локальный %, disclaimer |
| **EmojiMakerPlus** | 1 APK, редактор | Canvas, слои, экспорт | Horror = режим; WA stickers = MVP или v2 |
| **LockDraw** (эталон) | свой | — | Живые Store PNG вместо шаблонов; код близок к релизу |

---

## 10. Следующий шаг — Love Test (сейчас)

| Что | Статус | Действие в Cursor |
|-----|--------|-------------------|
| F0–F1 | ✅ | — |
| F2 SVG | 2/29 | Продолжить **№3** `onboarding_tests` |
| Решения PRD | черновик | Заполнить [PRODUCT_DECISIONS.md](./PRODUCT_DECISIONS.md) |
| Скрины device | пусто | По желанию: PNG → `reference/screenshots/device/` |
| F3 код | не начат | После ~6–8 SVG или полного F2 |

**Промпт на один экран:**

```
Love Test F2: нарисуй SVG экран №3 (onboarding_tests) по DESIGN_SYSTEM.md и screen2 эталон. XML entities для RU. Обнови README прогресс.
```
