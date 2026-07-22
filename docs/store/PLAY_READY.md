# Play Console — готовность Love Tester

Обновлено: **2026-07-22**

## Статус (автопроверка)

```bash
./scripts/print_store_checklist.sh   # блокеров нет
./scripts/play_console_next.sh       # → Internal upload
./scripts/check_legal_urls.sh        # HTTP 200 ×3
```

Одностраничник заливки: **[INTERNAL_UPLOAD_NOW.md](./INTERNAL_UPLOAD_NOW.md)**

| Блок | Статус |
|------|--------|
| Код, тесты | ✅ 63 unit · 115 instrumented |
| Store PNG 67/67 (1080×1920) | ✅ |
| AAB + R8 mapping | ✅ `build/store-upload/app-release.aab` |
| Feature graphic 1024×500 | ✅ |
| Upload-пакет | ✅ `build/store-upload/` · `build/love-tester-store-upload.zip` |
| Privacy HTTPS URL | ✅ `https://skmlproduction.github.io/TestCompabilityLove/` (200×3) |
| Production upload key | ✅ `build/keystore/lovetest-upload.jks` |
| git remote | ✅ `skmlproduction/TestCompabilityLove` |

## Следующий шаг (только Play Console)

1. Create app (если ещё нет) + Store listing RU/EN  
2. App content: Privacy URL выше · **Ads No** · Data safety / IARC · 13+  
3. Monetize → IAP `remove_ads` Active  
4. **Internal testing** → upload AAB + mapping → testers → smoke  
5. Closed IAP: [CLOSED_IAP_SMOKE.md](./CLOSED_IAP_SMOKE.md)

Локально пересобрать пакет (если нужно):

```bash
./gradlew finalizeStoreReleaseLoveTest
```

## Документы

| Файл | Назначение |
|------|------------|
| [INTERNAL_UPLOAD_NOW.md](./INTERNAL_UPLOAD_NOW.md) | Заливка Internal сейчас |
| [INTERNAL_TESTING.md](./INTERNAL_TESTING.md) | Первый релиз |
| [GO_NO_GO_V1.md](./GO_NO_GO_V1.md) | Вердикт треков |
| [CLOSED_IAP_SMOKE.md](./CLOSED_IAP_SMOKE.md) | Closed purchase/restore |
| [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md) | Тексты листинга |
