#!/usr/bin/env bash
# Съёмка каталога screens_catalog.csv на эмуляторе/устройстве.
#
# Usage:
#   ./scripts/capture_screenshot_catalog.sh ru
#   ./scripts/capture_screenshot_catalog.sh en --out-dir ci-screenshots
#   ./scripts/capture_screenshot_catalog.sh ru hub_main love_test_result
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOCALE="${1:?Usage: $0 <ru|en> [--out-dir DIR] [screen_id ...]}"
shift || true
PKG="dev.lovetest.app"

if [[ "$LOCALE" != ru && "$LOCALE" != en ]]; then
  echo "Locale must be ru or en" >&2
  exit 1
fi

OUT_DIR=""
IDS=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    --out-dir)
      OUT_DIR="${2:?--out-dir requires a path}"
      shift 2
      ;;
    *)
      IDS+=("$1")
      shift
      ;;
  esac
done

if [[ -z "$OUT_DIR" ]]; then
  OUT_DIR="${ROOT}/docs/screenshots/${LOCALE}"
fi
mkdir -p "$OUT_DIR"

if ! command -v adb >/dev/null 2>&1; then
  echo "adb not found in PATH — ./scripts/setup_android_sdk.sh" >&2
  exit 1
fi

if [[ ${#IDS[@]} -eq 0 ]]; then
  while IFS= read -r line; do
    IDS+=("$line")
  done < <(python3 - <<'PY'
import csv
from pathlib import Path
with Path("docs/product/screens_catalog.csv").open(encoding="utf-8", newline="") as f:
    for row in csv.DictReader(f):
        sid = (row.get("screen_id") or "").strip()
        if sid:
            print(sid)
PY
)
fi

echo "capture_screenshot_catalog: locale=${LOCALE} out=${OUT_DIR} screens=${#IDS[@]}"
echo "Installing debug APK…"
(cd "${ROOT}" && ./gradlew :app:installDebug -q)

COUNT=0
FAIL=0
for sid in "${IDS[@]}"; do
  [[ -z "$sid" ]] && continue
  if bash "${ROOT}/scripts/adb_screenshot_preview.sh" "$sid" "$LOCALE"; then
    COUNT=$((COUNT + 1))
    rel_path="$(python3 - "$ROOT" "$sid" "$LOCALE" <<'PY'
import csv, sys
from pathlib import Path
root, sid, loc = sys.argv[1:4]
key = "screenshot_ru_relative" if loc == "ru" else "screenshot_en_relative"
with open(Path(root) / "docs/product/screens_catalog.csv", encoding="utf-8") as f:
    for row in csv.DictReader(f):
        if row["screen_id"] == sid:
            print(row[key])
            break
PY
)"
    src="${ROOT}/${rel_path}"
    artifact="${OUT_DIR}/${sid}.png"
    if [[ -f "${src}" && "${src}" != "${artifact}" ]]; then
      cp "${src}" "${artifact}"
    elif [[ -f "${src}" && "${OUT_DIR}" != "${ROOT}/docs/screenshots/${LOCALE}" ]]; then
      cp "${src}" "${artifact}"
    fi
  else
    echo "FAIL: $sid" >&2
    FAIL=$((FAIL + 1))
  fi
done

if [[ "${LOCALE}" == "en" ]]; then
  adb shell cmd locale set-app-locales "${PKG}" --locales ru-RU 2>/dev/null || true
fi

REAL="$(python3 - <<'PY'
import struct
from pathlib import Path
n = 0
for p in Path("docs/screenshots").rglob("*.png"):
    head = p.read_bytes()[:24]
    if len(head) < 24 or head[:8] != b"\x89PNG\r\n\x1a\n":
        continue
    w, h = struct.unpack(">II", head[16:24])
    if (w, h) == (1080, 1920) and p.stat().st_size >= 32_000:
        n += 1
print(n)
PY
)"
echo "capture_screenshot_catalog: done ${COUNT} ok, ${FAIL} failed (${LOCALE})"
echo "screenshots ≥32KB (не placeholder): ${REAL}"
[[ "$FAIL" -eq 0 ]]
