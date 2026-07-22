#!/usr/bin/env bash
# One-shot: wait for RAM, boot LoveTester_Capture from snapshot, install P1 APK,
# recapture P1 QA screens, rerun 4 failed critical classes.
# Does NOT kill foreign qemu/apps. Does NOT change MIUI/wm/fonts.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

LOG="${LOVETEST_PIPELINE_LOG:-/tmp/lovetest-boot-pipeline.log}"
exec > >(tee -a "$LOG") 2>&1

host_free() {
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

echo "=== poll start $(date) ==="
i=0
ready=0
SERIAL=""
while [ "$i" -lt 90 ]; do
  i=$((i + 1))
  f="$(host_free)"
  if pgrep -q qemu-system; then q=1; else q=0; fi
  echo "$(date +%H:%M:%S) iter=$i free=$f qemu=$q"
  if [ "$f" -ge 650 ] && [ "$q" -eq 0 ]; then
    ready=1
    break
  fi
  st="$(adb devices | awk '/emulator-.*device$/{print $1; exit}')"
  if [ -n "$st" ] && [ "$f" -ge 400 ]; then
    echo "HITCHHIKE $st free=$f"
    ready=2
    SERIAL="$st"
    break
  fi
  sleep 10
done

if [ "$ready" -eq 0 ]; then
  echo "NOT READY free=$(host_free)"
  exit 2
fi

if [ "$ready" -eq 1 ]; then
  # Pin port 5554 so foreign AVDs (e.g. PixelCap on 5558) are not mistaken for ours.
  # Prefer snapshot boot; -memory 1024 reduces LMK risk on 16GB hosts.
  AVD_NAME="${LOVETEST_CAPTURE_AVD:-LoveTester_Capture}"
  EMU_PORT="${LOVETEST_EMU_PORT:-5554}"
  SERIAL="emulator-${EMU_PORT}"
  echo "Booting ${AVD_NAME} from default_boot on port ${EMU_PORT}..."
  nohup emulator -avd "${AVD_NAME}" \
    -port "${EMU_PORT}" \
    -snapshot default_boot \
    -no-snapshot-save \
    -no-boot-anim \
    -no-audio \
    -gpu swiftshader_indirect \
    -memory 1024 \
    -cores 2 \
    >/tmp/lovetest-capture.log 2>&1 &
  EPID=$!
  echo "pid=$EPID expect_serial=$SERIAL"
  j=0
  while [ "$j" -lt 72 ]; do
    j=$((j + 1))
    st="$(adb devices | awk -v s="$SERIAL" '$1==s{print $2; exit}')"
    boot=""
    if [ "$st" = "device" ]; then
      boot="$(adb -s "$SERIAL" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
    fi
    f="$(host_free)"
    echo "boot_wait j=$j serial=$SERIAL st=${st:-none} boot=${boot:-?} free=$f"
    if [ "$st" = "device" ] && [ "$boot" = "1" ]; then
      adb -s "$SERIAL" shell wm size || true
      echo "EMULATOR READY $SERIAL"
      break
    fi
    if ! kill -0 "$EPID" 2>/dev/null; then
      echo "emulator died"
      tail -50 /tmp/lovetest-capture.log || true
      exit 3
    fi
    sleep 5
  done
  st="$(adb devices | awk -v s="$SERIAL" '$1==s{print $2; exit}')"
  if [ "$st" != "device" ]; then
    echo "boot timeout for $SERIAL"
    exit 4
  fi
fi

export ANDROID_SERIAL="$SERIAL"
export LOVETEST_ADB_SERIAL="$SERIAL"
USER_GP="${HOME}/.gradle/gradle.properties"
FREE="$(host_free)"
HEAP=384
KHEAP=192
if [ "$FREE" -ge 1200 ]; then HEAP=768; KHEAP=384; fi
if [ "$FREE" -ge 900 ] && [ "$FREE" -lt 1200 ]; then HEAP=512; KHEAP=256; fi
echo "install free=$FREE heap=$HEAP on $SERIAL"
cp "$USER_GP" "${USER_GP}.bak-lt"
python3 - "$HEAP" "$KHEAP" <<'PY'
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
restore_gp() { mv "${USER_GP}.bak-lt" "$USER_GP" 2>/dev/null || true; }
trap restore_gp EXIT
./gradlew --stop >/dev/null 2>&1 || true
./gradlew :app:installDebug :app:installDebugAndroidTest --max-workers=1
INSTALL_RC=$?
restore_gp
trap - EXIT
echo "install_rc=$INSTALL_RC"
if [ "$INSTALL_RC" -ne 0 ]; then
  exit "$INSTALL_RC"
fi

OUT_QA="${LOVETEST_SCREENSHOT_OUT_DIR:-${ROOT}/docs/screenshots/qa/emulator/ru}"
mkdir -p "$OUT_QA"
export LOVETEST_SCREENSHOT_OUT_DIR="$OUT_QA"
export LOVETEST_USE_EMULATOR=1
P1_SCREENS=(
  pair_input consent_ads_gdpr love_test_result_low
  calculator_result protocol_result zodiac_result
  pair_result letters_result victory_result
  hub_main love_test_result premium_paywall
)
for sid in "${P1_SCREENS[@]}"; do
  echo "=== capture $sid ==="
  bash scripts/adb_screenshot_preview.sh "$sid" ru </dev/null || echo "WARN capture $sid"
done

FAILED_CLASSES=(
  "dev.lovetest.app.navigation.CalculatorFlowComposeTest"
  "dev.lovetest.app.navigation.ProtocolFlowComposeTest"
  "dev.lovetest.app.navigation.ZodiacFlowComposeTest"
  "dev.lovetest.app.ui.consent.ConsentScreenComposeTest"
)
OK=0
FAIL=0
adb -s "$SERIAL" shell am force-stop com.hyperlockscreen.app >/dev/null 2>&1 || true
for cls in "${FAILED_CLASSES[@]}"; do
  echo "=== $cls ==="
  OUT="$(adb -s "$SERIAL" shell am instrument -w -r -e class "$cls" \
    "dev.lovetest.app.test/androidx.test.runner.AndroidJUnitRunner" 2>&1)" || true
  echo "$OUT" | tail -30
  if echo "$OUT" | grep -q "OK ("; then
    OK=$((OK + 1))
  else
    FAIL=$((FAIL + 1))
  fi
done
echo "CRITICAL_RERUN ok=$OK fail=$FAIL"
echo "=== PIPELINE DONE $(date) ==="
