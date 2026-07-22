#!/bin/bash
# Boot exclusive LoveTester + connectedDebugAndroidTest.
# Log: /tmp/lt-connected.log  Status: /tmp/lt-connected-status.txt
set -eu
export PATH="${HOME}/Library/Android/sdk/emulator:${HOME}/Library/Android/sdk/platform-tools:${PATH:-}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

LOG=/tmp/lt-connected.log
STATUS=/tmp/lt-connected-status.txt
SERIAL=emulator-5554
WATCHDOG_PID_FILE=/tmp/lt-qemu-watchdog.pid
: >"${LOG}"
echo "starting" >"${STATUS}"
log() { printf '%s\n' "$*" >>"${LOG}"; }
echo "$$ $(date +%Y-%m-%dT%H:%M:%S)" > /tmp/lt-emulator.lock

log "=== $(date +%Y-%m-%dT%H:%M:%S) connected suite pid=$$ ==="

kill_foreign_qemu() {
  python3 - <<'PY'
import os, signal, subprocess
out = subprocess.check_output(["ps", "-ax", "-o", "pid=,command="], text=True, errors="replace")
for line in out.splitlines():
    parts = line.split(None, 1)
    if len(parts) < 2:
        continue
    pid_s, cmd = parts
    # Real emulator binary only — never Cursor sandbox zsh wrappers.
    if "/qemu-system-" not in cmd and not cmd.lstrip().startswith("qemu-system-"):
        continue
    if "/bin/zsh" in cmd or "snap=$(command cat" in cmd:
        continue
    # Keep LoveTester if already ours on 5554
    if "LoveTester_Capture" in cmd and "-port 5554" in cmd:
        continue
    pid = int(pid_s)
    print(f"kill_foreign {pid} {cmd[:140]}")
    try:
        os.kill(pid, signal.SIGKILL)
    except ProcessLookupError:
        pass
PY
}

kill_foreign_qemu >>"${LOG}" 2>&1 || true
sleep 4
adb kill-server >/dev/null 2>&1 || true
sleep 1
adb start-server >/dev/null 2>&1 || true

log "launch emulator"
nohup emulator -avd LoveTester_Capture -port 5554 -skin 1080x1920 \
  -no-window -no-audio -no-snapshot-load -no-snapshot-save -no-boot-anim \
  -memory 1536 -cores 2 -gpu swiftshader_indirect \
  >/tmp/lovetest-emulator.log 2>&1 &
EMU_PID=$!
log "emulator_pid=${EMU_PID}"
echo "booting" >"${STATUS}"

# Watchdog: kill sibling AVDs that respawn during boot/tests (16GB RAM).
(
  while kill -0 "${EMU_PID}" 2>/dev/null; do
    python3 - <<'PY'
import os, signal, subprocess
out = subprocess.check_output(["ps", "-ax", "-o", "pid=,command="], text=True, errors="replace")
for line in out.splitlines():
    parts = line.split(None, 1)
    if len(parts) < 2:
        continue
    pid_s, cmd = parts
    if "/qemu-system-" not in cmd:
        continue
    if "/bin/zsh" in cmd or "snap=$(command cat" in cmd:
        continue
    if "LoveTester_Capture" in cmd:
        continue
    try:
        os.kill(int(pid_s), signal.SIGKILL)
    except ProcessLookupError:
        pass
PY
    sleep 8
  done
) >/tmp/lt-qemu-watchdog.log 2>&1 &
echo $! >"${WATCHDOG_PID_FILE}"
log "watchdog_pid=$(cat "${WATCHDOG_PID_FILE}")"

boot=0
i=0
while [ "$i" -lt 100 ]; do
  i=$((i + 1))
  if ! kill -0 "${EMU_PID}" 2>/dev/null; then
    log "ERROR emulator process died during boot"
    echo "boot_fail" >"${STATUS}"
    exit 3
  fi
  prop="$(adb -s "${SERIAL}" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n' || true)"
  if [ "${prop}" = "1" ]; then
    boot=1
    log "boot_ok iter=${i}"
    break
  fi
  [ $((i % 5)) -eq 0 ] && log "still_booting iter=${i}"
  sleep 3
done
if [ "${boot}" != "1" ]; then
  log "ERROR boot timeout"
  echo "boot_fail" >"${STATUS}"
  exit 3
fi

adb -s "${SERIAL}" shell wm size reset >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell wm size 1080x1920 >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell wm density 420 >/dev/null 2>&1 || true
wm="$(adb -s "${SERIAL}" shell wm size 2>/dev/null | tr -d '\r\n' || true)"
log "wm=${wm}"
adb -s "${SERIAL}" shell pm uninstall com.hyperlockscreen.app >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell settings put global window_animation_scale 0 >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell settings put global transition_animation_scale 0 >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell settings put global animator_duration_scale 0 >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell cmd locale set-app-locales dev.lovetest.app --locales '' >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell am force-stop dev.lovetest.app >/dev/null 2>&1 || true

export LOVETEST_ADB_SERIAL="${SERIAL}"
export ANDROID_SERIAL="${SERIAL}"
unset LOVETEST_FORCE_EMULATOR || true
unset LOVETEST_USE_EMULATOR || true

echo "testing" >"${STATUS}"
log "connectedDebugAndroidTest start"
set +e
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.notAnnotation=androidx.test.filters.FlakyTest \
  >>"${LOG}" 2>&1
EC=$?
set -e
log "connected_exit=${EC}"

# Stop watchdog
if [ -f "${WATCHDOG_PID_FILE}" ]; then
  kill "$(cat "${WATCHDOG_PID_FILE}")" 2>/dev/null || true
  rm -f "${WATCHDOG_PID_FILE}"
fi

grep -E 'FAILED|BUILD |There was|tests completed|connected_exit' "${LOG}" | tail -40 >>"${LOG}" || true
echo "done exit=${EC}" >"${STATUS}"
exit "${EC}"
