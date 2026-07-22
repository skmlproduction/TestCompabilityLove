#!/usr/bin/env bash
# Проверка «готов к съёмке / сборке release» без placeholder-gate.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== store_ready ==="
bash scripts/preflight_store.sh
./gradlew verifyLoveTest -q
echo ""
echo "OK: код и инвентарь. Далее на машине с эмулятором:"
echo "  ./gradlew captureScreenshotCatalogRu"
echo "  ./gradlew captureScreenshotCatalogEn"
echo "  ./gradlew verifyLoveTestBeforeStore"
echo "  ./gradlew bundleRelease"
