#!/usr/bin/env bash
# Проверка git add --dry-run: нет build/, secrets, decompiled ref.
# Usage: validate_git_staging.sh [--paths-file FILE]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "ERROR: git не инициализирован" >&2
  exit 1
fi

PATHS_FILE=""
if [[ "${1:-}" == "--paths-file" ]]; then
  PATHS_FILE="${2:-}"
  if [[ -z "${PATHS_FILE}" || ! -f "${PATHS_FILE}" ]]; then
    echo "ERROR: --paths-file requires existing file" >&2
    exit 1
  fi
fi

TOTAL=0
fail=false

read_paths() {
  if [[ -n "${PATHS_FILE}" ]]; then
    cat "${PATHS_FILE}"
  else
    bash scripts/list_git_staging.sh
  fi
}

while IFS= read -r path; do
  [[ -z "${path}" ]] && continue
  TOTAL=$((TOTAL + 1))

  if [[ "${path}" == *"/build/"* ]] || [[ "${path}" == "build/"* ]]; then
    echo "ERROR: build artifact в staging: ${path}" >&2
    fail=true
  fi
  case "${path}" in
    keystore.properties|*.jks|*.keystore|local.properties)
      echo "ERROR: secret/local file в staging: ${path}" >&2
      fail=true
      ;;
  esac
  if [[ "${path}" == *Decompiler.com* ]]; then
    echo "ERROR: decompiled reference в staging: ${path}" >&2
    fail=true
  fi
done < <(read_paths)

if [[ "${fail}" == true ]]; then
  echo "validate_git_staging: FAIL — исправьте .gitignore" >&2
  exit 1
fi

echo "validate_git_staging: OK (${TOTAL} paths, no build/secrets/decompile)"
