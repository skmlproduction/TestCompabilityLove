#!/usr/bin/env bash
# Подсказка / активация adb из Android SDK (macOS / Linux).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

echo "=== setup_android_sdk ==="

if command -v adb >/dev/null 2>&1; then
  echo "OK: adb в PATH — $(command -v adb)"
  adb version | head -1
  echo ""
  if adb devices | grep -E '\tdevice$' >/dev/null 2>&1; then
    echo "Устройство подключено:"
    adb devices -l
    echo ""
    echo "Съёмка:"
    echo "  ./scripts/capture_store_local.sh both priority"
  else
    echo "Устройство не подключено. Запуск эмулятора:"
    echo "  ./scripts/start_capture_emulator.sh"
    echo "  ./scripts/capture_store_local.sh both priority"
  fi
  exit 0
fi

echo "adb не найден даже после android_sdk_env.sh"
echo ""
echo "1. Android Studio → SDK Manager → Android SDK Platform-Tools"
echo "2. source ${ROOT}/scripts/android_sdk_env.sh"
echo "3. ./scripts/ensure_capture_avd.sh && ./scripts/start_capture_emulator.sh"
