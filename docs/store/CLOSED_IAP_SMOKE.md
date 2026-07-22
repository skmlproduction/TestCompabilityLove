# Closed IAP smoke — Love Tester

Ручной чеклист перед Production. Агент **не может** выполнить покупку: нужен Play Console + license tester на устройстве.

**Предусловия:** Internal smoke OK · Privacy/Terms/Data-collection **HTTP 200** · SKU `remove_ads` **Active**.

## 1. Console (один раз)

- [ ] Monetize → Products → In-app → **`remove_ads`** Active, цена RU/EN
- [ ] Setup → License testing → Gmail тестера(ов)
- [ ] Closed testing track: AAB из `build/store-upload/app-release.aab` + testers
- [ ] Устройство: Play Store под Gmail из License testing

## 2. Покупка

- [ ] Установить Closed build
- [ ] Hub → Premium / Settings → Premium
- [ ] Paywall: цена с Play (не только fallback string)
- [ ] Купить → экран **Thank you**
- [ ] Premium-состояние видно в Hub/Settings (без ads-копирайта «все тесты» — v1 = поддержка разработки)

## 3. Restore

- [ ] Clear app data **или** переустановка Closed build
- [ ] Settings → **Restore purchases**
- [ ] Premium снова активен без повторной оплаты
- [ ] Повторный Restore не крашит (Restored / NotFound / Unavailable)

## 4. Негатив (быстро)

- [ ] Аккаунт **не** в License testing → покупка отклоняется / недоступна, UI без краша
- [ ] Offline: paywall не падает; restore показывает Unavailable/ошибку тостом

## 5. Зафиксировать

- [ ] Дата / устройство / Gmail (без пароля) в заметке релиза
- [ ] Обновить `AGENT_STATUS.md`: «Closed IAP smoke ✅»
- [ ] `GO_NO_GO_V1.md` → Production можно рассматривать после legal 200

См. также: [BILLING_SETUP.md](./BILLING_SETUP.md) · [GO_NO_GO_V1.md](./GO_NO_GO_V1.md)
