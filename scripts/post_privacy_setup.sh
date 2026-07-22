#!/usr/bin/env bash
# После деплоя privacy на HTTPS: URL → проверка → пересборка → pack.
# Usage: ./scripts/post_privacy_setup.sh https://USER.github.io/REPO/
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

URL="${1:-}"
if [[ -z "${URL}" ]]; then
  echo "Usage: $0 https://your-domain/privacy-or-root/" >&2
  echo "  ./scripts/suggest_privacy_url.sh USER REPO --apply" >&2
  exit 1
fi

echo "=== post_privacy_setup ==="
bash scripts/set_privacy_url.sh "${URL}"
bash scripts/check_privacy_url.sh "${URL}" || true
bash scripts/check_legal_urls.sh || echo "WARN: terms/data-collection — после деплоя Pages"
bash scripts/export_privacy_for_hosting.sh

if [[ -f keystore.properties ]]; then
  store_path="$(grep -E '^storeFile=' keystore.properties 2>/dev/null | cut -d= -f2- | tr -d ' ' || true)"
  if [[ -n "${store_path}" && -f "${store_path}" ]]; then
    ./gradlew bundleReleaseLoveTest -q
  else
    echo "WARN: keystore invalid — skip bundleRelease (см. generate_upload_keystore.sh)"
  fi
fi

bash scripts/finalize_store_release.sh
