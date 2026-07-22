# GitHub — first push и GitHub Pages (privacy + terms)

Репозиторий локально на ветке `main`, remote уже настроен на `maksimsokolov/TestCompabilityLove`.  
На GitHub репозиторий может **ещё не существовать** — создайте его, затем выполните команды ниже **у себя в терминале** (нужны SSH или HTTPS + токен).

Быстрая проверка: `./scripts/check_github_repo.sh` (HTTP 404 = repo ещё нет).

## 0. SSH-ключ (если `ssh-add -l` → «no identities»)

На этой машине ключей может не быть. Один раз:

```bash
# если файла ещё нет:
ssh-keygen -t ed25519 -C "lovetest" -f ~/.ssh/id_ed25519 -N ""
ssh-add --apple-use-keychain ~/.ssh/id_ed25519
pbcopy < ~/.ssh/id_ed25519.pub
# GitHub → Settings → SSH and GPG keys → New SSH key → вставить
```

Проверка: `ssh -T git@github.com` → «Hi USER!…»


> **Статус (2026-07-22):** repo `skmlproduction/TestCompabilityLove` live · Pages **HTTP 200 ×3**. Документ ниже — архив процедуры first push.

1. https://github.com/new  
2. **Repository name:** `TestCompabilityLove`  
3. **Public** (для бесплатных GitHub Pages)  
4. **Не** добавляйте README, .gitignore, license (локально уже есть история)

## 2. Закоммитить незакоммиченное (по желанию)

```bash
cd /Users/maksimsokolov/Desktop/TestAppsCursor/TestCompabilityLove
git status
# Store/docs и legal — отдельные коммиты или один «store docs» по вашему выбору
```

## 3. First push (SSH)

```bash
cd /Users/maksimsokolov/Desktop/TestAppsCursor/TestCompabilityLove

git remote -v
# Ожидается:
# origin  git@github.com:maksimsokolov/TestCompabilityLove.git

git branch -M main
git push -u origin main
```

### Альтернатива: HTTPS + Personal Access Token

```bash
cd /Users/maksimsokolov/Desktop/TestAppsCursor/TestCompabilityLove

git remote set-url origin https://github.com/skmlproduction/TestCompabilityLove.git
git branch -M main
git push -u origin main
# Username: maksimsokolov
# Password: <GitHub PAT с правом repo>
```

## 4. Включить GitHub Pages

1. GitHub → **TestCompabilityLove** → **Settings** → **Pages**  
2. **Build and deployment** → Source: **GitHub Actions**

## 5. Задеплоить legal (privacy + terms)

**Actions** → workflow **Privacy GitHub Pages** → **Run workflow** → branch `main`.

Или дождитесь автозапуска после push (изменения в `app/src/main/assets/legal/**`).

## 6. Проверить HTTPS

```bash
cd /Users/maksimsokolov/Desktop/TestAppsCursor/TestCompabilityLove
./scripts/check_legal_urls.sh
```

Ожидаемо HTTP 200 для:

- `https://skmlproduction.github.io/TestCompabilityLove/`
- `https://skmlproduction.github.io/TestCompabilityLove/terms.html`
- `https://skmlproduction.github.io/TestCompabilityLove/data-collection.html`

## 7. Play Console + приложение

URL уже в `gradle.properties`:

```properties
lovetest.privacy.policy.url=https://skmlproduction.github.io/TestCompabilityLove/
```

Скопируйте те же URL в листинг: [PLAY_CONSOLE_COPY.md](./PLAY_CONSOLE_COPY.md) (раздел Legal URLs).

После смены URL (если меняли):

```bash
./scripts/set_privacy_url.sh https://skmlproduction.github.io/TestCompabilityLove/
./gradlew :app:bundleRelease
```

Полный чеклист после push: `./scripts/post_push.sh`

## Troubleshooting

| Проблема | Решение |
|----------|---------|
| `Permission denied (publickey)` | Cursor-агент **не может** push без вашего SSH/PAT. В своём Terminal: `ssh-add --apple-use-keychain ~/.ssh/id_ed25519` (или ваш ключ), затем `git push -u origin main`. Альтернатива: HTTPS + PAT (раздел 3). |
| `Repository not found` | Создайте repo на GitHub под тем же именем |
| Pages 404 | Подождите 2–5 мин после workflow; проверьте Settings → Pages → Source: **GitHub Actions** |
| `check_legal_urls` FAIL | Re-run workflow; убедитесь, что в main есть `terms_of_use.html` |

После успешного push локально:

```bash
./scripts/check_legal_urls.sh   # HTTP 200 ×3
```
