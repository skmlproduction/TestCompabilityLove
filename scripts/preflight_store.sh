#!/usr/bin/env bash
# Проверки перед загрузкой в Play (без сборки).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== preflight_store ==="

FAIL=0

privacy=""
if [[ -f gradle.properties ]]; then
  privacy="$(grep -E '^lovetest\.privacy\.policy\.url=' gradle.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
fi
if [[ -z "${privacy}" || "${privacy}" == \#* ]]; then
  if [[ -f app/src/main/assets/legal/privacy_policy.html ]]; then
    echo "WARN: lovetest.privacy.policy.url не задан — для Play укажите публичный URL"
    echo "      ./scripts/init_store_config.sh  # добавит lovetest.* в gradle.properties"
    echo "      ./scripts/export_privacy_for_hosting.sh → GitHub Pages"
  else
    echo "ERROR: нет privacy URL и нет assets/legal/privacy_policy.html"
    FAIL=1
  fi
elif [[ "${privacy}" == https://example.com/privacy ]]; then
  echo "WARN: privacy URL — placeholder example.com; укажите реальный HTTPS"
  echo "      ./scripts/export_privacy_for_hosting.sh → GitHub Pages"
else
  echo "OK: privacy URL задан (${privacy})"
fi

read -r catalog_total catalog_missing catalog_placeholder catalog_real <<<"$(python3 - <<'PY'
import csv
import struct
from pathlib import Path

root = Path(".")
required: list[Path] = []
with (root / "docs/product/screens_catalog.csv").open(encoding="utf-8", newline="") as f:
    for row in csv.DictReader(f):
        for key in ("screenshot_ru_relative", "screenshot_en_relative"):
            raw = (row.get(key) or "").strip()
            if not raw or "N/A" in raw.upper():
                continue
            required.append(root / raw)

missing = 0
placeholder = 0
real = 0
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

print(len(required), missing, placeholder, real)
PY
)"

echo "Screenshots (каталог): всего ${catalog_total}, на диске $((catalog_total - catalog_missing)), реальных ≥32KB: ${catalog_real}, placeholder: ${catalog_placeholder}, отсутствуют: ${catalog_missing}"

if [[ "${catalog_missing}" -gt 0 ]]; then
  echo "WARN: не все PNG из screens_catalog.csv — запустите: ./gradlew materializeScreenshotPlaceholders"
fi
if [[ "${catalog_placeholder}" -gt 0 ]]; then
  echo "WARN: placeholder PNG: ${catalog_placeholder} — съёмка: ./gradlew captureScreenshotCatalogRu && captureScreenshotCatalogEn"
fi
if [[ "${catalog_real}" -eq "${catalog_total}" && "${catalog_total}" -gt 0 ]]; then
  echo "OK: все скриншоты каталога выглядят как реальные Store PNG"
fi

if [[ -f docs/store/feature_graphic.png ]]; then
  fg_bytes="$(wc -c <docs/store/feature_graphic.png | tr -d ' ')"
  if [[ "${fg_bytes}" -ge 50000 ]]; then
    echo "OK: feature_graphic.png (${fg_bytes} B)"
  else
    echo "WARN: feature_graphic.png слишком мал — ./scripts/export_feature_graphic.sh"
  fi
else
  echo "INFO: feature_graphic.png нет — ./gradlew exportFeatureGraphic"
fi

if [[ -f keystore.properties ]]; then
  store_path="$(grep -E '^storeFile=' keystore.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
  if [[ -n "${store_path}" && -f "${store_path}" ]]; then
    echo "OK: keystore.properties → ${store_path}"
  else
    echo "WARN: keystore.properties без валидного storeFile"
    echo "      ./scripts/generate_debug_upload_keystore.sh  # smoke-test"
  fi
else
  echo "WARN: keystore.properties нет — release AAB без upload-подписи (см. keystore.properties.example)"
fi

screen_count="$(python3 - <<'PY'
import csv
from pathlib import Path
with (Path("docs/product/screens_catalog.csv")).open(encoding="utf-8", newline="") as f:
    print(sum(1 for _ in csv.DictReader(f)))
PY
)"
echo "Каталог screen_id: ${screen_count} (MVP 29 + протокол/onboarding)"

if command -v adb >/dev/null 2>&1 && adb get-state >/dev/null 2>&1; then
  echo "OK: adb device подключён — можно captureScreenshotCatalog"
else
  echo "INFO: adb/эмулятор не готов — съёмка локально позже"
fi

echo ""
echo "Далее:"
echo "  ./gradlew storeReadyLoveTest          # код + инвентарь"
echo "  ./gradlew captureScreenshotCatalogRu"
echo "  ./gradlew verifyLoveTestBeforeStore   # gate: без placeholder"
echo "  ./gradlew bundleRelease"

exit "${FAIL}"
