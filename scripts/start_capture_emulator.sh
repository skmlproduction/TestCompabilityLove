#!/usr/bin/env bash
# Запускает эмулятор съёмки и ждёт boot_completed.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

AVD_NAME="${LOVETEST_CAPTURE_AVD:-LoveTester_Capture}"
BOOT_TIMEOUT_SEC="${LOVETEST_EMULATOR_BOOT_TIMEOUT:-300}"

echo "=== start_capture_emulator (${AVD_NAME}) ==="

if ! command -v emulator >/dev/null 2>&1; then
  echo "ERROR: emulator не найден в SDK" >&2
  exit 1
fi
if ! command -v adb >/dev/null 2>&1; then
  echo "ERROR: adb не найден — ./scripts/setup_android_sdk.sh" >&2
  exit 1
fi

bash "${ROOT}/scripts/ensure_capture_avd.sh"

if adb devices | grep -E '\tdevice$' >/dev/null 2>&1; then
  echo "Устройство уже подключено — пропускаю запуск эмулятора"
  adb shell getprop sys.boot_completed 2>/dev/null | grep -q 1 && exit 0
fi

echo "Запуск эмулятора (фон)…"
nohup emulator -avd "${AVD_NAME}" \
  -no-snapshot-save \
  -no-boot-anim \
  -gpu auto \
  >/tmp/lovetest-emulator.log 2>&1 &

echo "Ожидание adb device (до ${BOOT_TIMEOUT_SEC}s)…"
adb wait-for-device

deadline=$((SECONDS + BOOT_TIMEOUT_SEC))
while [[ "${SECONDS}" -lt "${deadline}" ]]; do
  boot="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
  if [[ "${boot}" == "1" ]]; then
    wm_size="$(adb shell wm size 2>/dev/null | tr -d '\r' || true)"
    echo "OK: эмулятор готов ${wm_size}"
    exit 0
  fi
  sleep 3
done

echo "ERROR: эмулятор не загрузился за ${BOOT_TIMEOUT_SEC}s — см. /tmp/lovetest-emulator.log" >&2
exit 1
