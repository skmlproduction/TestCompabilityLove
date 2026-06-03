#!/usr/bin/env bash
# Локальный превью privacy HTML (не заменяет HTTPS для Play).
# Usage: ./scripts/serve_privacy_preview.sh [port]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

PORT="${1:-8765}"
HOST_DIR="${ROOT}/build/legal-host"

if [[ ! -f "${HOST_DIR}/index.html" ]]; then
  bash scripts/export_privacy_for_hosting.sh
fi

echo "=== serve_privacy_preview ==="
echo "  http://127.0.0.1:${PORT}/"
echo "  Ctrl+C для остановки"
echo ""
echo "Play Console требует публичный HTTPS — см. docs/store/PRIVACY_HOSTING.md"
echo ""

cd "${HOST_DIR}"
exec python3 -m http.server "${PORT}" --bind 127.0.0.1
