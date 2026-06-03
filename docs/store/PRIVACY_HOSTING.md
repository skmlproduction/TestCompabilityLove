# Privacy policy — хостинг для Play Console

Play требует **публичный HTTPS URL**. Bundled WebView в APK — только для dev.

## Вариант A: GitHub Pages (рекомендуется)

### 1. Репозиторий на github.com (вручную)

1. [github.com/new](https://github.com/new) — имя репозитория, например `TestCompabilityLove`, **Public**, без README (код уже локально).
2. Локально (один раз):

```bash
./scripts/first_push.sh YOUR_GITHUB_USER TestCompabilityLove
git add .
git commit -m "Love Tester — store ready"
git push -u origin main
```

### 2. Включить Pages (вручную на github.com)

1. Откройте репозиторий → вкладка **Settings** (только у владельца).
2. Слева **Pages**.
3. **Build and deployment** → **Source:** выберите **GitHub Actions** (не «Deploy from a branch»).
4. Сохранение автоматическое; отдельной кнопки Save может не быть.

### 3. Запустить деплой privacy (вручную)

1. Вкладка **Actions**.
2. Слева workflow **Privacy GitHub Pages** (файл `pages-privacy.yml`).
3. Справа **Run workflow** → ветка **main** → **Run workflow**.
4. Дождитесь зелёной галочки **deploy** (1–3 мин). В job будет ссылка **github-pages** — это и есть живой URL.
5. Проверка в браузере: `https://YOUR_USER.github.io/TestCompabilityLove/` — должна открыться политика (RU/EN).

Альтернатива: любой **push в `main`**, если изменились `app/src/main/assets/legal/**` — workflow запустится сам.

Локально перед push (опционально):

```bash
./gradlew exportPrivacyForHosting   # build/legal-host/ для превью
```

### 4. URL в проекте (после живого Pages)

```bash
./scripts/post_privacy_setup.sh https://YOUR_USER.github.io/TestCompabilityLove/
```

Скрипт: `set_privacy_url` → `gradle.properties` → `check_privacy_url` (curl) → `exportPrivacyForHosting` → при наличии keystore — `bundleRelease`.

Тот же URL → Play Console → **Store listing** → **Privacy policy**.

Workflow: `.github/workflows/pages-privacy.yml`

### 3. URL в проекте

После деплоя URL будет вида:

```
https://<USER>.github.io/<REPO>/
```

Пример для репозитория `TestCompabilityLove`:

```bash
./scripts/set_privacy_url.sh https://YOUR_USER.github.io/TestCompabilityLove/
./gradlew bundleReleaseLoveTest
```

Тот же URL → Play Console → **Store listing** → **Privacy policy**.

## Вариант B: Свой домен

```bash
./gradlew exportPrivacyForHosting
# Залить build/legal-host/index.html на HTTPS
./scripts/set_privacy_url.sh https://your-domain.com/privacy
```

## Проверка

```bash
./gradlew exportPrivacyForHosting
./scripts/serve_privacy_preview.sh          # локальный превью http://127.0.0.1:8765/
curl -I https://YOUR_URL/
./gradlew checkPrivacyUrlLoveTest
./scripts/validate_store_upload.sh
```

После смены URL пересоберите release — `BuildConfig.PRIVACY_POLICY_URL` берётся из `gradle.properties`.
