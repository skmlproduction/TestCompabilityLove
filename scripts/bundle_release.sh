#!/usr/bin/env bash
# Сборка подписанного AAB (нужны keystore.properties + privacy URL для полного gate).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== bundle_release ==="

store_ok=false
if [[ -f keystore.properties ]]; then
  store_path="$(grep -E '^storeFile=' keystore.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
  if [[ -n "${store_path}" && -f "${store_path}" ]]; then
    store_ok=true
  fi
fi
if [[ "${store_ok}" != true ]]; then
  echo "WARN: keystore.properties без валидного storeFile — AAB будет unsigned"
  echo "      ./scripts/generate_debug_upload_keystore.sh  # smoke-test"
fi

./gradlew verifyLoveTest -q
./gradlew bundleRelease

AAB="$(ls -1 app/build/outputs/bundle/release/*.aab 2>/dev/null | head -1 || true)"
if [[ -n "${AAB}" ]]; then
  echo "OK: ${AAB} ($(wc -c <"${AAB}" | tr -d ' ') bytes)"
  MAPPING="app/build/outputs/mapping/release/mapping.txt"
  if [[ -f "${MAPPING}" ]]; then
    echo "Mapping: ${MAPPING}"
  fi
else
  echo "ERROR: AAB не найден" >&2
  exit 1
fi
