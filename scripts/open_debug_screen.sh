#!/usr/bin/env bash
# Открыть экран по screen_id на подключённом устройстве.
# Usage: ./scripts/open_debug_screen.sh hub_main
set -euo pipefail

SCREEN_ID="${1:?screen_id required}"
PKG="dev.lovetest.app"
ACTIVITY="${PKG}/.debug.DebugUiPreviewActivity"
EXTRA="lovetest.intent.extra.DEBUG_UI_PREVIEW"

if ! command -v adb >/dev/null 2>&1; then
  echo "adb not found" >&2
  exit 1
fi

adb shell am force-stop "${PKG}" >/dev/null 2>&1 || true
adb shell am start -n "${ACTIVITY}" --es "${EXTRA}" "${SCREEN_ID}" -a android.intent.action.MAIN
echo "open_debug_screen: ${SCREEN_ID}"
