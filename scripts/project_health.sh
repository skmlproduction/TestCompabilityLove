#!/usr/bin/env bash
# Быстрая сводка здоровья проекта (без полной Gradle-сборки).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

echo "=== project_health ==="
echo ""

echo "→ tests"
bash scripts/count_tests.sh | sed 's/^/  /'
bash scripts/verify_test_inventory.sh | sed 's/^/  /'
echo ""
echo "→ ads build (optional smoke)"
echo "  ./gradlew verifyAdsBuildLoveTest"
echo ""

echo "→ Play Console (кратко)"
bash scripts/print_store_checklist.sh | tail -n +3 | head -n 12
echo ""

echo "→ следующий шаг"
bash scripts/play_console_next.sh
