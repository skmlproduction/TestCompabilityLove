#!/usr/bin/env bash
# Сводный gate перед загрузкой в Play (не заменяет verifyLoveTestBeforeStore).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== release_gate ==="
echo ""

FAIL=0
pass() { echo "  [OK]   $1"; }
warn() { echo "  [WARN] $1"; }
fail() { echo "  [FAIL] $1"; FAIL=1; }

echo "1. Код и инвентарь"
if ./gradlew verifyLoveTest --no-daemon -q; then
  pass "verifyLoveTest"
else
  fail "verifyLoveTest"
fi

echo ""
echo "2. Аудит экранов (P0)"
if python3 scripts/audit_screens_matrix.py --write docs/product/AUDIT_REPORT.md >/dev/null; then
  pass "auditLoveTestScreens (P0=0)"
else
  fail "auditLoveTestScreens — есть P0, см. docs/product/AUDIT_REPORT.md"
fi

echo ""
echo "3. Store preflight"
bash scripts/preflight_store.sh || true

privacy=""
if [[ -f gradle.properties ]]; then
  privacy="$(grep -E '^lovetest\.privacy\.policy\.url=' gradle.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
fi
if [[ -n "${privacy}" && "${privacy}" != https://example.com/privacy ]]; then
  pass "privacy URL настроен"
  if command -v curl >/dev/null 2>&1; then
    if bash scripts/check_privacy_url.sh "${privacy}" >/dev/null 2>&1; then
      pass "privacy URL reachable"
    else
      warn "privacy URL не отвечает — ./scripts/check_privacy_url.sh"
    fi
  fi
else
  warn "privacy URL — gradle.properties + ./scripts/export_privacy_for_hosting.sh"
fi

if [[ -f keystore.properties ]]; then
  store_path="$(grep -E '^storeFile=' keystore.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
  if [[ -n "${store_path}" && -f "${store_path}" ]]; then
    pass "keystore.properties → ${store_path}"
  else
    warn "keystore.properties — нет валидного storeFile; ./scripts/generate_debug_upload_keystore.sh"
  fi
else
  warn "keystore.properties — cp keystore.properties.example"
fi

if [[ -f docs/store/feature_graphic.png ]] && [[ "$(wc -c <docs/store/feature_graphic.png | tr -d ' ')" -ge 50000 ]]; then
  pass "feature_graphic.png"
else
  warn "feature_graphic — ./gradlew exportFeatureGraphic"
fi

placeholder="$(python3 - <<'PY'
import csv, struct
from pathlib import Path
root = Path(".")
n = 0
with (root / "docs/product/screens_catalog.csv").open(encoding="utf-8", newline="") as f:
    for row in csv.DictReader(f):
        for key in ("screenshot_ru_relative", "screenshot_en_relative"):
            p = root / (row.get(key) or "")
            if not p.is_file():
                continue
            head = p.read_bytes()[:24]
            if len(head) >= 24 and head[:8] == b"\x89PNG\r\n\x1a\n":
                w, h = struct.unpack(">II", head[16:24])
                if (w, h) == (1080, 1920) and p.stat().st_size < 32_000:
                    n += 1
print(n)
PY
)"
if [[ "${placeholder}" -eq 0 ]]; then
  pass "Store PNG — все реальные (${placeholder} placeholder)"
else
  warn "Store PNG — ${placeholder} placeholder; ./gradlew captureScreenshotCatalogRu/En"
fi

echo ""
echo "4. Финальный gate PNG (опционально)"
echo "     ./gradlew verifyLoveTestBeforeStore"
echo ""
echo "5. Release AAB"
echo "     ./gradlew bundleRelease"
echo ""

if [[ "${FAIL}" -ne 0 ]]; then
  echo "release_gate: FAIL — исправьте [FAIL] выше"
  exit 1
fi
echo "release_gate: код OK — осталось WARN для Play Console (см. docs/store/STORE_UPLOAD.md)"
exit 0
