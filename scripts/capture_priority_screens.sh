#!/usr/bin/env bash
# Быстрая съёмка приоритетных экранов для листинга Play (см. CAPTURE_CHECKLIST.md).
# Usage: ./scripts/capture_priority_screens.sh [ru|en]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOCALE="${1:-ru}"

PRIORITY=(
  hub_main
  love_test_input
  love_test_result
  protocol_input
  protocol_result
  wheel_spin
  premium_paywall
)

echo "=== capture_priority_screens (${LOCALE}) ==="
echo "Экраны: ${PRIORITY[*]}"
bash "${ROOT}/scripts/capture_screenshot_catalog.sh" "${LOCALE}" "${PRIORITY[@]}"
