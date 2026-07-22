# Internal upload — сейчас (Love Tester)

Аккаунт GitHub/Pages: **skmlproduction** · Package: `dev.lovetest.app`

Локальный чеклист `./scripts/print_store_checklist.sh` → **блокеров нет**.

## Файлы для загрузки

| Что | Путь |
|-----|------|
| AAB | `build/store-upload/app-release.aab` (~8.4 MB, собран 2026-07-22) |
| ZIP всего пакета | `build/love-tester-store-upload.zip` |
| mapping (R8) | `build/store-upload/mapping.txt` |
| Listing 7×RU / 7×EN | `build/store-upload/listing-screenshots/{ru,en}/` |
| Feature graphic | `build/store-upload/feature_graphic.png` |

## Privacy / Terms (вставить в Console)

- Privacy: https://skmlproduction.github.io/TestCompabilityLove/
- Terms: https://skmlproduction.github.io/TestCompabilityLove/terms.html
- Data collection: https://skmlproduction.github.io/TestCompabilityLove/data-collection.html

## Upload certificate SHA-256 (App integrity)

```
6D:27:9A:11:08:2C:C0:B5:DA:A4:9B:9B:3B:1C:CD:0B:1B:0D:F4:14:03:ED:C2:0A:B4:1B:7C:FF:22:5D:B3:19
```

Проверка: `./scripts/print_upload_cert_sha.sh`

## По шагам в Play Console

1. Create app (если ещё нет) → **Love Tester**
2. **Store listing** — тексты из `PLAY_CONSOLE_COPY.md` + 7 PNG + feature graphic
3. **App content** — Privacy URL выше · Ads **No** · 13+ · Data safety / IARC по формам в `build/store-upload/`
4. **Monetize** → in-app product **`remove_ads`** → Active
5. **Testing → Internal testing** → Create release → upload AAB → Start rollout
6. Добавить Gmail тестеров → установить с Internal link → smoke
7. После smoke: Closed IAP — [CLOSED_IAP_SMOKE.md](./CLOSED_IAP_SMOKE.md)

Подробнее: [INTERNAL_TESTING_RUNBOOK.md](./INTERNAL_TESTING_RUNBOOK.md) · [PLAY_READY.md](./PLAY_READY.md)
