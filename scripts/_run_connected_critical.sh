#!/bin/bash
# Critical connected tests on exclusive LoveTester.
set -eu
export PATH="${HOME}/Library/Android/sdk/emulator:${HOME}/Library/Android/sdk/platform-tools:${PATH:-}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

LOG=/tmp/lt-critical.log
STATUS=/tmp/lt-critical-status.txt
SERIAL=emulator-5554
: >"${LOG}"
echo starting >"${STATUS}"
log() { printf '%s\n' "$*" | tee -a "${LOG}" >/dev/null; printf '%s\n' "$*"; }

kill_foreign() {
  python3 -c '
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
        print("kill", pid_s)
    except ProcessLookupError:
        pass
'
}

log "=== $(date +%Y-%m-%dT%H:%M:%S) critical pid=$$ ==="
kill_foreign >>"${LOG}" 2>&1 || true
sleep 2
kill_foreign >>"${LOG}" 2>&1 || true
sleep 2

adb kill-server >/dev/null 2>&1 || true
sleep 1
adb start-server >/dev/null 2>&1 || true

log "launch LoveTester"
nohup emulator -avd LoveTester_Capture -port 5554 -skin 1080x1920 \
  -no-window -no-audio -no-snapshot-load -no-snapshot-save -no-boot-anim \
  -memory 1280 -cores 2 -gpu swiftshader_indirect \
  >/tmp/lovetest-emulator.log 2>&1 &
EMU_PID=$!
log "emulator_pid=${EMU_PID}"
echo booting >"${STATUS}"

# Watchdog
(
  while kill -0 "${EMU_PID}" 2>/dev/null; do
    kill_foreign >/dev/null 2>&1 || true
    sleep 2
  done
) >/tmp/lt-critical-watchdog.log 2>&1 &
WD=$!
log "watchdog=${WD}"

boot=0
for i in $(seq 1 80); do
  if ! kill -0 "${EMU_PID}" 2>/dev/null; then
    log "emu_dead iter=${i} restart"
    kill_foreign >>"${LOG}" 2>&1 || true
    sleep 1
    nohup emulator -avd LoveTester_Capture -port 5554 -skin 1080x1920 \
      -no-window -no-audio -no-snapshot-load -no-snapshot-save -no-boot-anim \
      -memory 1280 -cores 2 -gpu swiftshader_indirect \
      >/tmp/lovetest-emulator.log 2>&1 &
    EMU_PID=$!
    log "emulator_pid=${EMU_PID}"
  fi
  prop="$(adb -s "${SERIAL}" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n' || true)"
  if [ "${prop}" = "1" ]; then
    boot=1
    log "boot_ok iter=${i}"
    break
  fi
  [ $((i % 5)) -eq 0 ] && log "still_booting ${i}"
  sleep 3
done

if [ "${boot}" != "1" ]; then
  log "boot_fail"
  echo boot_fail >"${STATUS}"
  kill "${WD}" 2>/dev/null || true
  exit 3
fi

adb -s "${SERIAL}" shell wm size reset >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell wm size 1080x1920 >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell wm density 420 >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell settings put global animator_duration_scale 0 >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell settings put global window_animation_scale 0 >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell settings put global transition_animation_scale 0 >/dev/null 2>&1 || true
adb -s "${SERIAL}" shell cmd locale set-app-locales dev.lovetest.app --locales '' >/dev/null 2>&1 || true
log "wm=$(adb -s "${SERIAL}" shell wm size 2>/dev/null | tr -d '\r\n' || true)"

export ANDROID_SERIAL="${SERIAL}" LOVETEST_ADB_SERIAL="${SERIAL}"
echo testing >"${STATUS}"
log "tests start"

CLASSES='dev.lovetest.app.a11y.CriticalFlowsA11yComposeTest,dev.lovetest.app.navigation.LoveTestFlowComposeTest,dev.lovetest.app.navigation.CalculatorFlowComposeTest,dev.lovetest.app.navigation.ProtocolFlowComposeTest,dev.lovetest.app.navigation.WheelFlowComposeTest,dev.lovetest.app.navigation.ZodiacFlowComposeTest,dev.lovetest.app.navigation.MissingSessionRedirectComposeTest,dev.lovetest.app.ui.consent.ConsentScreenComposeTest,dev.lovetest.app.ui.features.VictoryResultScreenComposeTest,dev.lovetest.app.ui.share.ShareCardImageExporterInstrumentedTest,dev.lovetest.app.DebugStartRouteInstrumentedTest'

set +e
./gradlew :app:connectedDebugAndroidTest \
  "-Pandroid.testInstrumentationRunnerArguments.class=${CLASSES}" \
  >>"${LOG}" 2>&1
EC=$?
set -e
log "critical_exit=${EC}"
kill "${WD}" 2>/dev/null || true
echo "done exit=${EC}" >"${STATUS}"
exit "${EC}"
