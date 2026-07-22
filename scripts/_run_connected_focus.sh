#!/bin/bash
set -eu
export PATH="${HOME}/Library/Android/sdk/emulator:${HOME}/Library/Android/sdk/platform-tools:${PATH:-}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
source "${ROOT}/scripts/android_sdk_env.sh"

LOG=/tmp/lt-focus.log
STATUS=/tmp/lt-focus-status.txt
SERIAL=emulator-5554
: >"${LOG}"
echo starting >"${STATUS}"
log() { printf '%s\n' "$*" >>"${LOG}"; }

python3 >>"${LOG}" 2>&1 <<'PY'
import os, signal, subprocess
out = subprocess.check_output(["ps", "-ax", "-o", "pid=,command="], text=True, errors="replace")
for line in out.splitlines():
    if "qemu-system-aarch64" in line or "qemu-system-x86_64" in line:
        pid = int(line.split(None, 1)[0])
        print("kill", pid)
        try:
            os.kill(pid, signal.SIGKILL)
        except ProcessLookupError:
            pass
PY
sleep 4
adb kill-server >/dev/null 2>&1 || true
sleep 1
adb start-server >/dev/null 2>&1 || true
nohup emulator -avd LoveTester_Capture -port 5554 -skin 1080x1920 \
  -no-window -no-audio -no-snapshot-load -no-snapshot-save -no-boot-anim \
  -memory 1536 -cores 2 -gpu swiftshader_indirect >/tmp/lovetest-emulator.log 2>&1 &
log "emu=$!"
echo booting >"${STATUS}"
boot=0
i=0
while [ "$i" -lt 90 ]; do
  i=$((i + 1))
  prop="$(adb -s "${SERIAL}" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n' || true)"
  if [ "${prop}" = "1" ]; then boot=1; log "boot_ok $i"; break; fi
  sleep 3
done
[ "$boot" = "1" ] || { echo boot_fail >"${STATUS}"; exit 3; }

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
CLASSES='dev.lovetest.app.ui.consent.ConsentScreenComposeTest,dev.lovetest.app.ui.features.CalculatorResultScreenComposeTest,dev.lovetest.app.a11y.CriticalFlowsA11yComposeTest,dev.lovetest.app.ui.features.VictoryResultScreenComposeTest'
set +e
./gradlew :app:connectedDebugAndroidTest \
  "-Pandroid.testInstrumentationRunnerArguments.class=${CLASSES}" \
  >>"${LOG}" 2>&1
EC=$?
set -e
log "focus_exit=${EC}"
echo "done exit=${EC}" >"${STATUS}"
exit "${EC}"
