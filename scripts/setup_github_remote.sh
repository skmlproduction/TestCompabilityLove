#!/usr/bin/env bash
# Добавить GitHub remote и показать privacy URL + push-команды.
# Usage: ./scripts/setup_github_remote.sh GITHUB_USER REPO_NAME
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

USER="${1:-}"
REPO="${2:-}"

if [[ -z "${USER}" || -z "${REPO}" ]]; then
  echo "Usage: $0 GITHUB_USER REPO_NAME" >&2
  echo "  $0 myuser TestCompabilityLove" >&2
  exit 1
fi

echo "=== setup_github_remote ==="

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  bash scripts/init_git_for_github.sh "${USER}" "${REPO}"
fi

if git remote get-url origin >/dev/null 2>&1; then
  echo "Remote origin уже есть: $(git remote get-url origin)"
else
  git remote add origin "git@github.com:${USER}/${REPO}.git"
  echo "OK: origin → git@github.com:${USER}/${REPO}.git"
fi

echo ""
bash scripts/suggest_privacy_url.sh "${USER}" "${REPO}"

echo ""
echo "Дальше:"
echo "  ./scripts/prepare_git_push.sh"
echo "  git add . && git commit -m \"Love Tester — store ready\""
echo "  git push -u origin main"
echo "  ./scripts/post_privacy_setup.sh https://${USER}.github.io/${REPO}/"
