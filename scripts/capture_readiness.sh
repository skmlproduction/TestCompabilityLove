#!/usr/bin/env bash
# Проверка готовности к съёмке Store PNG (без установки APK).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

echo "=== capture_readiness ==="

python3 - <<'PY'
import csv
import re
import struct
from pathlib import Path

root = Path(".")
csv_path = root / "docs/product/screens_catalog.csv"
debug_path = root / "app/src/main/java/dev/lovetest/app/debug/DebugUiPreview.kt"

text = debug_path.read_text(encoding="utf-8")
block = re.search(
    r"fun routeFor\(screenId: String\).*?when \(screenId\) \{(.*?)else -> null",
    text,
    re.DOTALL,
)
debug_ids = set(re.findall(r'"([a-z][a-z0-9_]*)"', block.group(1))) if block else set()

rows = list(csv.DictReader(csv_path.open(encoding="utf-8", newline="")))
missing_debug = [
    r["screen_id"]
    for r in rows
    if r["screen_id"] not in debug_ids and (r.get("route_path") or "").strip() not in ("", "N/A")
]

required: list[Path] = []
for row in rows:
    for key in ("screenshot_ru_relative", "screenshot_en_relative"):
        raw = (row.get(key) or "").strip()
        if not raw or "N/A" in raw.upper():
            continue
        required.append(root / raw)

placeholder = real = missing = 0
for path in required:
    if not path.is_file():
        missing += 1
        continue
    head = path.read_bytes()[:24]
    if len(head) < 24 or head[:8] != b"\x89PNG\r\n\x1a\n":
        missing += 1
        continue
    w, h = struct.unpack(">II", head[16:24])
    size = path.stat().st_size
    if (w, h) == (1080, 1920) and size < 32_000:
        placeholder += 1
    else:
        real += 1

print(f"Каталог screen_id: {len(rows)}")
print(f"DEBUG_UI_PREVIEW: {len(debug_ids)} id, без preview: {len(missing_debug)}")
if missing_debug:
    for sid in missing_debug:
        print(f"  MISSING: {sid}")
print(f"PNG: всего {len(required)}, реальных ≥32KB: {real}, placeholder: {placeholder}, отсутствуют: {missing}")
PY

if command -v adb >/dev/null 2>&1; then
  if adb get-state >/dev/null 2>&1; then
  model="$(adb shell getprop ro.product.model 2>/dev/null | tr -d '\r' || true)"
  size="$(adb shell wm size 2>/dev/null | tr -d '\r' || true)"
  echo "adb: OK (${model:-device}) ${size}"
  else
    echo "adb: установлен, устройство не подключено"
  fi
else
  echo "adb: не найден в PATH"
fi

echo ""
echo "Съёмка:"
echo "  ./gradlew captureScreenshotCatalogRu"
echo "  ./gradlew captureScreenshotCatalogEn"
echo "  ./gradlew verifyLoveTestBeforeStore"
