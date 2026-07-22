#!/usr/bin/env bash
# Align LoveTester_Capture config.ini with snapshot emp_ready_p1 (ram/lcd).
# Snapshot load fails with "different AVD configuration" if hw.ramSize drifts.
set -euo pipefail

AVD_NAME="${LOVETEST_CAPTURE_AVD:-LoveTester_Capture}"
AVD_DIR="${HOME}/.android/avd/${AVD_NAME}.avd"
CONFIG="${AVD_DIR}/config.ini"
SNAP_HW="${AVD_DIR}/snapshots/emp_ready_p1/hardware.ini"

if [[ ! -f "${CONFIG}" ]]; then
  echo "ERROR: missing ${CONFIG}" >&2
  exit 1
fi
if [[ ! -f "${SNAP_HW}" ]]; then
  echo "ERROR: missing snapshot hardware.ini at ${SNAP_HW}" >&2
  exit 1
fi

ram="$(grep -E '^hw\.ramSize ?=' "${SNAP_HW}" | head -1 | sed -E 's/.*= ?//;s/[[:space:]]//g')"
w="$(grep -E '^hw\.lcd\.width ?=' "${SNAP_HW}" | head -1 | sed -E 's/.*= ?//;s/[[:space:]]//g')"
h="$(grep -E '^hw\.lcd\.height ?=' "${SNAP_HW}" | head -1 | sed -E 's/.*= ?//;s/[[:space:]]//g')"
dens="$(grep -E '^hw\.lcd\.density ?=' "${SNAP_HW}" | head -1 | sed -E 's/.*= ?//;s/[[:space:]]//g')"

set_config() {
  local key="$1" value="$2"
  if grep -q "^${key}=" "${CONFIG}"; then
    if [[ "$(uname)" == Darwin ]]; then
      sed -i '' "s|^${key}=.*|${key}=${value}|" "${CONFIG}"
    else
      sed -i "s|^${key}=.*|${key}=${value}|" "${CONFIG}"
    fi
  else
    echo "${key}=${value}" >> "${CONFIG}"
  fi
}

cp "${CONFIG}" "${CONFIG}.bak.align.$(date +%Y%m%d%H%M%S)"
set_config "hw.ramSize" "${ram}"
set_config "hw.lcd.width" "${w}"
set_config "hw.lcd.height" "${h}"
set_config "hw.lcd.density" "${dens}"

echo "OK: ${AVD_NAME} aligned to emp_ready_p1 → ram=${ram} lcd=${w}x${h}@${dens}"
echo "Boot: emulator -avd ${AVD_NAME} -snapshot emp_ready_p1 -no-snapshot-save"
echo "Note: guest RAM ${ram} MB — need ~$((ram + 800)) MB free host before boot."
