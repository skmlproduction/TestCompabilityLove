#!/usr/bin/env bash
# Подсказки перед первым commit (не создаёт commit).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== suggest_first_commit ==="
echo ""

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Git не инициализирован — ./scripts/init_git_for_github.sh USER REPO"
  exit 1
fi

if git rev-parse HEAD >/dev/null 2>&1; then
  echo "Commits уже есть — см. ./scripts/prepare_git_push.sh или git push"
  exit 0
fi

PATHS_FILE="$(mktemp "${TMPDIR:-/tmp}/lovetest-staging.XXXXXX")"
trap 'rm -f "${PATHS_FILE}"' EXIT

echo "→ list git staging (dry-run)"
bash scripts/list_git_staging.sh "${PATHS_FILE}"
echo ""

echo "→ validate_git_staging"
bash scripts/validate_git_staging.sh --paths-file "${PATHS_FILE}"
echo ""

TOTAL="$(wc -l < "${PATHS_FILE}" | tr -d ' ')"
echo "Будет добавлено (git add .):"
head -20 "${PATHS_FILE}" | sed "s/^/add '/;s/$/'/"
if (( TOTAL > 20 )); then
  echo "  … и ещё $((TOTAL - 20)) путей"
fi
echo ""
echo "Всего новых путей: ${TOTAL}"

SCREENSHOT_MB="$(du -sm docs/screenshots 2>/dev/null | awk '{print $1}' || echo "?")"
echo "Store PNG: docs/screenshots ~${SCREENSHOT_MB} MB"
echo ""

echo "Рекомендуемые команды:"
echo "  ./scripts/prepare_git_push.sh"
echo "  git add ."
echo "  git commit -m \"Love Tester — store ready\""
if git remote get-url origin >/dev/null 2>&1; then
  echo "  git push -u origin main"
else
  echo "  ./scripts/setup_github_remote.sh USER REPO"
  echo "  git push -u origin main"
fi
