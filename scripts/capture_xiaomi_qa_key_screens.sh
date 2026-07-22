#!/usr/bin/env bash
# Capture 5 key Xiaomi QA screens without touching system settings (wm/theme/fonts).
# Waits for secure keyguard to be dismissed manually, then uses DebugUiPreview.
# Usage:
#   LOVETEST_ADB_SERIAL=PF99SSNFFQQ4XCF6 ./scripts/capture_xiaomi_qa_key_screens.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

PHONE="${LOVETEST_ADB_SERIAL:-PF99SSNFFQQ4XCF6}"
export LOVETEST_ADB_SERIAL="${PHONE}"
unset LOVETEST_USE_EMULATOR || true
export LOVETEST_SCREENSHOT_OUT_DIR="${LOVETEST_SCREENSHOT_OUT_DIR:-${ROOT}/docs/screenshots/qa/xiaomi/ru}"

SCREENS=(splash_brand hub_main love_test_input protocol_input premium_paywall)
WAIT_UNLOCK_SEC="${WAIT_UNLOCK_SEC:-180}"
POLL_SEC="${POLL_SEC:-3}"

if ! adb -s "${PHONE}" get-state >/dev/null 2>&1; then
  echo "capture_xiaomi_qa_key_screens: device ${PHONE} not connected" >&2
  exit 1
fi

keyguard_up() {
  adb -s "${PHONE}" shell dumpsys window 2>/dev/null \
    | grep -q 'isKeyguardShowing=true'
}

echo "capture_xiaomi_qa_key_screens: waiting up to ${WAIT_UNLOCK_SEC}s for manual unlock on ${PHONE}"
echo "  (no system settings will be changed; only app locale + screencap)"
elapsed=0
while keyguard_up; do
  if [[ "${elapsed}" -ge "${WAIT_UNLOCK_SEC}" ]]; then
    echo "capture_xiaomi_qa_key_screens: still locked after ${WAIT_UNLOCK_SEC}s — unlock phone and re-run" >&2
    exit 2
  fi
  # Soft wake only — does not change settings or bypass secure lock.
  adb -s "${PHONE}" shell input keyevent KEYCODE_WAKEUP >/dev/null 2>&1 || true
  sleep "${POLL_SEC}"
  elapsed=$((elapsed + POLL_SEC))
done

echo "capture_xiaomi_qa_key_screens: unlocked — capturing ${#SCREENS[@]} screens → ${LOVETEST_SCREENSHOT_OUT_DIR}"
fail=0
for sid in "${SCREENS[@]}"; do
  if ! bash "${ROOT}/scripts/adb_screenshot_preview.sh" "${sid}" ru; then
    echo "FAIL: ${sid}" >&2
    fail=1
  fi
done

if [[ "${fail}" -ne 0 ]]; then
  exit 1
fi
echo "capture_xiaomi_qa_key_screens: OK (${#SCREENS[@]} / ${#SCREENS[@]})"
