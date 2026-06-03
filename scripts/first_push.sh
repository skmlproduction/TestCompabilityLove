#!/usr/bin/env bash
# Pipeline до первого push: remote + staging + gate (без commit/push).
# Usage: ./scripts/first_push.sh GITHUB_USER REPO [--fast]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

USER="${1:-}"
REPO="${2:-}"
FAST=false
if [[ "${3:-}" == "--fast" ]]; then
  FAST=true
fi

echo "=== first_push ==="
echo ""

if [[ -z "${USER}" || -z "${REPO}" ]]; then
  echo "Usage: $0 GITHUB_USER REPO [--fast]" >&2
  echo "  $0 myuser TestCompabilityLove" >&2
  echo "  $0 myuser TestCompabilityLove --fast   # без verifyLoveTestBeforeStore" >&2
  exit 1
fi

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  bash scripts/init_git_for_github.sh "${USER}" "${REPO}"
  echo ""
fi

if ! git remote get-url origin >/dev/null 2>&1; then
  bash scripts/setup_github_remote.sh "${USER}" "${REPO}"
  echo ""
else
  echo "Remote: $(git remote get-url origin)"
  echo ""
fi

bash scripts/suggest_first_commit.sh
echo ""
if [[ "${FAST}" == true ]]; then
  echo "→ prepare_git_push (fast: без verifyLoveTestBeforeStore)"
  FIRST_PUSH_FAST=1 bash scripts/prepare_git_push.sh
else
  bash scripts/prepare_git_push.sh
fi

PRIVACY_URL="https://${USER}.github.io/${REPO}/"
echo ""
echo "=== После push ==="
echo "  git add . && git commit -m \"Love Tester — store ready\""
echo "  git push -u origin main"
echo "  ./scripts/post_push.sh                    # Pages → privacy → keystore → upload"
echo ""
echo "Privacy URL (после Pages): ${PRIVACY_URL}"
