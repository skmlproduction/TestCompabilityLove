#!/usr/bin/env bash
# Проверка доступности privacy URL (HTTPS, HTTP 2xx/3xx).
# Usage: ./scripts/check_privacy_url.sh [URL]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

URL="${1:-}"
if [[ -z "${URL}" && -f gradle.properties ]]; then
  URL="$(grep -E '^lovetest\.privacy\.policy\.url=' gradle.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
fi

echo "=== check_privacy_url ==="

if [[ -z "${URL}" ]]; then
  echo "ERROR: URL не задан — ./scripts/set_privacy_url.sh https://…" >&2
  exit 1
fi
if [[ "${URL}" == https://example.com/privacy ]]; then
  echo "WARN: placeholder example.com — укажите реальный URL" >&2
  exit 1
fi
if [[ "${URL}" != https://* ]]; then
  echo "ERROR: Play требует HTTPS" >&2
  exit 1
fi

if ! command -v curl >/dev/null 2>&1; then
  echo "WARN: curl не найден — проверьте URL вручную: ${URL}"
  exit 0
fi

code="$(curl -sS -o /dev/null -w '%{http_code}' -L --max-time 20 "${URL}" || echo "000")"
if [[ "${code}" =~ ^[23] ]]; then
  echo "OK: ${URL} → HTTP ${code}"
  exit 0
fi

echo "FAIL: ${URL} → HTTP ${code}" >&2
echo "Подсказка: GitHub Pages — docs/store/PRIVACY_HOSTING.md" >&2
exit 1
