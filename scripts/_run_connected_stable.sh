#!/bin/bash
# Detached stable critical connected run. Status: /tmp/lt-stable-status.txt
set +e
export PATH="${HOME}/Library/Android/sdk/emulator:${HOME}/Library/Android/sdk/platform-tools:${PATH:-}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

LOG=/tmp/lt-stable.log
STATUS=/tmp/lt-stable-status.txt
SERIAL=emulator-5554
: >"${LOG}"
echo starting >"${STATUS}"
log() { echo "$*" >>"${LOG}"; }

log "=== $(date +%Y-%m-%dT%H:%M:%S) stable pid=$$ ==="

# Kill every qemu except we have none yet
python3 - <<'PY' >>"${LOG}" 2>&1
import os, signal, subprocess, time
def kill_all_qemu():
    out = subprocess.check_output(["ps", "-ax", "-o", "pid=,command="], text=True, errors="replace")
    for line in out.splitlines():
        parts = line.split(None, 1)
        if len(parts) < 2:
            continue
        if "/qemu-system-" not in parts[1]:
            continue
        if "/bin/zsh" in parts[1]:
            continue
        try:
            os.kill(int(parts[0]), signal.SIGKILL)
            print("kill", parts[0])
        except ProcessLookupError:
            pass
kill_all_qemu()
time.sleep(2)
kill_all_qemu()
time.sleep(2)
PY

adb kill-server >/dev/null 2>&1
sleep 1
adb start-server >/dev/null 2>&1

log "launch"
nohup emulator -avd LoveTester_Capture -port 5554 -skin 1080x1920 \
  -no-window -no-audio -no-snapshot-load -no-snapshot-save -no-boot-anim \
  -memory 1536 -cores 2 -gpu swiftshader_indirect \
  >/tmp/lovetest-emulator.log 2>&1 &
log "launcher=$!"
echo booting >"${STATUS}"

# Narrow watchdog — named rivals only
nohup bash -c 'while true; do
python3 -c "
import os,signal,subprocess
out=subprocess.check_output([\"ps\",\"-ax\",\"-o\",\"pid=,command=\"],text=True,errors=\"replace\")
for line in out.splitlines():
    if \"/qemu-system-\" not in line: continue
    if \"LoveTester_Capture\" in line: continue
    if \"/bin/zsh\" in line: continue
    if any(x in line for x in (\"RegSnap\",\"HyperLock\",\"MatchByNames\",\"HLS_\",\"NEWlock\",\"RegSnap_\")):
        try: os.kill(int(line.split(None,1)[0]), signal.SIGKILL)
        except Exception: pass
" >/dev/null 2>&1
sleep 2
done' >/tmp/lt-stable-wd.log 2>&1 &
echo $! >/tmp/lt-stable-wd.pid
log "wd=$(cat /tmp/lt-stable-wd.pid)"

boot=0
for i in $(seq 1 70); do
  prop=$(adb -s "${SERIAL}" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
  log "iter=$i boot=${prop:-}"
  if [ "${prop}" = "1" ]; then boot=1; break; fi
  # if qemu gone, do NOT thrash-restart more than twice
  if ! pgrep -f 'LoveTester_Capture' >/dev/null 2>&1; then
    log "qemu missing at iter=$i"
  fi
  sleep 4
done

if [ "${boot}" != "1" ]; then
  log BOOT_FAIL
  echo boot_fail >"${STATUS}"
  kill "$(cat /tmp/lt-stable-wd.pid)" 2>/dev/null
  exit 3
fi

adb -s "${SERIAL}" shell wm size 1080x1920 >/dev/null 2>&1
adb -s "${SERIAL}" shell wm density 420 >/dev/null 2>&1
adb -s "${SERIAL}" shell settings put global animator_duration_scale 0 >/dev/null 2>&1
adb -s "${SERIAL}" shell settings put global window_animation_scale 0 >/dev/null 2>&1
adb -s "${SERIAL}" shell settings put global transition_animation_scale 0 >/dev/null 2>&1
adb -s "${SERIAL}" shell cmd locale set-app-locales dev.lovetest.app --locales '' >/dev/null 2>&1
log "wm=$(adb -s "${SERIAL}" shell wm size 2>/dev/null | tr -d '\r')"

export ANDROID_SERIAL="${SERIAL}" LOVETEST_ADB_SERIAL="${SERIAL}"
echo testing >"${STATUS}"
log "tests"

CLASSES='dev.lovetest.app.a11y.CriticalFlowsA11yComposeTest,dev.lovetest.app.navigation.LoveTestFlowComposeTest,dev.lovetest.app.navigation.CalculatorFlowComposeTest,dev.lovetest.app.navigation.ProtocolFlowComposeTest,dev.lovetest.app.navigation.WheelFlowComposeTest,dev.lovetest.app.navigation.ZodiacFlowComposeTest,dev.lovetest.app.navigation.MissingSessionRedirectComposeTest,dev.lovetest.app.ui.consent.ConsentScreenComposeTest,dev.lovetest.app.ui.features.VictoryResultScreenComposeTest,dev.lovetest.app.ui.share.ShareCardImageExporterInstrumentedTest,dev.lovetest.app.DebugStartRouteInstrumentedTest'

./gradlew :app:connectedDebugAndroidTest \
  "-Pandroid.testInstrumentationRunnerArguments.class=${CLASSES}" \
  >>"${LOG}" 2>&1
EC=$?
log "critical_exit=${EC}"
echo "done exit=${EC}" >"${STATUS}"
kill "$(cat /tmp/lt-stable-wd.pid)" 2>/dev/null
exit "${EC}"
