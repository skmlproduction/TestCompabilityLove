#!/usr/bin/env bash
# Финальный gate перед загрузкой в Play Console.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

echo "=== finalize_store_release ==="
echo ""

FAIL=0

run_step() {
  local label="$1"
  shift
  echo "→ ${label}"
  if "$@"; then
    echo "  OK"
  else
    echo "  FAIL: ${label}" >&2
    FAIL=1
  fi
  echo ""
}

run_step "verifyLoveTest" ./gradlew verifyLoveTest -q
run_step "verifyLoveTestBeforeStore (PNG gate)" ./gradlew verifyLoveTestBeforeStore -q
run_step "audit P0" python3 scripts/audit_screens_matrix.py --write docs/product/AUDIT_REPORT.md
run_step "export privacy for hosting" bash scripts/export_privacy_for_hosting.sh
run_step "release gate" bash scripts/release_gate.sh

if [[ -f keystore.properties ]]; then
  store_path="$(grep -E '^storeFile=' keystore.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
  if [[ -n "${store_path}" && -f "${store_path}" ]]; then
    run_step "bundleRelease" ./gradlew bundleRelease -q
  else
    echo "→ skip bundleRelease (keystore storeFile invalid)"
    echo ""
  fi
else
  echo "→ skip bundleRelease (no keystore.properties)"
  echo ""
fi

run_step "pack store upload" bash scripts/pack_store_upload.sh
run_step "validate store upload" bash scripts/validate_store_upload.sh

privacy=""
if [[ -f gradle.properties ]]; then
  privacy="$(grep -E '^lovetest\.privacy\.policy\.url=' gradle.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
fi

echo "=== Итог ==="
if [[ -f scripts/count_tests.sh ]]; then
  echo "Tests: $(bash scripts/count_tests.sh | grep '^summary=' | cut -d= -f2-)"
fi
if [[ "${FAIL}" -ne 0 ]]; then
  echo "finalize_store_release: FAIL — исправьте шаги выше"
  exit 1
fi

if [[ "${privacy}" == https://example.com/privacy || -z "${privacy}" ]]; then
  echo "finalize_store_release: код и PNG OK"
  echo ""
  echo "Осталось для Play:"
  echo "  1. Privacy URL — docs/store/PRIVACY_HOSTING.md"
  echo "     ./scripts/set_privacy_url.sh https://USER.github.io/REPO/"
  echo "  2. Production upload key в keystore.properties"
  echo "  3. ./gradlew bundleReleaseLoveTest && ./scripts/finalize_store_release.sh"
  echo "  4. Upload build/store-upload/ — docs/store/INTERNAL_TESTING.md"
  exit 0
fi

echo "finalize_store_release: READY — загрузите build/store-upload/"
if [[ -f build/love-tester-store-upload.zip ]]; then
  echo "  ZIP: build/love-tester-store-upload.zip"
fi
echo "  docs/store/INTERNAL_TESTING.md"
