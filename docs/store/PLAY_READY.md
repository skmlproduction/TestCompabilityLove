# Play Console — готовность Love Tester

Обновлено: 2026-05-24

## Статус (автопроверка)

```bash
./scripts/project_health.sh
./scripts/print_store_checklist.sh
./scripts/play_console_next.sh
./scripts/setup_github_remote.sh USER REPO
./scripts/prepare_git_push.sh
./gradlew countTestsLoveTest
./gradlew finalizeStoreReleaseLoveTest
```

| Блок | Статус |
|------|--------|
| Код, тесты, audit P0/P1 | ✅ (36 unit · 49 Compose UI + 7 route smoke) |
| Store PNG 67/67 (1080×1920) | ✅ |
| AAB + R8 mapping | ✅ |
| Feature graphic 1024×500 | ✅ |
| Upload-пакет | `build/store-upload/` · ZIP `build/love-tester-store-upload.zip` |
| Privacy HTTPS URL | ⏳ `example.com` → [PRIVACY_HOSTING.md](./PRIVACY_HOSTING.md) |
| Production upload key | ⏳ [generate_upload_keystore.sh](../scripts/generate_upload_keystore.sh) |
| git remote + first commit | ⏳ `./scripts/first_push.sh USER REPO` |

## 4 шага до Internal testing

### 0. GitHub remote + push

```bash
./scripts/first_push.sh YOUR_USER YOUR_REPO  # remote + gate (без commit)
# или по шагам:
./scripts/onboard_release.sh YOUR_USER YOUR_REPO
./scripts/suggest_first_commit.sh            # dry-run: что попадёт в git
./scripts/prepare_git_push.sh
git add . && git commit -m "Love Tester — store ready"
git push -u origin main
./scripts/post_push.sh                       # Pages → privacy → keystore
```

### 1. Privacy URL

```bash
./gradlew exportPrivacyForHosting
# GitHub: Settings → Pages → GitHub Actions → Run «Privacy GitHub Pages»
./scripts/post_privacy_setup.sh https://YOUR_USER.github.io/YOUR_REPO/
```

### 2. Production keystore

```bash
LOVETEST_KEYSTORE_PASS='***' LOVETEST_KEY_PASS='***' ./scripts/generate_upload_keystore.sh
./gradlew bundleReleaseLoveTest
```

Backup `build/keystore/lovetest-upload.jks` — обязателен.

### 3. Upload

```bash
./scripts/finalize_store_release.sh
```

Загрузить **`build/store-upload/`** (или **`build/love-tester-store-upload.zip`**) по [INTERNAL_TESTING.md](./INTERNAL_TESTING.md):

- `app-release.aab` + `mapping.txt`
- `feature_graphic.png`
- `listing-screenshots/ru|en/` (7 PNG каждый)
- Тексты: `PLAY_CONSOLE_COPY.md`, `DATA_SAFETY_FORM.md`

## Документы

| Файл | Назначение |
|------|------------|
| [INTERNAL_TESTING.md](./INTERNAL_TESTING.md) | Первый релиз |
| [STORE_UPLOAD.md](./STORE_UPLOAD.md) | Полный pipeline |
| [PRIVACY_HOSTING.md](./PRIVACY_HOSTING.md) | GitHub Pages |
| [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md) | Тексты листинга |
