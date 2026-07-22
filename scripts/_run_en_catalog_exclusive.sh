#!/bin/bash
# Exclusive LoveTester EN catalog — 34 @1080×1920. Logs: /tmp/lovetest-en-catalog.log
set -eu
export PATH="${HOME}/Library/Android/sdk/emulator:${HOME}/Library/Android/sdk/platform-tools:${PATH:-}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

LOG=/tmp/lovetest-en-catalog.log
STATUS=/tmp/lovetest-en-status.txt
SERIAL=emulator-5554
PORT=5554

: >"${LOG}"
echo "starting $$" >"${STATUS}"
log() { printf '%s\n' "$*" >>"${LOG}"; }

log "=== $(date +%Y-%m-%dT%H:%M:%S) EN exclusive pid=$$ ==="

# Kill real qemu only
python3 - <<'PY' >>"${LOG}" 2>&1
import os, signal, subprocess
out = subprocess.check_output(["ps", "-ax", "-o", "pid=,command="], text=True, errors="replace")
for line in out.splitlines():
    if "qemu-system-aarch64" in line or "qemu-system-x86_64" in line:
        pid = int(line.split(None, 1)[0])
        print(f"kill_qemu {pid}")
        try:
            os.kill(pid, signal.SIGKILL)
        except ProcessLookupError:
            pass
PY
sleep 4
adb kill-server >/dev/null 2>&1 || true
sleep 1
adb start-server >/dev/null 2>&1 || true

log "launch emulator"
nohup emulator -avd LoveTester_Capture -port "${PORT}" -skin 1080x1920 \
  -no-window -no-audio -no-snapshot-load -no-snapshot-save -no-boot-anim \
  -memory 1536 -cores 2 -gpu swiftshader_indirect \
  >/tmp/lovetest-emulator.log 2>&1 &
log "emulator_pid=$!"
echo "booting" >"${STATUS}"

boot=0
i=0
while [ "$i" -lt 100 ]; do
  i=$((i + 1))
  prop="$(adb -s "${SERIAL}" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n' || true)"
  if [ "${prop}" = "1" ]; then
    boot=1
    log "boot_ok iter=${i}"
    break
  fi
  # heartbeat so we can see progress
  if [ $((i % 5)) -eq 0 ]; then
    log "still_booting iter=${i} prop='${prop}'"
  fi
  sleep 3
done

if [ "${boot}" != "1" ]; then
  log "ERROR boot timeout"
  echo "boot_fail" >"${STATUS}"
  exit 3
fi

wm="$(adb -s "${SERIAL}" shell wm size 2>/dev/null | tr -d '\r\n' || true)"
log "wm=${wm}"
case "${wm}" in
  *1080x1920*) ;;
  *)
    log "ERROR bad wm"
    echo "bad_wm" >"${STATUS}"
    exit 4
    ;;
esac

unset LOVETEST_FORCE_EMULATOR || true
unset LOVETEST_USE_EMULATOR || true
unset LOVETEST_SCREENSHOT_OUT_DIR || true
export LOVETEST_ADB_SERIAL="${SERIAL}"
export ANDROID_SERIAL="${SERIAL}"

echo "capturing" >"${STATUS}"
log "capture start"
set +e
bash "${ROOT}/scripts/capture_screenshot_catalog.sh" en </dev/null >>"${LOG}" 2>&1
CAT_EC=$?
set -e
log "catalog_exit=${CAT_EC}"

mkdir -p "${ROOT}/docs/screenshots/qa/emulator/en"
python3 >>"${LOG}" 2>&1 <<'PY'
import csv, shutil, sys
from pathlib import Path
from PIL import Image
root = Path(".")
ok = bad = 0
with (root / "docs/product/screens_catalog.csv").open(encoding="utf-8", newline="") as f:
    ids = [(r.get("screen_id") or "").strip() for r in csv.DictReader(f)]
ids = [s for s in ids if s]
out = root / "docs/screenshots/qa/emulator/en"
out.mkdir(parents=True, exist_ok=True)
for sid in ids:
    src = root / "docs/screenshots/en" / f"{sid}.png"
    if not src.exists():
        print("MISSING", sid)
        bad += 1
        continue
    if Image.open(src).size != (1080, 1920):
        print("BAD_SIZE", sid)
        bad += 1
        continue
    shutil.copy2(src, out / f"{sid}.png")
    ok += 1
print(f"EN_DONE ok={ok} bad={bad}")
Path("/tmp/lovetest-en-status.txt").write_text(f"done ok={ok} bad={bad}\n")
sys.exit(bad)
PY
EC=$?
log "verify_exit=${EC}"
exit "${EC}"
