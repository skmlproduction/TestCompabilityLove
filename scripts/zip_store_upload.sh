#!/usr/bin/env bash
# ZIP архив build/store-upload/ для передачи в Play Console.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

SRC="${ROOT}/build/store-upload"
ZIP="${ROOT}/build/love-tester-store-upload.zip"

if [[ ! -d "${SRC}" ]]; then
  echo "ERROR: нет ${SRC} — ./scripts/pack_store_upload.sh" >&2
  exit 1
fi

rm -f "${ZIP}"
(cd "${ROOT}/build" && zip -r "love-tester-store-upload.zip" "store-upload" -x "*.DS_Store")
bytes="$(wc -c <"${ZIP}" | tr -d ' ')"
echo "zip_store_upload: ${ZIP} (${bytes} bytes)"
