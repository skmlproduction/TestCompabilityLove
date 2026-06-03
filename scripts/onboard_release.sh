#!/usr/bin/env bash
# Первичная настройка локального окружения перед Play Console.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

USER="${1:-}"
REPO="${2:-}"

echo "=== onboard_release ==="
echo ""

echo "→ store config (gradle.properties, keystore)"
bash scripts/init_store_config.sh
echo ""

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  if [[ -n "${USER}" && -n "${REPO}" ]]; then
    bash scripts/init_git_for_github.sh "${USER}" "${REPO}"
  else
    bash scripts/init_git_for_github.sh
  fi
  echo ""
elif [[ -n "${USER}" && -n "${REPO}" ]] && ! git remote get-url origin >/dev/null 2>&1; then
  bash scripts/setup_github_remote.sh "${USER}" "${REPO}"
  echo ""
fi

if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "→ validate_git_staging"
  bash scripts/validate_git_staging_cached.sh
  echo ""
fi

echo "→ project health"
bash scripts/project_health.sh
