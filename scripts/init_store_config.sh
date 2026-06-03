#!/usr/bin/env bash
# Локальная подготовка конфигов для Play (без секретов в git).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== init_store_config ==="

copy_if_missing() {
  local src="$1"
  local dst="$2"
  if [[ -f "${dst}" ]]; then
    echo "  skip ${dst} (уже есть)"
  else
    cp "${src}" "${dst}"
    echo "  created ${dst} из ${src}"
  fi
}

copy_if_missing gradle.properties.example gradle.properties
copy_if_missing keystore.properties.example keystore.properties

needs_debug_keystore=false
if [[ -f keystore.properties ]]; then
  if grep -qE 'storeFile=/absolute/path|your-store-password|your-key-password' keystore.properties; then
    needs_debug_keystore=true
  elif grep -qE '^storeFile=' keystore.properties; then
    store_path="$(grep -E '^storeFile=' keystore.properties | cut -d= -f2- | tr -d ' ')"
    if [[ -n "${store_path}" && ! -f "${store_path}" ]]; then
      needs_debug_keystore=true
    fi
  fi
fi
if [[ "${needs_debug_keystore}" == true ]]; then
  bash scripts/generate_debug_upload_keystore.sh
fi

if [[ -f gradle.properties ]] && ! grep -qE '^lovetest\.' gradle.properties; then
  echo "" >> gradle.properties
  echo "# Store / release (см. gradle.properties.example)" >> gradle.properties
  grep -E '^lovetest\.' gradle.properties.example >> gradle.properties || true
  echo "  appended lovetest.* в gradle.properties — укажите privacy URL"
fi

echo ""
echo "Дальше:"
echo "  ./scripts/project_health.sh"
echo "  ./scripts/play_console_next.sh"
echo "  1. gradle.properties → lovetest.privacy.policy.url=https://USER.github.io/REPO/"
echo "     ./scripts/export_privacy_for_hosting.sh  # GitHub Pages"
echo "  2. Production keystore — ./scripts/generate_upload_keystore.sh"
echo "  3. ./gradlew verifyLoveTestBeforeStore && ./gradlew bundleReleaseLoveTest"
echo "  4. Store PNG — ✅ 67/67 (см. docs/screenshots/)"
