#!/usr/bin/env bash
# Один следующий шаг до Play Console по текущему состоянию репозитория.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== play_console_next ==="
echo ""

read_prop() {
  local key="$1"
  if [[ ! -f gradle.properties ]]; then
    return 0
  fi
  grep -E "^${key}=" gradle.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true
}

privacy="$(read_prop lovetest.privacy.policy.url)"
store_path=""
is_debug_ks=false
if [[ -f keystore.properties ]]; then
  store_path="$(grep -E '^storeFile=' keystore.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
fi
if [[ -n "${store_path}" && -f "${store_path}" ]]; then
  if [[ "${store_path}" == *debug* || "${store_path}" == *upload-debug* ]]; then
    is_debug_ks=true
  fi
else
  store_path=""
fi

git_ok=false
git_remote=false
git_has_commits=false
git_needs_push=false
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  git_ok=true
  git remote get-url origin >/dev/null 2>&1 && git_remote=true
  if git rev-parse HEAD >/dev/null 2>&1; then
    git_has_commits=true
    if [[ "${git_remote}" == true ]]; then
      if git rev-parse --abbrev-ref '@{u}' >/dev/null 2>&1; then
        ahead="$(git rev-list --count '@{u}..HEAD' 2>/dev/null || echo 0)"
        [[ "${ahead}" -gt 0 ]] && git_needs_push=true
      else
        git_needs_push=true
      fi
    fi
  fi
fi

step=0
title=""
cmd=""
note=""

if [[ "${git_ok}" == false ]]; then
  step=1
  title="Инициализировать git (нужен для GitHub Pages и CI)"
  cmd="./scripts/init_git_for_github.sh YOUR_GITHUB_USER YOUR_REPO"
  note="Замените USER/REPO. Commit и push — вручную после init."
elif [[ "${git_remote}" == false ]]; then
  step=1
  title="Настроить GitHub remote и подготовить push"
  cmd="./scripts/first_push.sh YOUR_GITHUB_USER YOUR_REPO"
  note="Затем вручную:
git add . && git commit -m \"Love Tester — store ready\"
git push -u origin main
./scripts/post_push.sh"
elif ! git rev-parse HEAD >/dev/null 2>&1; then
  step=1
  title="Создать первый commit"
  cmd="./scripts/suggest_first_commit.sh"
  note="./scripts/prepare_git_push.sh
git add . && git commit -m \"Love Tester — store ready\"
git push -u origin main
./scripts/post_push.sh"
elif [[ "${git_needs_push}" == true ]]; then
  step=1
  title="Push на GitHub (нужен для CI и GitHub Pages)"
  cmd="git push -u origin main"
  note="Затем: ./scripts/post_push.sh"
elif [[ -z "${privacy}" || "${privacy}" == https://example.com/privacy ]]; then
  step=2
  title="Опубликовать privacy policy на HTTPS (GitHub Pages)"
  if [[ "${git_has_commits}" == true && "${git_remote}" == true ]]; then
    cmd="./scripts/post_push.sh"
    note="Pages workflow → post_privacy_setup.sh → production keystore"
  else
    cmd="./gradlew exportPrivacyForHosting"
    note="GitHub → Settings → Pages → GitHub Actions → Run «Privacy GitHub Pages»
Затем: ./scripts/post_privacy_setup.sh https://USER.github.io/REPO/"
  fi
elif [[ "${is_debug_ks}" == true || -z "${store_path}" ]]; then
  step=3
  title="Создать production upload keystore"
  cmd="LOVETEST_KEYSTORE_PASS='***' LOVETEST_KEY_PASS='***' ./scripts/generate_upload_keystore.sh"
  note="Backup build/keystore/lovetest-upload.jks обязателен. Затем: ./gradlew bundleReleaseLoveTest"
elif ! bash scripts/check_privacy_url.sh "${privacy}" >/dev/null 2>&1; then
  step=2
  title="Privacy URL недоступен — проверьте deploy Pages"
  cmd="./scripts/check_privacy_url.sh ${privacy}"
  note="См. docs/store/PRIVACY_HOSTING.md"
else
  aab="$(ls -1 app/build/outputs/bundle/release/*.aab 2>/dev/null | head -1 || true)"
  pack_aab="build/store-upload/app-release.aab"
  pack_zip="build/love-tester-store-upload.zip"
  if [[ -z "${aab}" ]]; then
    step=4
    title="Собрать подписанный release AAB"
    cmd="./gradlew bundleReleaseLoveTest"
  elif [[ -f "${pack_aab}" && -f "${pack_zip}" ]]; then
    step=5
    title="Загрузить AAB в Play Console (Internal testing)"
    cmd="open docs/store/INTERNAL_UPLOAD_NOW.md"
    note="Пакет уже готов: ${pack_aab} · ${pack_zip}
Чеклист: ./scripts/print_store_checklist.sh (блокеров нет)
Дальше: Closed IAP — docs/store/CLOSED_IAP_SMOKE.md"
  else
    step=5
    title="Финальный gate и upload-пакет"
    cmd="./gradlew finalizeStoreReleaseLoveTest"
    note="Загрузите build/store-upload/ или build/love-tester-store-upload.zip
Инструкция: docs/store/INTERNAL_UPLOAD_NOW.md"
  fi
fi

echo "Шаг ${step}/5: ${title}"
echo ""
echo "  ${cmd}"
if [[ -n "${note}" ]]; then
  echo ""
  echo "${note}" | sed 's/^/  /'
fi
echo ""
echo "Полный чеклист: ./scripts/print_store_checklist.sh"
