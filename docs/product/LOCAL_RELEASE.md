# Локальный релиз — Love Tester

Актуальный чеклист: **[../store/PLAY_READY.md](../store/PLAY_READY.md)**

## Быстрая проверка

```bash
./scripts/print_store_checklist.sh
./gradlew finalizeStoreReleaseLoveTest
```

## Артефакты

| Путь | Назначение |
|------|------------|
| `build/store-upload/app-release.aab` | Upload в Play |
| `build/store-upload/mapping.txt` | Deobfuscation |
| `build/store-upload/listing-screenshots/` | 7 PNG × RU/EN |
| `docs/screenshots/ru|en/` | Полный каталог 67 PNG |

## Privacy URL

```bash
./gradlew exportPrivacyForHosting
./scripts/suggest_privacy_url.sh USER REPO --apply
./scripts/check_privacy_url.sh
./gradlew bundleReleaseLoveTest
```

## Подпись upload key

```bash
# Smoke-test (локально)
./scripts/generate_debug_upload_keystore.sh

# Production (Play)
LOVETEST_KEYSTORE_PASS='***' LOVETEST_KEY_PASS='***' ./scripts/generate_upload_keystore.sh
./gradlew bundleReleaseLoveTest
```

## Store checklist

1. Privacy HTTPS — `./scripts/check_privacy_url.sh`
2. Production keystore — `generate_upload_keystore.sh`
3. `./gradlew verifyLoveTestBeforeStore` ✅ (67 PNG)
4. `./scripts/pack_store_upload.sh` → Play Console

См. [INTERNAL_TESTING.md](../store/INTERNAL_TESTING.md) · [GOOGLE_PLAY_RELEASE_CHECKLIST.md](./GOOGLE_PLAY_RELEASE_CHECKLIST.md)
