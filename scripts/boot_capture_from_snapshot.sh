#!/usr/bin/env bash
# Boot LoveTester_Capture from emp_ready_p1 only if host free RAM is enough.
# Does NOT kill foreign qemu. Usage:
#   ./scripts/boot_capture_from_snapshot.sh
#   LOVETEST_MIN_FREE_MB=3500 ./scripts/boot_capture_from_snapshot.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

AVD="${LOVETEST_CAPTURE_AVD:-LoveTester_Capture}"
SNAPSHOT="${LOVETEST_CAPTURE_SNAPSHOT:-emp_ready_p1}"
PORT="${LOVETEST_EMU_PORT:-5554}"
MIN_FREE_MB="${LOVETEST_MIN_FREE_MB:-3400}"
SERIAL="emulator-${PORT}"

free_mb() {
  if [[ "$(uname)" == Darwin ]]; then
    local pages
    pages="$(vm_stat | awk '/Pages free/ {gsub(/\./,"",$3); print $3}')"
    echo $((pages * 16 / 1024))
  else
    awk '/MemAvailable/ {print int($2/1024)}' /proc/meminfo
  fi
}

if pgrep -f "qemu-system-.*${AVD}" >/dev/null 2>&1; then
  echo "Already running: ${AVD}"
  adb devices -l
  exit 0
fi

# Align config so snapshot can load
bash "${ROOT}/scripts/align_capture_snapshot_config.sh"

have="$(free_mb)"
echo "host free_mb=${have} (need ≥ ${MIN_FREE_MB} for snapshot ${SNAPSHOT})"
if [[ "${have}" -lt "${MIN_FREE_MB}" ]]; then
  echo "SKIP: not enough free RAM — retry later or free other apps/AVDs." >&2
  exit 3
fi

if adb devices 2>/dev/null | grep -qE 'emulator-[0-9]+'; then
  echo "WARN: another emulator already in adb — not killing it." >&2
  adb devices -l
  # Prefer free port if 5554 busy
  if adb devices | grep -q "emulator-${PORT}"; then
    PORT=5556
    SERIAL="emulator-${PORT}"
    echo "Using alternate port ${PORT}"
  fi
fi

LOG="/tmp/lovetester_capture_snapshot_boot.log"
echo "Booting ${AVD} snapshot=${SNAPSHOT} port=${PORT} → ${LOG}"
nohup emulator -avd "${AVD}" -port "${PORT}" \
  -no-window -no-audio -no-boot-anim \
  -snapshot "${SNAPSHOT}" -no-snapshot-save \
  -gpu swiftshader_indirect \
  > "${LOG}" 2>&1 &
echo "emu_pid=$!"

for i in $(seq 1 90); do
  boot="$(adb -s "${SERIAL}" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
  if [[ "${boot}" == "1" ]]; then
    echo "READY ${SERIAL} after ${i}s"
    adb -s "${SERIAL}" devices -l
    # Quick proof screencap
    OUT="${ROOT}/docs/screenshots/qa/emulator/ru/_snapshot_boot_canary.png"
    mkdir -p "$(dirname "${OUT}")"
    adb -s "${SERIAL}" exec-out screencap -p > "${OUT}" || true
    ls -la "${OUT}" 2>/dev/null || true
    exit 0
  fi
  if ! pgrep -f "qemu-system-.*${AVD}" >/dev/null 2>&1; then
    echo "ERROR: emulator exited — see ${LOG}" >&2
    tail -30 "${LOG}" >&2 || true
    exit 1
  fi
  sleep 2
done

echo "ERROR: timeout waiting for boot_completed on ${SERIAL}" >&2
tail -40 "${LOG}" >&2 || true
exit 2
