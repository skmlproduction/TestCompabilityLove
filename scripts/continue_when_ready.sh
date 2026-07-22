#!/usr/bin/env bash
# Wait for adb device + host free RAM, then install, recapture P1 QA, rerun failed critical.
# Usage: ./scripts/continue_when_ready.sh
# Does NOT change MIUI/wm/fonts. Does NOT kill foreign apps/AVDs.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

MIN_FREE_MB="${LOVETEST_MIN_HOST_FREE_MB:-900}"
USER_GP="${HOME}/.gradle/gradle.properties"
# Default QA dir: Xiaomi if that serial, else emulator archive
OUT_QA="${LOVETEST_SCREENSHOT_OUT_DIR:-}"
if [[ -z "${OUT_QA}" ]]; then
  OUT_QA="${ROOT}/docs/screenshots/qa/emulator/ru"
fi
FAILED_CLASSES=(
  "dev.lovetest.app.navigation.CalculatorFlowComposeTest"
  "dev.lovetest.app.navigation.ProtocolFlowComposeTest"
  "dev.lovetest.app.navigation.ZodiacFlowComposeTest"
  "dev.lovetest.app.ui.consent.ConsentScreenComposeTest"
)
P1_SCREENS=(
  pair_input consent_ads_gdpr love_test_result_low
  calculator_result protocol_result zodiac_result
  pair_result letters_result victory_result
  hub_main love_test_result premium_paywall
)

host_free_mb() {
  python3 - <<'PY'
import subprocess, re
out = subprocess.check_output(["vm_stat"], text=True)
page = 16384
free = 0
for line in out.splitlines():
    if "page size of" in line:
        m = re.search(r"(\d+)", line)
        if m:
            page = int(m.group(1))
    if line.startswith("Pages free:") or line.startswith("Pages speculative:"):
        free += int(line.split(":")[1].strip().rstrip("."))
print(int(free * page / 1024 / 1024))
PY
}

pick_serial() {
  if adb devices | grep -q 'PF99SSNFFQQ4XCF6[[:space:]]*device'; then
    echo PF99SSNFFQQ4XCF6
  else
    adb devices | awk '/[[:space:]]device$/{print $1; exit}'
  fi
}

echo "continue_when_ready: need device + free>=${MIN_FREE_MB}MB"
for i in $(seq 1 120); do
  SERIAL="$(pick_serial || true)"
  FREE="$(host_free_mb)"
  echo "$(date +%H:%M:%S) free_mb=${FREE} serial=${SERIAL:-none}"
  if [[ -n "${SERIAL}" && "${FREE}" -ge "${MIN_FREE_MB}" ]]; then
    export ANDROID_SERIAL="${SERIAL}"
    export LOVETEST_ADB_SERIAL="${SERIAL}"
    echo "GO — install on ${SERIAL} (free=${FREE})"
    cp "${USER_GP}" "${USER_GP}.bak-lt"
    # Scale heap to available free RAM
    HEAP=384
    KHEAP=192
    if [[ "${FREE}" -ge 1200 ]]; then HEAP=768; KHEAP=384; fi
    if [[ "${FREE}" -ge 900 && "${FREE}" -lt 1200 ]]; then HEAP=512; KHEAP=256; fi
    echo "heap=${HEAP}m kotlin=${KHEAP}m"
    python3 - "${HEAP}" "${KHEAP}" <<'PY'
from pathlib import Path
import re, sys
heap, kheap = sys.argv[1], sys.argv[2]
p = Path.home() / ".gradle" / "gradle.properties"
t = p.read_text()
t = re.sub(
    r"org\.gradle\.jvmargs=.*",
    f"org.gradle.jvmargs=-Xmx{heap}m -XX:MaxMetaspaceSize=192m -XX:-TieredCompilation -Dfile.encoding=UTF-8",
    t,
)
t = re.sub(r"kotlin\.daemon\.jvmargs=.*\n?", "", t)
t += f"\nkotlin.daemon.jvmargs=-Xmx{kheap}m -XX:MaxMetaspaceSize=128m -XX:-TieredCompilation -Dfile.encoding=UTF-8\n"
t = re.sub(r"org\.gradle\.workers\.max=.*", "org.gradle.workers.max=1", t)
t = re.sub(r"org\.gradle\.parallel=.*", "org.gradle.parallel=false", t)
p.write_text(t)
PY
    restore_gp() { mv "${USER_GP}.bak-lt" "${USER_GP}" 2>/dev/null || true; }
    trap restore_gp EXIT
    ./gradlew --stop >/dev/null 2>&1 || true
    ./gradlew :app:installDebug :app:installDebugAndroidTest --max-workers=1
    restore_gp
    trap - EXIT

    if [[ "${SERIAL}" == PF99SSNFFQQ4XCF6* ]]; then
      OUT_QA="${LOVETEST_SCREENSHOT_OUT_DIR:-${ROOT}/docs/screenshots/qa/xiaomi/ru}"
    else
      OUT_QA="${LOVETEST_SCREENSHOT_OUT_DIR:-${ROOT}/docs/screenshots/qa/emulator/ru}"
    fi
    mkdir -p "${OUT_QA}"
    export LOVETEST_SCREENSHOT_OUT_DIR="${OUT_QA}"
    unset LOVETEST_USE_EMULATOR || true
    for sid in "${P1_SCREENS[@]}"; do
      bash scripts/adb_screenshot_preview.sh "${sid}" ru </dev/null || echo "WARN capture ${sid}"
    done

    LOG=build/continue_critical_rerun.log
    mkdir -p build
    {
      OK=0; FAIL=0
      adb -s "${SERIAL}" shell am force-stop com.hyperlockscreen.app >/dev/null 2>&1 || true
      for cls in "${FAILED_CLASSES[@]}"; do
        echo "=== ${cls} ==="
        OUT="$(adb -s "${SERIAL}" shell am instrument -w -r \
          -e class "${cls}" \
          "dev.lovetest.app.test/androidx.test.runner.AndroidJUnitRunner" 2>&1)" || true
        echo "${OUT}" | tail -25
        if echo "${OUT}" | grep -q "OK ("; then OK=$((OK + 1)); else FAIL=$((FAIL + 1)); fi
        sleep 2
      done
      echo "DONE ok=${OK} fail=${FAIL}"
    } 2>&1 | tee "${LOG}"

    echo "continue_when_ready: finished — see ${LOG} and ${OUT_QA}"
    exit 0
  fi
  sleep 15
done
echo "continue_when_ready: timeout waiting for device/RAM" >&2
exit 2
