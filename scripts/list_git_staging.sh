#!/usr/bin/env bash
# Один раз git add --dry-run → файл путей (reuse в validate/suggest).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

OUT="${1:-}"

if [[ -z "${OUT}" ]]; then
  git add --dry-run . 2>/dev/null | sed -n "s/^add '//;s/'$//p"
  exit 0
fi

git add --dry-run . 2>/dev/null | sed -n "s/^add '//;s/'$//p" > "${OUT}"
