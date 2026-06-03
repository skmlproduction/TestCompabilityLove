#!/usr/bin/env bash
# Инициализация git для GitHub Pages (privacy) и CI.
# Не создаёт commit — только git init + подсказки.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== init_git_for_github ==="

if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Git: уже инициализирован"
else
  git init -b main
  echo "Git: init → branch main"
fi

if git remote get-url origin >/dev/null 2>&1; then
  echo "Remote origin: $(git remote get-url origin)"
else
  echo ""
  echo "Добавьте remote (замените USER/REPO):"
  echo "  git remote add origin git@github.com:USER/REPO.git"
  echo "  # или: git remote add origin https://github.com/USER/REPO.git"
fi

echo ""
echo "Privacy URL (после push + Pages):"
if [[ $# -ge 2 ]]; then
  bash scripts/suggest_privacy_url.sh "$1" "$2"
else
  bash scripts/suggest_privacy_url.sh 2>/dev/null || true
fi

echo ""
echo "Следующие шаги:"
echo "  1. git add . && git commit -m \"Love Tester — store ready\""
echo "  2. git push -u origin main"
echo "  3. GitHub → Settings → Pages → Source: GitHub Actions"
echo "  4. Actions → Privacy GitHub Pages → Run workflow"
echo "  5. ./scripts/post_privacy_setup.sh https://${USER:-USER}.github.io/${REPO:-REPO}/"
echo ""
echo "Проверка: ./scripts/print_store_checklist.sh"
