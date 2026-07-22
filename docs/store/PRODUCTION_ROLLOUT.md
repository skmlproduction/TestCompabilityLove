# Production rollout — Love Tester v1.0.0

Package: `dev.lovetest.app` · `versionCode` 1 · `versionName` 1.0.0  
Монетизация v1: **premium-only**, Ads **No**, IAP `remove_ads`.

Связано: [RELEASE_ENGINEERING.md](../product/RELEASE_ENGINEERING.md) · [PLAY_FORMS_FILLED.md](./PLAY_FORMS_FILLED.md) · [INTERNAL_TESTING_RUNBOOK.md](./INTERNAL_TESTING_RUNBOOK.md)

---

## 1. Go / No-Go (перед Production)

### GO — когда все пункты ✅

| # | Критерий | Проверка |
|---|----------|----------|
| 1 | **Internal + Closed** smoke пройден (≥3 устройства, RU+EN) | Чеклист INTERNAL_TESTING §5 |
| 2 | **Production upload key** подписывает AAB; backup `.jks` вне репо | `keystore.properties` + `validate_store_upload` |
| 3 | **Play App Signing** включён; upload certificate зарегистрирован | Console → App integrity |
| 4 | **Privacy URL** HTTPS 200, совпадает с Store + `gradle.properties` | `curl -I` + Settings в приложении |
| 5 | **Data safety + IARC + Ads: No** — submitted, без блокеров Policy | Dashboard → Policy status |
| 6 | **Store listing** RU+EN, 7+ скринов, feature graphic, тексты финальные | [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md) |
| 7 | **SKU `remove_ads`** Active (для покупок после релиза) | Monetize → Products |
| 8 | **Pre-launch report** на release-кандидате — без critical | Release → Pre-launch |
| 9 | `verifyLoveTestBeforeStore` + `releaseGateLoveTest` зелёные | Локально / CI |
| 10 | **Release notes** RU+EN готовы (ниже) | Скопировать в Console |

### NO-GO — стоп до исправления

| Блокер | Действие |
|--------|----------|
| AAB подписан **debug** upload key без плана смены в Console | Prod keystore → новый AAB |
| Privacy 404 / example.com | Pages / `post_privacy_setup.sh` |
| Pre-launch: crash на старте, broken APK, target API fail | Хотфикс → новый `versionCode` |
| Data safety / rating incomplete | [PLAY_FORMS_FILLED.md](./PLAY_FORMS_FILLED.md) |
| Crashlytics не подключён — **не блокер v1**, но без автоматического краш-алерта — усилить ручной мониторинг vitals |

### Вердикт на 2026-06-04 (обновлено)

| Трек | Статус |
|------|--------|
| Upload-пакет + prod AAB | ✅ |
| Prod keystore | ✅ (см. `AGENT_STATUS` SHA-256) |
| Internal testing | **GO** — [GO_NO_GO_V1.md](./GO_NO_GO_V1.md) |
| Privacy Pages | ⏳ после `git push` + Pages workflow |
| Store PNG (UI-фиксы) | ⏳ пересъёмка рекомендуется |
| Production 100% | **NO-GO** до Closed smoke |

**Рекомендация:** **GO Internal** → Closed + billing → затем staged Production.

---

## 2. Staged rollout (10% → 50% → 100%)

Play Console → **Production** → **Releases** → **Staged rollout**.

| Этап | % пользователей | Минимум наблюдения | Критерии перехода |
|------|-----------------|--------------------|------------------|
| **S0** | 0% (review only) | — | Release approved Google |
| **S1** | **10%** | **24 ч** | Нет автоматического halt; crash rate стабилен vs pre-launch; нет всплеска 1★ «не открывается» |
| **S2** | **50%** | **24–48 ч** на S1 | Crash ≤ **1.0%** sessions; ANR ≤ **0.30%**; нет P0 багов в отзывах |
| **S3** | **100%** | **48 ч** на S2 | Те же пороги; IAP/restore smoke на prod треке OK |

### Пороги отката (halt rollout → full rollback)

Остановить staged rollout и рассмотреть **откат версии** (предыдущий `versionCode` или снятие с продажи), если **любое**:

| Метрика | Порог (v1, мало данных) |
|---------|-------------------------|
| **User-perceived crash rate** | > **1.5%** sessions **или** > **2×** pre-launch baseline |
| **ANR rate** | > **0.50%** |
| **Отзывы 1★** | ≥ **5** за 24 ч с повторяющимся P0 (crash, «обман», billing) |
| **Policy warning** | Любой strike / removal risk |
| **Vitals: startup crash** | Любой кластер >10 одинаковых crash/час на top device |

Действия при откате:

1. Console → **Halt rollout** (0% new users на bad release).
2. Собрать **1.0.1** hotfix (`versionCode` 2) или откатить к предыдущей версии, если была.
3. Загрузить mapping для deobfuscation хотфикса.
4. Постмортем в `AGENT_STATUS.md` + issue в трекере.

---

## 3. Release notes — Production (RU + EN)

Скопировать в Play Console → Production release.  
Короткая версия для «What’s new»; полный список — [RELEASE_NOTES_v1.0.0.md](./RELEASE_NOTES_v1.0.0.md).

### RU (≤500 символов рекомендуется)

```
Первый публичный релиз Love Tester — развлекательные тесты совместимости по имени.

• Love test, протокол любви, калькулятор, пара, зодиак, колесо идей
• Поделиться результатом
• RU / EN
• Опциональный Premium без рекламы (покупка в Google Play)

Результаты только для развлечения, не научный совет.
```

### EN

```
First public Love Tester release — fun name-based compatibility tests.

• Love test, love protocol, letter calculator, pair, zodiac, idea wheel
• Share your result
• Russian and English UI
• Optional Premium without ads (Google Play purchase)

For entertainment only — not scientific advice.
```

---

## 4. Финальная сверка перед Publish (Production)

| Раздел Console | Документ / артефакт |
|----------------|-------------------|
| App name, short, full RU | [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md) |
| App name, short, full EN | Там же |
| Screenshots | `build/store-upload/listing-screenshots/` |
| Feature graphic | `feature_graphic.png` |
| Privacy policy URL | = `gradle.properties` |
| Data safety | [DATA_SAFETY_FORM.md](./DATA_SAFETY_FORM.md) |
| Content rating | [IARC_QUESTIONNAIRE.md](./IARC_QUESTIONNAIRE.md) |
| Ads declaration | **No** |
| IAP | `remove_ads` Active |
| AAB + mapping | `app-release.aab`, `mapping.txt` |

---

## 5. Чеклист дня релиза (D0)

- [ ] Publish Production release (staged **10%**)
- [ ] Подтвердить **Play App Signing** enrollment
- [ ] Сохранить **IARC certificate ID**
- [ ] Скриншот Policy status = green
- [ ] Записать время старта S1 в `AGENT_STATUS.md`
- [ ] Включить напоминание: проверка vitals **+4 ч, +24 ч, +72 ч** ([POST_RELEASE_PLAN.md](./POST_RELEASE_PLAN.md))

---

## 6. После 100% rollout

- Мониторинг **72 ч** — [POST_RELEASE_PLAN.md](./POST_RELEASE_PLAN.md)
- План v1.1 / v2 — [V2_BACKLOG.md](../product/V2_BACKLOG.md)
- Closed track можно оставить для тестеров billing
