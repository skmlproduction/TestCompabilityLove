#!/usr/bin/env bash
# Полный локальный pipeline: AVD → эмулятор → съёмка → статистика.
# Usage: ./scripts/capture_store_local.sh [ru|en|both] [priority|full]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

MODE="${1:-both}"
SCOPE="${2:-priority}"

echo "=== capture_store_local mode=${MODE} scope=${SCOPE} ==="

bash "${ROOT}/scripts/start_capture_emulator.sh"

capture_locale() {
  local loc="$1"
  if [[ "${SCOPE}" == "full" ]]; then
    bash "${ROOT}/scripts/capture_screenshot_catalog.sh" "${loc}"
  else
    bash "${ROOT}/scripts/capture_priority_screens.sh" "${loc}"
  fi
}

case "${MODE}" in
  ru) capture_locale ru ;;
  en) capture_locale en ;;
  both)
    capture_locale ru
    capture_locale en
    ;;
  *)
    echo "Usage: $0 [ru|en|both] [priority|full]" >&2
    exit 1
    ;;
esac

bash "${ROOT}/scripts/capture_readiness.sh"
