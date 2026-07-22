#!/usr/bin/env bash
# Быстрая диагностика: GitHub repo + Pages legal URLs.
# Usage: ./scripts/check_github_repo.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

USER_REPO="skmlproduction/TestCompabilityLove"
PAGES="https://skmlproduction.github.io/TestCompabilityLove"

echo "=== check_github_repo ==="
repo_code="$(curl -sS -o /dev/null -w '%{http_code}' -L --max-time 20 "https://github.com/${USER_REPO}" || echo 000)"
pages_code="$(curl -sS -o /dev/null -w '%{http_code}' -L --max-time 20 "${PAGES}/" || echo 000)"
echo "  github.com/${USER_REPO} → HTTP ${repo_code}"
echo "  Pages ${PAGES}/ → HTTP ${pages_code}"

if [[ "${repo_code}" == "404" ]]; then
  echo ""
  echo "Repo ещё нет (или private без доступа)."
  echo "Сделайте в Terminal:"
  echo "  1) https://github.com/new → Public → name TestCompabilityLove (без README)"
  echo "  2) ssh-add --apple-use-keychain ~/.ssh/id_ed25519   # или gh auth login"
  echo "  3) cd ${ROOT} && git push -u origin main"
  echo "  4) Actions → Privacy GitHub Pages → ./scripts/check_legal_urls.sh"
  echo "См. docs/store/GITHUB_FIRST_PUSH.md"
  exit 2
fi

if [[ ! "${pages_code}" =~ ^[23] ]]; then
  echo ""
  echo "Repo есть, но Pages ещё не 200 — дождитесь Actions или workflow_dispatch."
  echo "  Settings → Pages → Source: GitHub Actions"
  echo "  Actions → Privacy GitHub Pages → Run workflow"
  echo "  ./scripts/check_legal_urls.sh"
  exit 1
fi

echo "check_github_repo: OK"
exit 0
