#!/usr/bin/env bash
# One-shot EN store catalog on LOVETEST_ADB_SERIAL (default emulator-5554).
set -eu
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

SERIAL="${LOVETEST_ADB_SERIAL:-emulator-5554}"
export LOVETEST_ADB_SERIAL="${SERIAL}"
export ANDROID_SERIAL="${SERIAL}"
unset LOVETEST_USE_EMULATOR || true
unset LOVETEST_FORCE_EMULATOR || true
unset LOVETEST_SCREENSHOT_OUT_DIR || true

APK="${ROOT}/app/build/outputs/apk/debug/app-debug.apk"
[[ -f "${APK}" ]] || ./gradlew :app:assembleDebug -q
adb -s "${SERIAL}" install -r "${APK}"

IDS_FILE="$(mktemp)"
python3 - <<'PY' >"${IDS_FILE}"
import csv
from pathlib import Path
path = Path("docs/product/screens_catalog.csv")
with path.open(encoding="utf-8") as f:
    ids = [row["screen_id"].strip() for row in csv.DictReader(f) if row.get("screen_id", "").strip()]
print(f"ids={len(ids)}", flush=True)
for sid in ids:
    print(sid)
PY

# Drop the ids=N header line if present
IDS=()
while IFS= read -r line; do
  case "${line}" in
    ids=*) echo "${line}" ;;
    "") ;;
    *) IDS+=("${line}") ;;
  esac
done <"${IDS_FILE}"
rm -f "${IDS_FILE}"
echo "loaded ${#IDS[@]} screens"

OK=0
FAIL=0
for sid in "${IDS[@]}"; do
  # Do not pkill broadly — sibling agents may restart; only ensure our package.
  if ! adb -s "${SERIAL}" shell pm path dev.lovetest.app >/dev/null 2>&1; then
    echo "REINSTALL before ${sid}"
    adb -s "${SERIAL}" install -r "${APK}"
  fi
  echo "=== EN ${sid} ==="
  # </dev/null — иначе am/adb съедают stdin и цикл обрывается после 1 экрана
  if WAIT_SEC=3.5 bash "${ROOT}/scripts/adb_screenshot_preview.sh" "${sid}" en </dev/null; then
    python3 -c "from PIL import Image; im=Image.open('docs/screenshots/en/${sid}.png'); assert im.size==(1080,1920), im.size"
    mkdir -p "${ROOT}/docs/screenshots/qa/emulator/en"
    cp "${ROOT}/docs/screenshots/en/${sid}.png" "${ROOT}/docs/screenshots/qa/emulator/en/${sid}.png"
    OK=$((OK + 1))
  else
    adb -s "${SERIAL}" install -r "${APK}" >/dev/null 2>&1 || true
    if WAIT_SEC=5 bash "${ROOT}/scripts/adb_screenshot_preview.sh" "${sid}" en </dev/null; then
      cp "${ROOT}/docs/screenshots/en/${sid}.png" "${ROOT}/docs/screenshots/qa/emulator/en/${sid}.png"
      OK=$((OK + 1))
      echo "RETRY OK ${sid}"
    else
      FAIL=$((FAIL + 1))
      echo "FAIL ${sid}"
    fi
  fi
  sleep 0.5
done

echo "EN_DONE ok=${OK} fail=${FAIL}"
exit "${FAIL}"
