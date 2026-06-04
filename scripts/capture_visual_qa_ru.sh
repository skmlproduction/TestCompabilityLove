#!/usr/bin/env bash
# Пересъёмка для проверки визуальных фиксов (промпт 2/7).
# Требует: adb + устройство/эмулятор 1080×1920, API 34+, RU locale, debug APK.
#
# Проверяемые баги:
#   1) Нет двойной серой шапки «Love Tester» (NoActionBar + один Compose top bar)
#   2) RU текст в hero-карточках (Victory/Letters/Pair input) не обрезается
#   3) «Назад» в Settings не под статус-баром/часами
#   4) Низкий % — серый LoveResultMutedHeroBrush на всех result-экранах
#
# Usage:
#   ./scripts/setup_android_sdk.sh          # если adb не в PATH
#   ./scripts/start_capture_emulator.sh     # при необходимости
#   ./scripts/capture_visual_qa_ru.sh
#   ./scripts/capture_visual_qa_ru.sh --check-only   # только чеклист в stdout
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

CHECK_ONLY=false
if [[ "${1:-}" == "--check-only" ]]; then
  CHECK_ONLY=true
fi

OUT_DIR="${ROOT}/docs/screenshots/qa/ru"
PKG="dev.lovetest.app"
ACTIVITY="${PKG}/.debug.DebugUiPreviewActivity"
EXTRA_PREVIEW="lovetest.intent.extra.DEBUG_UI_PREVIEW"
WAIT_DEFAULT=2.5

# screen_id → что смотреть глазами
declare -a SCREENS=(
  "splash_brand|Splash: одна шапка, без серой полосы ActionBar"
  "hub_main|Hub: app_name один раз, отступ от status bar"
  "onboarding_welcome|Onboarding стр.1: карточки RU, без обрезки"
  "onboarding_tests|Onboarding стр.2: сетка тестов, длинные подписи"
  "onboarding_protocol|Onboarding стр.3: protocol hero"
  "settings_main|Settings: «Назад» ниже status bar"
  "pair_input|Pair hero: RU заголовок/подпись"
  "letters_input|Letters hero: RU body не налезает"
  "victory_input|Victory hero: RU title"
  "love_test_result_low|Love test: серый muted hero (<50%)"
  "protocol_result_low|Protocol: серый muted hero"
  "pair_result_low|Pair: серый muted hero"
  "letters_result_low|Letters: серый muted hero"
  "victory_result_low|Victory: серый muted hero"
  "calculator_result_low|Calculator: серый muted hero"
  "love_test_result|Love test: цветной hero (контраст с low)"
  "premium_paywall|Premium paywall"
)

print_checklist() {
  echo "=== Visual QA checklist (RU) ==="
  echo "См. также docs/screenshots/CAPTURE_CHECKLIST.md"
  echo ""
  for entry in "${SCREENS[@]}"; do
    sid="${entry%%|*}"
    note="${entry#*|}"
    printf "  [ ] %-28s %s\n" "${sid}" "${note}"
  done
  echo ""
  echo "Ручной cold start (без DEBUG preview), если нужно:"
  echo "  - Пройти onboarding → hub → Settings → «Назад»"
  echo "  - Love test: Anna+Max → низкий % → сравнить с protocol/pair/letters/victory"
}

if $CHECK_ONLY; then
  print_checklist
  exit 0
fi

if ! command -v adb >/dev/null 2>&1; then
  echo "adb not found — ./scripts/setup_android_sdk.sh" >&2
  print_checklist
  exit 1
fi

if ! adb get-state >/dev/null 2>&1; then
  echo "No adb device — ./scripts/start_capture_emulator.sh" >&2
  print_checklist
  exit 1
fi

mkdir -p "${OUT_DIR}"
echo "=== capture_visual_qa_ru → ${OUT_DIR} ==="
print_checklist
echo ""
echo "Installing debug APK…"
(cd "${ROOT}" && ./gradlew :app:installDebug -q)

adb shell cmd locale set-app-locales "${PKG}" --locales ru-RU 2>/dev/null || true

FAIL=0
OK=0
for entry in "${SCREENS[@]}"; do
  sid="${entry%%|*}"
  out="${OUT_DIR}/${sid}.png"
  case "${sid}" in
    splash_brand) wait=3.0 ;;
    settings_main|premium_paywall) wait=2.0 ;;
    letters_input|letters_result*) wait=2.0 ;;
    *) wait="${WAIT_DEFAULT}" ;;
  esac
  adb shell am force-stop "${PKG}" >/dev/null 2>&1 || true
  if ! adb shell am start -n "${ACTIVITY}" --es "${EXTRA_PREVIEW}" "${sid}" -W >/dev/null; then
    echo "FAIL start: ${sid}" >&2
    FAIL=$((FAIL + 1))
    continue
  fi
  sleep "${wait}"
  if adb exec-out screencap -p > "${out}"; then
    bytes=$(wc -c <"${out}" | tr -d ' ')
    echo "OK  ${sid} (${bytes} bytes) → ${out#${ROOT}/}"
    OK=$((OK + 1))
  else
    echo "FAIL screencap: ${sid}" >&2
    FAIL=$((FAIL + 1))
  fi
done

echo ""
echo "capture_visual_qa_ru: ${OK} ok, ${FAIL} failed"
echo "Откройте PNG в ${OUT_DIR#${ROOT}/} и отметьте чеклист выше."
[[ "${FAIL}" -eq 0 ]]
