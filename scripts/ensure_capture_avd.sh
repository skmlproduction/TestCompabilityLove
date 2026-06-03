#!/usr/bin/env bash
# Создаёт AVD для Store-съёмки (1080×1920) если его ещё нет.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

AVD_NAME="${LOVETEST_CAPTURE_AVD:-LoveTester_Capture}"
SYSTEM_IMAGE="${LOVETEST_CAPTURE_SYSTEM_IMAGE:-system-images;android-34;google_apis_playstore;arm64-v8a}"
DEVICE_ID="${LOVETEST_CAPTURE_DEVICE:-pixel_6}"
TARGET_WIDTH=1080
TARGET_HEIGHT=1920

echo "=== ensure_capture_avd (${AVD_NAME}) ==="

if ! command -v avdmanager >/dev/null 2>&1; then
  echo "ERROR: avdmanager не найден — установите Android SDK cmdline-tools" >&2
  exit 1
fi

if ! avdmanager list avd 2>/dev/null | grep -q "Name: ${AVD_NAME}"; then
  echo "Создаю AVD ${AVD_NAME} (${SYSTEM_IMAGE})…"
  echo "no" | avdmanager create avd \
    -n "${AVD_NAME}" \
    -k "${SYSTEM_IMAGE}" \
    -d "${DEVICE_ID}" \
    --force
else
  echo "AVD уже есть: ${AVD_NAME}"
fi

AVD_DIR="${HOME}/.android/avd/${AVD_NAME}.avd"
CONFIG="${AVD_DIR}/config.ini"
if [[ ! -f "${CONFIG}" ]]; then
  echo "ERROR: не найден ${CONFIG}" >&2
  exit 1
fi

set_config() {
  local key="$1"
  local value="$2"
  if grep -q "^${key}=" "${CONFIG}"; then
    if [[ "$(uname)" == Darwin ]]; then
      sed -i '' "s|^${key}=.*|${key}=${value}|" "${CONFIG}"
    else
      sed -i "s|^${key}=.*|${key}=${value}|" "${CONFIG}"
    fi
  else
    echo "${key}=${value}" >> "${CONFIG}"
  fi
}

set_config "hw.lcd.width" "${TARGET_WIDTH}"
set_config "hw.lcd.height" "${TARGET_HEIGHT}"
set_config "hw.lcd.density" "420"

echo "OK: ${AVD_NAME} → ${TARGET_WIDTH}×${TARGET_HEIGHT}"
echo "Запуск: ./scripts/start_capture_emulator.sh"
