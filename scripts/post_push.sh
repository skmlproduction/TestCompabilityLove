#!/usr/bin/env bash
# Шаги после git push: Pages, privacy, keystore, upload.
# Usage: ./scripts/post_push.sh [PRIVACY_URL]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== post_push ==="
echo ""

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "ERROR: git не инициализирован" >&2
  exit 1
fi

if ! git rev-parse HEAD >/dev/null 2>&1; then
  echo "ERROR: нет commits — сначала git add . && git commit" >&2
  exit 1
fi

if git remote get-url origin >/dev/null 2>&1; then
  echo "Remote: $(git remote get-url origin)"
else
  echo "WARN: нет remote origin"
fi

if git rev-parse --abbrev-ref '@{u}' >/dev/null 2>&1; then
  ahead="$(git rev-list --count '@{u}..HEAD' 2>/dev/null || echo 0)"
  if [[ "${ahead}" -gt 0 ]]; then
    echo "WARN: ${ahead} commit(s) не запушены — git push -u origin main"
    echo ""
  else
    echo "Push: upstream синхронизирован"
    echo ""
  fi
else
  echo "WARN: нет upstream — git push -u origin main"
  echo ""
fi

PRIVACY_URL="${1:-}"
if [[ -z "${PRIVACY_URL}" ]]; then
  if git remote get-url origin >/dev/null 2>&1; then
    remote="$(git remote get-url origin)"
    if [[ "${remote}" =~ github\.com[:/]([^/]+)/([^/.]+) ]]; then
      user="${BASH_REMATCH[1]}"
      repo="${BASH_REMATCH[2]%.git}"
      PRIVACY_URL="https://${user}.github.io/${repo}/"
    fi
  fi
fi

echo "=== Следующие шаги ==="
echo ""
echo "1. GitHub → Settings → Pages → GitHub Actions"
echo "   Run workflow «Privacy GitHub Pages»"
echo ""
if [[ -n "${PRIVACY_URL}" ]]; then
  echo "2. После деплоя Pages:"
  echo "   ./scripts/post_privacy_setup.sh ${PRIVACY_URL}"
else
  echo "2. ./scripts/post_privacy_setup.sh https://USER.github.io/REPO/"
fi
echo ""
echo "3. Production keystore:"
echo "   LOVETEST_KEYSTORE_PASS='***' ./scripts/generate_upload_keystore.sh"
echo ""
echo "4. Release:"
echo "   ./gradlew bundleReleaseLoveTest"
echo "   ./gradlew finalizeStoreReleaseLoveTest"
echo ""
echo "5. Upload build/store-upload/ — docs/store/INTERNAL_TESTING.md"
echo ""

if [[ -n "${PRIVACY_URL}" ]]; then
  echo "Privacy URL (ожидаемый): ${PRIVACY_URL}"
  if bash scripts/check_privacy_url.sh "${PRIVACY_URL}" >/dev/null 2>&1; then
    echo "Privacy URL: reachable ✅"
  else
    echo "Privacy URL: пока недоступен (нормально до деплоя Pages)"
  fi
  echo ""
fi

bash scripts/play_console_next.sh
