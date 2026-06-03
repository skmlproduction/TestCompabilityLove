#!/usr/bin/env bash
# Снимает один экран по screen_id из screens_catalog.csv.
# Usage: ./scripts/adb_screenshot_preview.sh <screen_id> [ru|en]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"
SCREEN_ID="${1:?screen_id required}"
LOCALE="${2:-ru}"
PKG="dev.lovetest.app"
ACTIVITY="${PKG}/.debug.DebugUiPreviewActivity"
EXTRA_PREVIEW="lovetest.intent.extra.DEBUG_UI_PREVIEW"
DEFAULT_WAIT=2.5
case "${SCREEN_ID}" in
  hub_loading|error_network) DEFAULT_WAIT=1.2 ;;
  love_test_calculating|wheel_spin|protocol_calculating) DEFAULT_WAIT=1.0 ;;
  splash_brand) DEFAULT_WAIT=3.0 ;;
  share_result_card|ad_interstitial_placeholder|premium_thank_you) DEFAULT_WAIT=3.0 ;;
  consent_ads_gdpr|premium_paywall) DEFAULT_WAIT=2.0 ;;
  letters_input|letters_result) DEFAULT_WAIT=2.0 ;;
  onboarding_protocol|protocol_input|protocol_result|protocol_result_low) DEFAULT_WAIT=2.5 ;;
esac
WAIT_SEC="${WAIT_SEC:-$DEFAULT_WAIT}"

if ! command -v adb >/dev/null 2>&1; then
  echo "adb not found — ./scripts/setup_android_sdk.sh или source scripts/android_sdk_env.sh" >&2
  exit 1
fi

if ! adb get-state >/dev/null 2>&1; then
  echo "No adb device connected" >&2
  exit 1
fi

REL_PATH="$(python3 - "$ROOT" "$SCREEN_ID" "$LOCALE" <<'PY'
import csv, sys
from pathlib import Path
root, sid, loc = sys.argv[1:4]
key = "screenshot_ru_relative" if loc == "ru" else "screenshot_en_relative"
with open(Path(root) / "docs/product/screens_catalog.csv", encoding="utf-8") as f:
    for row in csv.DictReader(f):
        if row["screen_id"] == sid:
            print(row[key])
            break
    else:
        raise SystemExit(f"unknown screen_id: {sid}")
PY
)"

OUT="${ROOT}/${REL_PATH}"
mkdir -p "$(dirname "$OUT")"

if [[ "$LOCALE" == "en" ]]; then
  adb shell cmd locale set-app-locales "${PKG}" --locales en-US 2>/dev/null || true
else
  adb shell cmd locale set-app-locales "${PKG}" --locales ru-RU 2>/dev/null || true
fi

adb shell am force-stop "${PKG}" >/dev/null 2>&1 || true
adb shell am start -n "${ACTIVITY}" \
  --es "${EXTRA_PREVIEW}" "${SCREEN_ID}" \
  -W >/dev/null

sleep "${WAIT_SEC}"
adb exec-out screencap -p > "${OUT}"
echo "adb_screenshot_preview: ${SCREEN_ID} (${LOCALE}) -> ${REL_PATH} ($(wc -c <"${OUT}" | tr -d ' ') bytes)"
