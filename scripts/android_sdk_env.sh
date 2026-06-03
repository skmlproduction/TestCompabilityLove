#!/usr/bin/env bash
# Общий PATH для Android SDK (source из других скриптов).
# Usage: source scripts/android_sdk_env.sh

_android_sdk_root() {
  if [[ -n "${ANDROID_HOME:-}" && -d "${ANDROID_HOME}" ]]; then
    echo "${ANDROID_HOME}"
    return 0
  fi
  if [[ -n "${ANDROID_SDK_ROOT:-}" && -d "${ANDROID_SDK_ROOT}" ]]; then
    echo "${ANDROID_SDK_ROOT}"
    return 0
  fi
  for candidate in \
    "${HOME}/Library/Android/sdk" \
    "${HOME}/Android/Sdk" \
    "/opt/android-sdk"; do
    if [[ -d "${candidate}" ]]; then
      echo "${candidate}"
      return 0
    fi
  done
  return 1
}

if SDK_ROOT="$(_android_sdk_root)"; then
  export ANDROID_HOME="${SDK_ROOT}"
  export ANDROID_SDK_ROOT="${SDK_ROOT}"
  export PATH="${SDK_ROOT}/platform-tools:${SDK_ROOT}/emulator:${SDK_ROOT}/cmdline-tools/latest/bin:${PATH}"
fi

android_sdk_adb() {
  if command -v adb >/dev/null 2>&1; then
    command adb "$@"
    return $?
  fi
  return 127
}
