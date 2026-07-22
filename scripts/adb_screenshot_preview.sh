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

if ! lovetest_adb get-state >/dev/null 2>&1; then
  echo "No adb device connected (LOVETEST_USE_EMULATOR=1 для эмулятора)" >&2
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

# Optional override: write under a directory (e.g. docs/screenshots/qa/xiaomi/ru)
# without overwriting store catalog docs/screenshots/{ru,en}/.
if [[ -n "${LOVETEST_SCREENSHOT_OUT_DIR:-}" ]]; then
  OUT="${LOVETEST_SCREENSHOT_OUT_DIR%/}/${SCREEN_ID}.png"
  REL_PATH="${OUT#"${ROOT}/"}"
else
  OUT="${ROOT}/${REL_PATH}"
fi
mkdir -p "$(dirname "$OUT")"

if [[ "$LOCALE" == "en" ]]; then
  lovetest_adb shell cmd locale set-app-locales "${PKG}" --locales en-US 2>/dev/null || true
  sleep 0.8
else
  lovetest_adb shell cmd locale set-app-locales "${PKG}" --locales ru-RU 2>/dev/null || true
fi

MIN_BYTES=28000
MAX_ATTEMPTS=3
focus_is_app() {
  lovetest_adb shell dumpsys window 2>/dev/null \
    | grep -E 'mCurrentFocus|mFocusedApp' \
    | grep -q "${PKG}"
}

attempt=1
while [[ "${attempt}" -le "${MAX_ATTEMPTS}" ]]; do
  # Avoid Play Store / other overlays stealing the screencap (no system settings).
  lovetest_adb shell am force-stop com.android.vending >/dev/null 2>&1 || true
  # Soft-stop known focus stealers on hitchhike AVDs (no kill-all).
  lovetest_adb shell am force-stop com.hyperlockscreen.app >/dev/null 2>&1 || true
  lovetest_adb shell am force-stop "${PKG}" >/dev/null 2>&1 || true
  lovetest_adb shell am start -n "${ACTIVITY}" \
    --es "${EXTRA_PREVIEW}" "${SCREEN_ID}" \
    -W >/dev/null

  sleep "${WAIT_SEC}"
  # Dismiss System UI ANR only when the dialog is actually present.
  # Blind taps land on Hub CTAs and navigate away (wrong-route frames).
  if lovetest_adb shell dumpsys window 2>/dev/null | grep -qiE 'Application Not Responding|isn.t responding|System UI'; then
    lovetest_adb shell input tap 540 1180 >/dev/null 2>&1 || true
    sleep 0.35
    lovetest_adb shell input tap 540 1260 >/dev/null 2>&1 || true
    sleep 0.25
  fi
  if ! focus_is_app; then
    echo "WARN: ${SCREEN_ID} focus not on ${PKG}, retry ${attempt}/${MAX_ATTEMPTS}" >&2
    WAIT_SEC="$(python3 - <<PY
print(round(${WAIT_SEC} + 1.5, 2))
PY
)"
    attempt=$((attempt + 1))
    sleep 1
    continue
  fi
  lovetest_adb exec-out screencap -p > "${OUT}"
  size="$(wc -c <"${OUT}" | tr -d ' ')"
  # Reject near-black / System UI ANR overlay frames.
  if [[ "${size}" -ge "${MIN_BYTES}" ]] && python3 - "${OUT}" <<'PY'
import struct, sys
from pathlib import Path
p = Path(sys.argv[1])
data = p.read_bytes()
if len(data) < 24 or data[:8] != b"\x89PNG\r\n\x1a\n":
    raise SystemExit(1)
w, h = struct.unpack(">II", data[16:24])
try:
    from PIL import Image
except ImportError:
    raise SystemExit(0)
im = Image.open(p).convert("RGB")
pts = [(w // 2, h // 2), (w // 4, h // 4), (3 * w // 4, 3 * h // 4), (w // 2, h // 5)]
bright = sum(1 for x, y in pts if sum(im.getpixel((x, y))) > 90)
if bright < 2:
    raise SystemExit(1)
# Flat cool-grey center ≈ System UI ANR dialog on 1080×1920.
cx, cy = w // 2, h // 2
samples = [
    im.getpixel((cx + dx, cy + dy))
    for dx in range(-90, 91, 30)
    for dy in range(-70, 71, 28)
]
n = len(samples)
avg = tuple(sum(c[i] for c in samples) / n for i in range(3))
var = sum((c[0] - avg[0]) ** 2 + (c[1] - avg[1]) ** 2 + (c[2] - avg[2]) ** 2 for c in samples) / n
cool = abs(avg[0] - avg[1]) < 6 and abs(avg[1] - avg[2]) < 6
if cool and 225 <= avg[0] <= 252 and var < 120:
    raise SystemExit(1)
raise SystemExit(0)
PY
  then
    echo "adb_screenshot_preview: ${SCREEN_ID} (${LOCALE}) -> ${REL_PATH} (${size} bytes)"
    exit 0
  fi
  if [[ "${size}" -ge "${MIN_BYTES}" ]]; then
    echo "WARN: ${SCREEN_ID} frame looks blank/ANR/lockscreen (${size}b), retry ${attempt}/${MAX_ATTEMPTS}" >&2
  else
    echo "WARN: ${SCREEN_ID} frame too small (${size}b), retry ${attempt}/${MAX_ATTEMPTS}" >&2
  fi
  WAIT_SEC="$(python3 - <<PY
print(round(${WAIT_SEC} + 1.5, 2))
PY
)"
  attempt=$((attempt + 1))
  sleep 1
done

echo "ERROR: ${SCREEN_ID} capture failed after ${MAX_ATTEMPTS} attempts (last ${size:-0}b)" >&2
exit 1
