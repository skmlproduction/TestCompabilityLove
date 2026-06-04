#!/usr/bin/env bash
# Пересъёмка Store PNG на текущей debug-сборке (34 RU + 34 EN + listing 7×2).
# Без adb — выход с кодом 1 и инструкцией; существующие PNG не трогаем.
#
# Usage:
#   ./scripts/recapture_store_screenshots.sh
#   ./scripts/recapture_store_screenshots.sh --skip-gates   # только съёмка + pack
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

SKIP_GATES=false
for arg in "$@"; do
  case "$arg" in
    --skip-gates) SKIP_GATES=true ;;
    -h|--help)
      sed -n '2,12p' "$0" | sed 's/^# \{0,1\}//'
      exit 0
      ;;
    *)
      echo "Unknown arg: $arg (use --skip-gates)" >&2
      exit 1
      ;;
  esac
done

LISTING=(
  hub_main
  love_test_input
  love_test_result
  protocol_input
  protocol_result
  wheel_spin
  premium_paywall
)

print_no_device_help() {
  cat <<'EOF'
=== recapture_store_screenshots: НЕТ УСТРОЙСТВА ===

Пересъёмка не выполнена. Файлы в docs/screenshots/{ru,en}/ НЕ изменены.

Подготовка (macOS, один раз):
  1. Android Studio → SDK Platform-Tools
  2. source ./scripts/android_sdk_env.sh
  3. ./scripts/ensure_capture_avd.sh
  4. ./scripts/start_capture_emulator.sh
  5. adb devices   # должно быть одно устройство «device»

Полная пересъёмка (34+34, исправленная UI-сборка):
  ./gradlew assembleDebug
  ./scripts/recapture_store_screenshots.sh

Что снимается:
  • docs/screenshots/ru/*.png — 34 screen_id из screens_catalog.csv
  • docs/screenshots/en/*.png — 34 screen_id (locale en-US)
  • listing 7×RU/EN — копия тех же PNG в build/store-upload/listing-screenshots/
    (love_test_result, protocol_result, wheel_spin — с дисклеймером на UI)

Проверка после съёмки:
  python3 scripts/verify_ui_inventory.py --require-screenshots --fail-on-placeholders
  ./scripts/pack_store_upload.sh
  ./scripts/validate_store_upload.sh
  # или: ./gradlew verifyLoveTestBeforeStore

Документация: docs/screenshots/RECAPTURE_AFTER_UI_FIX.md
EOF
}

if ! command -v adb >/dev/null 2>&1; then
  print_no_device_help
  exit 1
fi

if ! adb get-state >/dev/null 2>&1; then
  echo "adb: устройство не подключено (adb devices пуст)" >&2
  print_no_device_help
  exit 1
fi

echo "=== recapture_store_screenshots ==="
echo "Эмулятор/устройство:"
adb devices -l | sed -n '2,$p'
echo ""
echo "Сборка и установка debug APK (UI-фиксы)…"
./gradlew :app:assembleDebug :app:installDebug -q

echo ""
echo "--- Каталог RU (34) ---"
bash "${ROOT}/scripts/capture_screenshot_catalog.sh" ru

echo ""
echo "--- Каталог EN (34) ---"
bash "${ROOT}/scripts/capture_screenshot_catalog.sh" en

echo ""
echo "--- Listing 7 (проверка наличия) ---"
MISS=0
for loc in ru en; do
  for sid in "${LISTING[@]}"; do
    f="${ROOT}/docs/screenshots/${loc}/${sid}.png"
    if [[ ! -f "${f}" ]]; then
      echo "MISSING: ${f#${ROOT}/}" >&2
      MISS=$((MISS + 1))
    fi
  done
done
if [[ "${MISS}" -gt 0 ]]; then
  echo "Listing PNG неполный (${MISS} missing)" >&2
  exit 1
fi
echo "Listing source OK (7×2 в docs/screenshots/)"

echo ""
echo "--- pack_store_upload (listing → build/store-upload/) ---"
bash "${ROOT}/scripts/pack_store_upload.sh"

if $SKIP_GATES; then
  echo "recapture_store_screenshots: done (gates skipped)"
  exit 0
fi

echo ""
echo "--- PNG gate ---"
python3 "${ROOT}/scripts/verify_ui_inventory.py" \
  --require-screenshots \
  --fail-on-placeholders

echo ""
echo "--- validate_store_upload ---"
bash "${ROOT}/scripts/validate_store_upload.sh"

python3 - <<'PY'
import struct
from datetime import datetime
from pathlib import Path

root = Path("docs/screenshots")
stamp = root / "CAPTURE_BUILD_STAMP.txt"
lines = [
    f"captured_at={datetime.now().isoformat(timespec='seconds')}",
    "build=assembleDebug",
    "method=recapture_store_screenshots.sh",
    "locales=ru,en",
    "count_per_locale=34",
    "listing=hub_main,love_test_input,love_test_result,protocol_input,protocol_result,wheel_spin,premium_paywall",
]
for loc in ("ru", "en"):
    n = 0
    for p in (root / loc).glob("*.png"):
        head = p.read_bytes()[:24]
        if len(head) >= 24 and head[:8] == b"\x89PNG\r\n\x1a\n":
            w, h = struct.unpack(">II", head[16:24])
            if (w, h) == (1080, 1920) and p.stat().st_size >= 32_000:
                n += 1
    lines.append(f"{loc}_real_1080x1920={n}")
stamp.write_text("\n".join(lines) + "\n", encoding="utf-8")
print(f"Wrote {stamp}")
PY

echo ""
echo "recapture_store_screenshots: OK"
