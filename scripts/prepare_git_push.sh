#!/usr/bin/env bash
# Проверка перед первым push на GitHub (commit не создаёт).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== prepare_git_push ==="
echo ""

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Git не инициализирован — ./scripts/init_git_for_github.sh USER REPO"
  exit 1
fi

if ! git rev-parse HEAD >/dev/null 2>&1; then
  echo "Git: нет commits на main — ./scripts/suggest_first_commit.sh (dry-run)"
  echo ""
fi

echo "→ validate_git_staging"
bash scripts/validate_git_staging_cached.sh
echo ""

echo "→ test inventory"
bash scripts/count_tests.sh | sed 's/^/  /'
echo ""
if [[ "${FIRST_PUSH_FAST:-}" == "1" ]]; then
  echo "→ skip verifyLoveTestBeforeStore (FIRST_PUSH_FAST=1)"
else
  echo "→ verifyLoveTestBeforeStore"
  ./gradlew verifyLoveTestBeforeStore -q
fi
echo ""
echo "→ git status"
git status -sb

DEcompile="Love Test - Compatibility Test_3.0.5_apkcombo.com.xapk_Decompiler.com"
if git status --porcelain 2>/dev/null | grep -qF "${DEcompile}"; then
  echo ""
  echo "WARN: decompiled reference в status — проверьте .gitignore"
fi

echo ""
if git remote get-url origin >/dev/null 2>&1; then
  echo "Remote: $(git remote get-url origin)"
else
  echo "Remote: (нет)"
  echo "  ./scripts/setup_github_remote.sh USER REPO"
fi

SCREENSHOT_MB="$(du -sm docs/screenshots 2>/dev/null | awk '{print $1}' || echo "?")"
echo ""
echo "Store assets: docs/screenshots ~${SCREENSHOT_MB} MB (ожидаемо в git)"

STAGED="$(git status --porcelain 2>/dev/null | wc -l | tr -d ' ')"
echo "Изменений в working tree: ${STAGED} файлов"

echo ""
echo "Следующие шаги (вручную):"
echo "  ./scripts/setup_github_remote.sh USER REPO   # если нет remote"
echo "  git add ."
echo "  git commit -m \"Love Tester — store ready\""
echo "  git push -u origin main"
echo "  GitHub → Settings → Pages → GitHub Actions → Privacy GitHub Pages"
echo "  ./scripts/post_privacy_setup.sh https://USER.github.io/REPO/"
echo ""
echo "Проверка: ./scripts/play_console_next.sh"
