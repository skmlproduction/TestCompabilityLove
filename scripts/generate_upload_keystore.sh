#!/usr/bin/env bash
# Создаёт production upload keystore для Play Console (НЕ коммитить).
# Usage:
#   LOVETEST_KEYSTORE_PASS='...' LOVETEST_KEY_PASS='...' ./scripts/generate_upload_keystore.sh
#   LOVETEST_KEY_ALIAS=upload ./scripts/generate_upload_keystore.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

KEYSTORE_DIR="${ROOT}/build/keystore"
KEYSTORE_FILE="${KEYSTORE_DIR}/lovetest-upload.jks"
PROPS="${ROOT}/keystore.properties"
REL_PATH="build/keystore/lovetest-upload.jks"

STORE_PASS="${LOVETEST_KEYSTORE_PASS:-}"
KEY_ALIAS="${LOVETEST_KEY_ALIAS:-upload}"
KEY_PASS="${LOVETEST_KEY_PASS:-${STORE_PASS}}"

echo "=== generate_upload_keystore (Play upload key) ==="

if [[ -z "${STORE_PASS}" ]]; then
  echo "ERROR: задайте LOVETEST_KEYSTORE_PASS (и опционально LOVETEST_KEY_PASS)" >&2
  echo "Пример:" >&2
  echo "  LOVETEST_KEYSTORE_PASS='strong-pass' LOVETEST_KEY_PASS='strong-pass' $0" >&2
  exit 1
fi

if ! command -v keytool >/dev/null 2>&1; then
  echo "ERROR: keytool не найден (нужен JDK)" >&2
  exit 1
fi

if [[ -f "${KEYSTORE_FILE}" ]]; then
  echo "ERROR: ${REL_PATH} уже существует — удалите вручную, если нужен новый ключ" >&2
  exit 1
fi

mkdir -p "${KEYSTORE_DIR}"
keytool -genkeypair -v \
  -keystore "${KEYSTORE_FILE}" \
  -alias "${KEY_ALIAS}" \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass "${STORE_PASS}" \
  -keypass "${KEY_PASS}" \
  -dname "CN=Love Tester, OU=Mobile, O=dev.lovetest.app, L=Unknown, ST=Unknown, C=US"

cat > "${PROPS}" <<EOF
# Play Console upload key — храните backup в безопасном месте.
storeFile=${REL_PATH}
storePassword=${STORE_PASS}
keyAlias=${KEY_ALIAS}
keyPassword=${KEY_PASS}
EOF

echo "  created ${REL_PATH}"
echo "  wrote keystore.properties"
echo ""
echo "ВАЖНО:"
echo "  • Сохраните backup ${REL_PATH} — потеря ключа = нельзя обновлять приложение"
echo "  • Play App Signing: загрузите AAB, Google хранит app signing key"
echo ""
echo "Дальше:"
echo "  ./gradlew bundleReleaseLoveTest"
echo "  ./scripts/finalize_store_release.sh"
