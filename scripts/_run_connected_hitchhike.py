#!/usr/bin/env python3
"""Run critical connected tests on whatever emulator becomes healthy (hitchhike)."""
from __future__ import annotations

import os
import re
import signal
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOG = Path("/tmp/lt-hitch.log")
STATUS = Path("/tmp/lt-hitch-status.txt")
SDK = Path.home() / "Library/Android/sdk"
ADB = SDK / "platform-tools" / "adb"
CLASSES = ",".join(
    [
        "dev.lovetest.app.a11y.CriticalFlowsA11yComposeTest",
        "dev.lovetest.app.navigation.LoveTestFlowComposeTest",
        "dev.lovetest.app.navigation.CalculatorFlowComposeTest",
        "dev.lovetest.app.navigation.ProtocolFlowComposeTest",
        "dev.lovetest.app.navigation.WheelFlowComposeTest",
        "dev.lovetest.app.navigation.ZodiacFlowComposeTest",
        "dev.lovetest.app.navigation.MissingSessionRedirectComposeTest",
        "dev.lovetest.app.ui.consent.ConsentScreenComposeTest",
        "dev.lovetest.app.ui.features.VictoryResultScreenComposeTest",
        "dev.lovetest.app.ui.share.ShareCardImageExporterInstrumentedTest",
        "dev.lovetest.app.DebugStartRouteInstrumentedTest",
    ]
)


def log(msg: str) -> None:
    with LOG.open("a", encoding="utf-8") as f:
        f.write(msg + "\n")
    print(msg, flush=True)


def set_status(s: str) -> None:
    STATUS.write_text(s + "\n", encoding="utf-8")


def adb_global(*args: str, timeout: float = 10.0) -> str:
    try:
        return subprocess.check_output(
            [str(ADB), *args], text=True, errors="replace", timeout=timeout
        ).strip()
    except Exception:
        return ""


def adb_s(serial: str, *args: str, timeout: float = 10.0) -> str:
    try:
        return subprocess.check_output(
            [str(ADB), "-s", serial, *args],
            text=True,
            errors="replace",
            timeout=timeout,
        ).strip()
    except Exception:
        return ""


def list_emulator_serials() -> list[str]:
    out = adb_global("devices")
    serials = []
    for line in out.splitlines()[1:]:
        parts = line.split()
        if len(parts) >= 2 and parts[0].startswith("emulator-") and parts[1] == "device":
            serials.append(parts[0])
    return serials


def kill_offline_emulators() -> None:
    """Nudge sibling agent to respawn a clean AVD if stuck offline."""
    out = adb_global("devices")
    for line in out.splitlines()[1:]:
        parts = line.split()
        if len(parts) >= 2 and parts[0].startswith("emulator-") and parts[1] == "offline":
            log(f"offline {parts[0]} — kill matching qemu to force respawn")
            # kill all qemu; sibling will restart RegSnap
            try:
                ps = subprocess.check_output(
                    ["ps", "-ax", "-o", "pid=,command="], text=True, errors="replace"
                )
                for pl in ps.splitlines():
                    if "/qemu-system-" in pl and "/bin/zsh" not in pl:
                        try:
                            os.kill(int(pl.split(None, 1)[0]), signal.SIGKILL)
                            log(f"kill_qemu {pl.split(None,1)[0]}")
                        except Exception:
                            pass
            except Exception as e:
                log(f"kill_err {e}")


def main() -> int:
    LOG.write_text("", encoding="utf-8")
    set_status("waiting")
    os.chdir(ROOT)
    log(f"=== {time.strftime('%Y-%m-%dT%H:%M:%S')} hitchhike pid={os.getpid()} ===")

    subprocess.run([str(ADB), "start-server"], check=False, capture_output=True)
    kill_offline_emulators()

    serial = None
    for i in range(1, 90):
        serials = list_emulator_serials()
        log(f"iter={i} devices={serials!r} raw={adb_global('devices')!r}")
        for s in serials:
            boot = adb_s(s, "shell", "getprop", "sys.boot_completed", timeout=6.0)
            if boot == "1":
                serial = s
                break
        if serial:
            break
        # if nothing online for a while, poke again
        if i in (15, 30, 45):
            kill_offline_emulators()
        time.sleep(4)

    if not serial:
        log("NO_DEVICE")
        set_status("no_device")
        return 3

    log(f"using {serial}")
    set_status(f"testing {serial}")
    adb_s(serial, "shell", "settings", "put", "global", "animator_duration_scale", "0")
    adb_s(serial, "shell", "settings", "put", "global", "window_animation_scale", "0")
    adb_s(serial, "shell", "settings", "put", "global", "transition_animation_scale", "0")
    adb_s(serial, "shell", "cmd", "locale", "set-app-locales", "dev.lovetest.app", "--locales", "")
    adb_s(serial, "shell", "pm", "uninstall", "com.hyperlockscreen.app")
    log(f"wm={adb_s(serial, 'shell', 'wm', 'size')}")

    env = os.environ.copy()
    env["PATH"] = f"{SDK / 'emulator'}:{SDK / 'platform-tools'}:{env.get('PATH', '')}"
    env["ANDROID_SERIAL"] = serial
    env["LOVETEST_ADB_SERIAL"] = serial

    r = subprocess.run(
        [
            "./gradlew",
            ":app:connectedDebugAndroidTest",
            f"-Pandroid.testInstrumentationRunnerArguments.class={CLASSES}",
        ],
        cwd=ROOT,
        env=env,
        stdout=LOG.open("a"),
        stderr=subprocess.STDOUT,
    )
    log(f"critical_exit={r.returncode}")
    set_status(f"done exit={r.returncode} serial={serial}")
    return r.returncode


if __name__ == "__main__":
    sys.exit(main())
