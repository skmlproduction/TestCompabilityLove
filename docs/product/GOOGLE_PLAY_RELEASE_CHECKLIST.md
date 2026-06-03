# Чеклист: Google Play — Love Tester

Черновик для владельца Console. Перед публикацией сверьте с [актуальными требованиями Google](https://support.google.com/googleplay/android-developer/answer/10787469).

## 1. Идентичность приложения

| Поле | Рекомендация |
|------|----------------|
| Package | `dev.lovetest.app` |
| Название RU | Тест на совместимость и любовь |
| Название EN | Love Tester (уточнить уникальность в Store) |
| Категория | Entertainment / Lifestyle |
| Контакт разработчика | Email + сайт политики |

## 2. Локализация листинга

- Описание: на базе `info/description.rtf`, **переписать** (не копировать дословно).
- Языки: **RU + EN** минимум; строки приложения `values` / `values-en`.
- Short description: упор на «развлечение», не «научная совместимость».

## 3. Скриншоты и графика

- Каталог: `docs/product/screens_catalog.csv` — **34** screen_id, **67** PNG (RU+EN).
- PNG: `docs/screenshots/ru/`, `docs/screenshots/en/` — **67/67 реальных** 1080×1920 ✅
- Workflow: `docs/screenshots/WORKFLOW.md`, `CAPTURE_CHECKLIST.md`.
- Проверка: `./gradlew verifyLoveTestBeforeStore` ✅
- Feature Graphic + иконка — в Console (вектор из `:app`).

**Приоритет для листинга:** `hub_main`, `love_test_input`, `love_test_result`, `protocol_input`, `protocol_result`, `wheel_spin`, `premium_paywall` (см. `LISTING_DRAFT.md`).

## 4. Тестовые треки

1. Internal testing — первый AAB.
2. Closed testing — billing + ads consent.
3. Production — после `verifyLoveTestBeforeStore` и обновления Data safety.

```bash
./gradlew verifyLoveTest              # unit + lint + UI/test inventory
./gradlew countTestsLoveTest          # inventory: unit + Compose UI + route smoke
./scripts/run_compose_ui_tests.sh     # Compose UI на эмуляторе (adb)
./gradlew verifyLoveTestRelease       # AAB с R8
```

**Качество (2026-05):** 36 unit · 56 instrumented (49 Compose UI + 7 route smoke).

## 5. Data safety (черновик MVP)

| Данные | Собирается? | Куда | Примечание |
|--------|-------------|------|------------|
| Имена (ввод) | Локально на устройстве | Не на сервер в MVP | Опционально кэш в DataStore |
| Advertising ID | Если ads включены | Mediation SDK | UMP перед показом |
| Покупки | Google Play | Google | Billing Library |
| Analytics | Если Firebase добавлен | Google | Обновить форму |
| Crash logs | Если Crashlytics | Google | Опционально |
| Файлы | Share по действию пользователя | Выбранное приложение | Не фоновая отправка |

**Шифрование:** HTTPS для любых будущих API; MVP без своего backend — «данные не передаются на сервер разработчика».

Текст для пользователя: `settings_data_collection` / `settings_data_collection_sub` (RU/EN) — см. Settings.

## 6. Политики и рейтинг

- [ ] Privacy Policy URL в листинге и в приложении (`lovetest.privacy.policy.url` или `./scripts/export_privacy_for_hosting.sh`).
- [ ] IARC: нет азарта, насилия; честно про «love fantasy wheel» — см. `docs/store/IARC_QUESTIONNAIRE.md`
- [ ] Declarations: ads (если есть), billing.
- [ ] **Misleading claims:** disclaimer в описании и в приложении.

## 7. Технический релиз

- [ ] `targetSdk` 35
- [ ] Play App Signing
- [ ] R8 mapping uploaded
- [ ] Нет лишних permissions (storage legacy — избегать)
- [ ] Подпись: `keystore.properties` не в git

## 8. Монетизация

- [ ] In-app products созданы в Console (remove ads).
- [ ] Ценовой диапазон согласован (референс: $0.99–$99.99 — сузить для MVP).
- [ ] Тестовые лицензии на closed track.

## 9. Связанные документы

- `PRD.md`, `ONBOARDING_AND_LEGAL.md`, `RELEASE_ENGINEERING.md`
- `docs/store/LISTING_DRAFT.md`, `docs/store/DATA_SAFETY_FORM.md`
- `docs/store/PLAY_READY.md` — **сводка готовности + pipeline до Internal testing**
- `REFERENCE_SOURCES.md` — что было в оригинале 3.0.5

## 10. Pipeline до первого upload (кратко)

```bash
./scripts/first_push.sh USER REPO
git add . && git commit -m "Love Tester — store ready" && git push -u origin main
./scripts/post_push.sh                       # Pages → privacy → keystore
./scripts/post_privacy_setup.sh https://USER.github.io/REPO/
LOVETEST_KEYSTORE_PASS='***' ./scripts/generate_upload_keystore.sh
./gradlew bundleReleaseLoveTest
./gradlew finalizeStoreReleaseLoveTest
```

Следующий шаг: `./scripts/play_console_next.sh`
