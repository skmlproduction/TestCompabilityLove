#!/usr/bin/env bash
# validate_git_staging с одним git add --dry-run.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

PATHS_FILE="$(mktemp "${TMPDIR:-/tmp}/lovetest-staging.XXXXXX")"
trap 'rm -f "${PATHS_FILE}"' EXIT

bash scripts/list_git_staging.sh "${PATHS_FILE}"
bash scripts/validate_git_staging.sh --paths-file "${PATHS_FILE}"
