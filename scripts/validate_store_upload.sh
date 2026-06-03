#!/usr/bin/env bash
# Проверка build/store-upload/ перед загрузкой в Play.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
OUT="${ROOT}/build/store-upload"

echo "=== validate_store_upload ==="
FAIL=0

warn() { echo "  [WARN] $1"; }
fail() { echo "  [FAIL] $1"; FAIL=1; }
pass() { echo "  [OK]   $1"; }

if [[ ! -d "${OUT}" ]]; then
  fail "нет ${OUT} — ./scripts/pack_store_upload.sh"
  exit 1
fi

AAB="$(ls -1 "${OUT}"/*.aab 2>/dev/null | head -1 || true)"
if [[ -n "${AAB}" && "$(wc -c <"${AAB}" | tr -d ' ')" -ge 1000000 ]]; then
  pass "AAB $(basename "${AAB}")"
else
  fail "AAB отсутствует или слишком мал"
fi

if [[ -f "${OUT}/mapping.txt" && "$(wc -c <"${OUT}/mapping.txt" | tr -d ' ')" -ge 1000 ]]; then
  pass "mapping.txt"
else
  warn "mapping.txt отсутствует или пуст"
fi

if [[ -f "${OUT}/feature_graphic.png" && "$(wc -c <"${OUT}/feature_graphic.png" | tr -d ' ')" -ge 50000 ]]; then
  pass "feature_graphic.png"
else
  warn "feature_graphic.png отсутствует или < 50KB"
fi

for loc in ru en; do
  dir="${OUT}/listing-screenshots/${loc}"
  count=0
  if [[ -d "${dir}" ]]; then
    count="$(find "${dir}" -maxdepth 1 -name '*.png' 2>/dev/null | wc -l | tr -d ' ')"
  fi
  if [[ "${count}" -ge 7 ]]; then
    pass "listing-screenshots/${loc}/ (${count} PNG)"
  elif [[ "${count}" -ge 2 ]]; then
    warn "listing-screenshots/${loc}/ (${count} PNG) — рекомендуем ≥7"
  else
    warn "listing-screenshots/${loc}/ — ./scripts/pack_store_upload.sh"
  fi
done

if [[ -f "${OUT}/UPLOAD_MANIFEST.txt" ]]; then
  pass "UPLOAD_MANIFEST.txt"
else
  warn "UPLOAD_MANIFEST.txt — пересоберите pack"
fi

ZIP="${ROOT}/build/love-tester-store-upload.zip"
if [[ -f "${ZIP}" && "$(wc -c <"${ZIP}" | tr -d ' ')" -ge 1000000 ]]; then
  pass "love-tester-store-upload.zip"
else
  warn "ZIP — ./gradlew zipStoreUploadLoveTest"
fi

privacy=""
if [[ -f gradle.properties ]]; then
  privacy="$(grep -E '^lovetest\.privacy\.policy\.url=' gradle.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
fi
if [[ -n "${privacy}" && "${privacy}" != https://example.com/privacy ]]; then
  pass "privacy URL ${privacy}"
else
  warn "privacy URL — placeholder; ./scripts/set_privacy_url.sh"
fi

python3 - <<'PY'
import struct
from pathlib import Path

root = Path("docs/screenshots")
real = placeholder = 0
for p in root.rglob("*.png"):
    head = p.read_bytes()[:24]
    if len(head) < 24 or head[:8] != b"\x89PNG\r\n\x1a\n":
        continue
    w, h = struct.unpack(">II", head[16:24])
    size = p.stat().st_size
    if w == 1080 and h >= 1920 and size >= 32_000:
        real += 1
    elif w == 1080 and h == 1920 and size < 32_000:
        placeholder += 1
print(f"store_png_real={real}")
print(f"store_png_placeholder={placeholder}")
PY

REAL="$(python3 - <<'PY'
import struct
from pathlib import Path
n = 0
for p in Path("docs/screenshots").rglob("*.png"):
    head = p.read_bytes()[:24]
    if len(head) < 24 or head[:8] != b"\x89PNG\r\n\x1a\n":
        continue
    w, h = struct.unpack(">II", head[16:24])
    if w == 1080 and h >= 1920 and p.stat().st_size >= 32_000:
        n += 1
print(n)
PY
)"
if [[ "${REAL}" -ge 8 ]]; then
  pass "Store PNG real=${REAL} (≥8 для листинга)"
else
  warn "Store PNG real=${REAL} — ./scripts/capture_store_local.sh both priority"
fi

if [[ "${FAIL}" -ne 0 ]]; then
  echo "validate_store_upload: FAIL"
  exit 1
fi
echo "validate_store_upload: OK (см. WARN выше)"
