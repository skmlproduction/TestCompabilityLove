#!/usr/bin/env bash
# Задать lovetest.privacy.policy.url в gradle.properties.
# Usage: ./scripts/set_privacy_url.sh https://user.github.io/lovetest-privacy/
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

URL="${1:-}"
if [[ -z "${URL}" ]]; then
  echo "Usage: $0 https://your-domain/privacy" >&2
  exit 1
fi
if [[ "${URL}" != https://* ]]; then
  echo "ERROR: URL должен начинаться с https://" >&2
  exit 1
fi
if [[ "${URL}" == https://example.com/privacy ]]; then
  echo "ERROR: укажите реальный URL, не example.com" >&2
  exit 1
fi

PROPS="gradle.properties"
touch "${PROPS}"
if grep -qE '^lovetest\.privacy\.policy\.url=' "${PROPS}"; then
  if [[ "$(uname)" == Darwin ]]; then
    sed -i '' "s|^lovetest\.privacy\.policy\.url=.*|lovetest.privacy.policy.url=${URL}|" "${PROPS}"
  else
    sed -i "s|^lovetest\.privacy\.policy\.url=.*|lovetest.privacy.policy.url=${URL}|" "${PROPS}"
  fi
else
  echo "lovetest.privacy.policy.url=${URL}" >> "${PROPS}"
fi

echo "OK: lovetest.privacy.policy.url=${URL}"
echo "Проверка после деплоя: ./scripts/check_privacy_url.sh ${URL}"
echo "Пересборка: ./gradlew bundleReleaseLoveTest"
