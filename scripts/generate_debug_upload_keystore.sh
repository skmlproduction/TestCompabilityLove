#!/usr/bin/env bash
# Локальный upload keystore для тестовой release-сборки (НЕ для Play Production).
# Создаёт build/keystore/lovetest-upload-debug.jks и обновляет keystore.properties.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

KEYSTORE_DIR="${ROOT}/build/keystore"
KEYSTORE_FILE="${KEYSTORE_DIR}/lovetest-upload-debug.jks"
PROPS="${ROOT}/keystore.properties"
REL_PATH="build/keystore/lovetest-upload-debug.jks"

STORE_PASS="${LOVETEST_KEYSTORE_PASS:-android}"
KEY_ALIAS="${LOVETEST_KEY_ALIAS:-upload}"
KEY_PASS="${LOVETEST_KEY_PASS:-android}"

echo "=== generate_debug_upload_keystore ==="

if ! command -v keytool >/dev/null 2>&1; then
  echo "ERROR: keytool не найден (нужен JDK)" >&2
  exit 1
fi

mkdir -p "${KEYSTORE_DIR}"

if [[ ! -f "${KEYSTORE_FILE}" ]]; then
  keytool -genkeypair -v \
    -keystore "${KEYSTORE_FILE}" \
    -alias "${KEY_ALIAS}" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -storepass "${STORE_PASS}" \
    -keypass "${KEY_PASS}" \
    -dname "CN=Love Tester Debug Upload, OU=Dev, O=dev.lovetest.app, L=Local, ST=Dev, C=US"
  echo "  created ${REL_PATH}"
else
  echo "  skip keystore (уже есть ${REL_PATH})"
fi

cat > "${PROPS}" <<EOF
# Локальный debug upload key — только для smoke-test bundleRelease.
# Для Play Console создайте отдельный upload key и замените этот файл.
storeFile=${REL_PATH}
storePassword=${STORE_PASS}
keyAlias=${KEY_ALIAS}
keyPassword=${KEY_PASS}
EOF

echo "  wrote keystore.properties → ${REL_PATH}"
echo ""
echo "Smoke-test:"
echo "  ./gradlew bundleReleaseLoveTest"
echo ""
echo "Play Console: замените keystore.properties на production upload key."
