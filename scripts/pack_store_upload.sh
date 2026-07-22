#!/usr/bin/env bash
# Собрать артефакты для ручной загрузки в Play Console.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

OUT="${ROOT}/build/store-upload"
mkdir -p "${OUT}"

echo "=== pack_store_upload ==="

copy_if_exists() {
  local src="$1"
  local dst="$2"
  if [[ -f "${src}" ]]; then
    cp "${src}" "${dst}"
    echo "  + $(basename "${dst}")"
  else
    echo "  skip $(basename "${dst}") — нет ${src}"
  fi
}

AAB="$(ls -1 app/build/outputs/bundle/release/*.aab 2>/dev/null | head -1 || true)"
if [[ -n "${AAB}" ]]; then
  cp "${AAB}" "${OUT}/"
  echo "  + $(basename "${AAB}")"
else
  echo "  skip AAB — ./gradlew bundleReleaseLoveTest"
fi

if [[ ! -f build/legal-host/index.html ]]; then
  bash scripts/export_privacy_for_hosting.sh >/dev/null
fi

copy_if_exists "app/build/outputs/mapping/release/mapping.txt" "${OUT}/mapping.txt"
copy_if_exists "docs/store/feature_graphic.png" "${OUT}/feature_graphic.png"
copy_if_exists "build/legal-host/index.html" "${OUT}/privacy_policy.html"
copy_if_exists "docs/store/RELEASE_NOTES_v1.0.0.md" "${OUT}/RELEASE_NOTES_v1.0.0.md"
copy_if_exists "docs/store/PLAY_CONSOLE_COPY.md" "${OUT}/PLAY_CONSOLE_COPY.md"
copy_if_exists "docs/store/DATA_SAFETY_FORM.md" "${OUT}/DATA_SAFETY_FORM.md"
copy_if_exists "docs/store/PRIVACY_HOSTING.md" "${OUT}/PRIVACY_HOSTING.md"
copy_if_exists "docs/store/INTERNAL_TESTING.md" "${OUT}/INTERNAL_TESTING.md"
copy_if_exists "docs/store/PLAY_READY.md" "${OUT}/PLAY_READY.md"
copy_if_exists "docs/store/PLAY_FORMS_FILLED.md" "${OUT}/PLAY_FORMS_FILLED.md"
copy_if_exists "docs/store/INTERNAL_TESTING_RUNBOOK.md" "${OUT}/INTERNAL_TESTING_RUNBOOK.md"

LISTING=(
  hub_main
  love_test_input
  love_test_result
  protocol_input
  protocol_result
  wheel_spin
  premium_paywall
)
for loc in ru en; do
  dest_dir="${OUT}/listing-screenshots/${loc}"
  mkdir -p "${dest_dir}"
  copied=0
  for sid in "${LISTING[@]}"; do
    src="docs/screenshots/${loc}/${sid}.png"
    if [[ -f "${src}" ]]; then
      cp "${src}" "${dest_dir}/${sid}.png"
      copied=$((copied + 1))
    fi
  done
  echo "  + listing-screenshots/${loc}/ (${copied} PNG)"
done

cat > "${OUT}/UPLOAD_README.txt" <<'EOF'
Love Tester — пакет для Play Console

1. AAB → Release → Create new release → Upload
2. mapping.txt → App bundle explorer → Deobfuscation file
3. feature_graphic.png → Store listing → Feature graphic (1024x500)
4. privacy_policy.html → хостинг HTTPS + URL в Console и gradle.properties
5. PLAY_CONSOLE_COPY.md / DATA_SAFETY_FORM.md — тексты листинга и Data safety
6. listing-screenshots/ru|en/ — 7 приоритетных PNG для Store listing
7. PRIVACY_HOSTING.md / INTERNAL_TESTING.md — инструкции

Проверка: ./scripts/print_store_checklist.sh
Финальный gate: ./scripts/finalize_store_release.sh
EOF

python3 - <<'PY'
import os
from pathlib import Path

out = Path("build/store-upload")
lines = ["Love Tester — UPLOAD_MANIFEST", ""]
total = 0
for path in sorted(out.rglob("*")):
    if path.is_file():
        size = path.stat().st_size
        total += size
        rel = path.relative_to(out)
        lines.append(f"{size:>12}  {rel}")
lines.append("")
lines.append(f"{'TOTAL':>12}  {total} bytes")
Path(out / "UPLOAD_MANIFEST.txt").write_text("\n".join(lines) + "\n", encoding="utf-8")
print("  + UPLOAD_MANIFEST.txt")
PY

bash scripts/zip_store_upload.sh 2>/dev/null || true

echo ""
echo "OK: ${OUT}/"
ls -la "${OUT}"
if [[ -f build/love-tester-store-upload.zip ]]; then
  echo "ZIP: build/love-tester-store-upload.zip"
fi
