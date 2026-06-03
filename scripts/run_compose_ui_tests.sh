#!/usr/bin/env bash
# Локальный запуск Compose UI tests (нужен эмулятор/устройство + adb).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

echo "=== run_compose_ui_tests ==="

if ! command -v adb >/dev/null 2>&1; then
  echo "ERROR: adb не в PATH — ./scripts/setup_android_sdk.sh" >&2
  exit 1
fi

if ! adb devices | grep -E '\tdevice$' >/dev/null; then
  echo "ERROR: нет подключённого устройства — запустите эмулятор или ./scripts/start_capture_emulator.sh" >&2
  exit 1
fi

echo "Tests: $(bash scripts/count_tests.sh | grep '^summary=' | cut -d= -f2-)"
echo ""
./gradlew :app:connectedDebugAndroidTest --no-daemon "$@"

echo ""
echo "OK: Compose UI tests"
