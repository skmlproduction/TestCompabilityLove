#!/usr/bin/env bash
# Подсказка URL privacy policy для GitHub Pages.
# Usage:
#   ./scripts/suggest_privacy_url.sh
#   ./scripts/suggest_privacy_url.sh USER REPO
#   GITHUB_PAGES_URL=https://user.github.io/repo/ ./scripts/suggest_privacy_url.sh --apply
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

APPLY=false
USER=""
REPO=""

if [[ "${1:-}" == "--apply" ]]; then
  APPLY=true
elif [[ $# -ge 2 ]]; then
  USER="$1"
  REPO="$2"
  [[ "${3:-}" == "--apply" ]] && APPLY=true
fi

URL="${GITHUB_PAGES_URL:-}"

if [[ -z "${URL}" && -n "${USER}" && -n "${REPO}" ]]; then
  URL="https://${USER}.github.io/${REPO}/"
fi

if [[ -z "${URL}" ]] && git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  remote="$(git remote get-url origin 2>/dev/null || true)"
  if [[ "${remote}" =~ github\.com[:/]([^/]+)/([^/.]+) ]]; then
    USER="${BASH_REMATCH[1]}"
    REPO="${BASH_REMATCH[2]%.git}"
    URL="https://${USER}.github.io/${REPO}/"
  fi
fi

if [[ -z "${URL}" ]]; then
  echo "Usage: $0 [GITHUB_USER REPO_NAME] [--apply]" >&2
  echo "   or: GITHUB_PAGES_URL=https://user.github.io/repo/ $0 --apply" >&2
  exit 1
fi

[[ "${URL}" != */ ]] && URL="${URL}/"

echo "=== suggest_privacy_url ==="
echo "Suggested: ${URL}"
echo ""
echo "GitHub Pages:"
echo "  1. Settings → Pages → Source: GitHub Actions"
echo "  2. Run workflow: Privacy GitHub Pages"
echo "  3. ./scripts/set_privacy_url.sh ${URL}"
echo ""
echo "Проверка после деплоя:"
echo "  curl -I ${URL}"

if [[ "${APPLY}" == true ]]; then
  echo ""
  bash "${ROOT}/scripts/set_privacy_url.sh" "${URL}"
fi
