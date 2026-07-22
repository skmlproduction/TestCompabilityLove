# Post-release plan — первые 72 часа (v1.0.0)

Package: `dev.lovetest.app` · См. [PRODUCTION_ROLLOUT.md](./PRODUCTION_ROLLOUT.md) для staged rollout.

---

## 1. Мониторинг (Play Console + Vitals)

### Где смотреть

| Источник | Что |
|----------|-----|
| **Android vitals** → Crashes & ANRs | Crash-free users, clusters, stack traces (с mapping) |
| **Android vitals** → Release dashboard | Сравнение по `versionCode` 1 |
| **Pre-launch report** | Регрессии после публикации |
| **Statistics** → Installs / Uninstalls | Всплески uninstall после update |
| **User feedback** → Reviews | 1–2★, ключевые слова |
| **Monetize** → Revenue (после первых покупок) | IAP `remove_ads` |

### Расписание (первые 72 ч)

| Время | Действие |
|-------|----------|
| **+4 ч** | Vitals crash/ANR; 5 последних отзывов; rollout % |
| **+24 ч** | Решение S1→50% или halt; топ-3 crash stacks |
| **+48 ч** | На 50%: отзывы + uninstall rate |
| **+72 ч** | Решение 50%→100%; краткий отчёт в `AGENT_STATUS.md` |

### Целевые ориентиры (v1, entertainment app)

| Метрика | Зелёный | Жёлтый | Красный (→ halt) |
|---------|---------|--------|------------------|
| Crash rate | < 0.8% | 0.8–1.5% | > 1.5% |
| ANR rate | < 0.2% | 0.2–0.5% | > 0.5% |
| Rating avg (если ≥10 отзывов) | ≥ 4.0 | 3.5–4.0 | < 3.5 + P0 темы |

*v1 без Crashlytics — опора на Play Vitals; подключение Firebase — см. V2 backlog P2.*

---

## 2. Ответы на отзывы (шаблоны)

Тон: вежливо, коротко, **развлекательный** характер, без обещаний «правды о совместимости».

### RU — общий негатив «врёт / неправда»

```
Спасибо за отзыв! Love Tester — игровой тест для развлечения, результат не является научным прогнозом. Мы уточняем это в приложении и в описании Store. Если что-то не работает — напишите, пожалуйста, модель телефона, мы разберёмся.
```

### RU — реклама (если спрашивают)

```
В версии 1.0 рекламы нет. Если видите экран Premium — это опциональная покупка «без рекламы» на будущее, все тесты доступны бесплатно. Спасибо!
```

### EN — misleading / scam concern

```
Thanks for your feedback. Love Tester is for entertainment only — results are not scientific advice. We state this in the app and store listing. If something is broken, please share your device model and we’ll investigate.
```

### RU/EN — благодарность 4–5★

```
Спасибо, что пользуетесь Love Tester! Будем рады, если поделитесь с друзьями. / Thank you for using Love Tester — glad you’re enjoying it!
```

**SLA:** ответ на 1–2★ с P0 — в течение **48 ч**; остальные — в течение **7 дней**.

---

## 3. Порог отката (кратко)

См. полная таблица в [PRODUCTION_ROLLOUT.md](./PRODUCTION_ROLLOUT.md) §2.

**Halt staged rollout**, если: crash >1.5%, ANR >0.5%, policy alert, кластер startup crash.

**Hotfix 1.0.1**, если: воспроизводимый P0 на top-3 devices, fix < 1 день разработки.

---

## 4. План хотфикса (1.0.1)

| Шаг | Действие |
|-----|----------|
| 1 | Воспроизвести на device/API level из Vitals |
| 2 | Fix + `versionCode` 2, `versionName` 1.0.1 |
| 3 | `./gradlew verifyLoveTest` → `bundleRelease` → `pack_store_upload.sh` |
| 4 | Upload **Production** (не Internal) с **full rollout 100%** после быстрого Internal smoke |
| 5 | Release notes: «Исправлена стабильность / Fixed crash on …» |
| 6 | Обновить `AGENT_STATUS.md` |

Шаблон release notes хотфикса:

```
RU: Исправления стабильности и мелкие улучшения интерфейса.
EN: Stability fixes and minor UI improvements.
```

---

## 5. Что НЕ делать в первые 72 ч

- Включать **AdMob** без Closed test и обновления Data safety
- Менять **upload key** без регистрации в Play App Signing
- Отвечать агрессивно на негативные отзывы
- Обещать в отзывах «точную совместимость» или «предсказание будущего»

---

## 6. Отчёт T+72 (шаблон для AGENT_STATUS)

```
Rollout: S1 10% → … → 100% (даты)
Vitals: crash X%, ANR Y%
Reviews: N новых, avg Z
IAP: N покупок remove_ads (если есть)
Решение: v1.0.1 нужен? / v2 приоритет?
```
