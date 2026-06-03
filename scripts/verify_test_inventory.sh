#!/usr/bin/env bash
# Минимальный gate: unit / androidTest tests не ниже порога (без эмулятора).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

MIN_UNIT="${MIN_UNIT:-30}"
MIN_ANDROID_TEST="${MIN_ANDROID_TEST:-45}"
MIN_COMPOSE_UI="${MIN_COMPOSE_UI:-40}"
MIN_ROUTE_SMOKE="${MIN_ROUTE_SMOKE:-4}"

unit=0
android_test=0
compose_ui=0
route_smoke=0
compose_classes=0
summary=""

while IFS='=' read -r key val; do
  case "${key}" in
    unit) unit="${val}" ;;
    android_test) android_test="${val}" ;;
    compose_ui) compose_ui="${val}" ;;
    route_smoke) route_smoke="${val}" ;;
    compose_classes) compose_classes="${val}" ;;
    summary) summary="${val}" ;;
    compose) android_test="${val}" ;; # backward compat
  esac
done < <(bash scripts/count_tests.sh)

fail=false
if (( unit < MIN_UNIT )); then
  echo "ERROR: unit tests ${unit} < ${MIN_UNIT}" >&2
  fail=true
fi
if (( android_test < MIN_ANDROID_TEST )); then
  echo "ERROR: instrumented tests ${android_test} < ${MIN_ANDROID_TEST}" >&2
  fail=true
fi
if (( compose_ui < MIN_COMPOSE_UI )); then
  echo "ERROR: Compose UI tests ${compose_ui} < ${MIN_COMPOSE_UI}" >&2
  fail=true
fi
if (( route_smoke < MIN_ROUTE_SMOKE )); then
  echo "ERROR: route smoke tests ${route_smoke} < ${MIN_ROUTE_SMOKE}" >&2
  fail=true
fi

if [[ "${fail}" == true ]]; then
  exit 1
fi

echo "verify_test_inventory: OK (${summary}, ${compose_classes} ComposeTest classes)"
