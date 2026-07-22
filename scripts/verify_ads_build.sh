#!/usr/bin/env bash
# Smoke-compile with ADS_ENABLED=true (closed testing / AdMob QA).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== verify_ads_build ==="
echo "Compile debug with -Plovetest.ads.enabled=true"
echo ""

./gradlew :app:assembleDebug -Plovetest.ads.enabled=true -q

merged="$(find app/build/intermediates/merged_manifest/debug -name AndroidManifest.xml 2>/dev/null | head -1 || true)"
if [[ -n "${merged}" ]] && grep -q 'com.google.android.gms.permission.AD_ID' "${merged}"; then
  echo "AD_ID permission: OK (merged manifest)"
else
  echo "WARN: AD_ID not found in merged debug manifest — проверьте app/src/ads/"
fi

echo ""
echo "verify_ads_build: OK"
