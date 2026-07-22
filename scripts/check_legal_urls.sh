#!/usr/bin/env bash
# Проверка privacy + terms + data-collection на HTTPS (GitHub Pages).
# Usage: ./scripts/check_legal_urls.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

BASE=""
if [[ -f gradle.properties ]]; then
  BASE="$(grep -E '^lovetest\.privacy\.policy\.url=' gradle.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
fi
BASE="${BASE%/}"

if [[ -z "${BASE}" ]]; then
  echo "ERROR: lovetest.privacy.policy.url не задан" >&2
  exit 1
fi

echo "=== check_legal_urls (base=${BASE}) ==="

check_one() {
  local url="$1"
  local label="$2"
  if ! command -v curl >/dev/null 2>&1; then
    echo "WARN: curl missing — open manually: ${url}"
    return 0
  fi
  local code
  code="$(curl -sS -o /dev/null -w '%{http_code}' -L --max-time 25 "${url}" || echo "000")"
  if [[ "${code}" =~ ^[23] ]]; then
    echo "  [OK]   ${label} → HTTP ${code}"
  else
    echo "  [FAIL] ${label} → HTTP ${code} (${url})" >&2
    return 1
  fi
}

FAIL=0
check_one "${BASE}/" "privacy" || FAIL=1
check_one "${BASE}/terms.html" "terms" || FAIL=1
check_one "${BASE}/data-collection.html" "data-collection" || FAIL=1

if [[ "${FAIL}" -ne 0 ]]; then
  echo "Подсказка: git push → GitHub Actions «Privacy GitHub Pages»" >&2
  echo "См. docs/store/GITHUB_FIRST_PUSH.md" >&2
  exit 1
fi
echo "check_legal_urls: OK"
