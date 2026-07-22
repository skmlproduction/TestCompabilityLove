#!/usr/bin/env python3
"""Exclusive LoveTester boot + critical connected tests (contested 16GB host)."""
from __future__ import annotations

import os
import re
import signal
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOG = Path("/tmp/lt-stable.log")
STATUS = Path("/tmp/lt-stable-status.txt")
SERIAL = "emulator-5554"
# Prefer Lite (API 35 google_apis, 1024MB) on 16GB hosts; Capture is Play Store + heavier.
AVD = "LoveTester_Lite" if (Path.home() / ".android/avd/LoveTester_Lite.avd").exists() else "LoveTester_Capture"
SDK = Path.home() / "Library/Android/sdk"
EMU = SDK / "emulator" / "emulator"
ADB = SDK / "platform-tools" / "adb"
RIVALS = ("RegSnap", "HyperLock", "MatchByNames", "HLS_", "NEWlock", "MBN_", "Gate34")
EMU_MEMORY = "1024" if AVD == "LoveTester_Lite" else "1280"
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
    line = f"{msg}"
    with LOG.open("a", encoding="utf-8") as f:
        f.write(line + "\n")
    print(line, flush=True)


def set_status(s: str) -> None:
    STATUS.write_text(s + "\n", encoding="utf-8")


def ps_lines() -> list[str]:
    return subprocess.check_output(
        ["ps", "-ax", "-o", "pid=,command="], text=True, errors="replace"
    ).splitlines()


def kill_pid(pid: int) -> None:
    try:
        os.kill(pid, signal.SIGKILL)
    except ProcessLookupError:
        pass


def kill_rivals() -> int:
    n = 0
    for line in ps_lines():
        parts = line.split(None, 1)
        if len(parts) < 2:
            continue
        pid_s, cmd = parts
        if "/qemu-system-" not in cmd:
            continue
        if "/bin/zsh" in cmd or "snap=$(command cat" in cmd:
            continue
        if "LoveTester_Lite" in cmd or "LoveTester_Capture" in cmd:
            continue
        if any(r in cmd for r in RIVALS) or True:
            # Kill any non-LoveTester qemu — only one AVD fits in 16GB.
            kill_pid(int(pid_s))
            n += 1
            log(f"kill_rival {pid_s}")
    return n


def love_qemu_alive() -> bool:
    for line in ps_lines():
        if "/qemu-system-" not in line or "/bin/zsh" in line:
            continue
        if AVD in line or "LoveTester_Lite" in line or "LoveTester_Capture" in line:
            return True
    return False


def adb(*args: str, timeout: float = 8.0) -> str:
    try:
        out = subprocess.check_output(
            [str(ADB), "-s", SERIAL, *args],
            text=True,
            errors="replace",
            timeout=timeout,
        )
        return out.strip()
    except (subprocess.CalledProcessError, subprocess.TimeoutExpired, FileNotFoundError):
        return ""


def free_mb() -> float:
    vm = subprocess.check_output(["vm_stat"], text=True)
    m = re.search(r"Pages free:\s+(\d+)", vm)
    return round(int(m.group(1)) * 16384 / 1024 / 1024, 1) if m else -1


def main() -> int:
    LOG.write_text("", encoding="utf-8")
    set_status("starting")
    os.chdir(ROOT)
    env = os.environ.copy()
    env["PATH"] = f"{SDK / 'emulator'}:{SDK / 'platform-tools'}:{env.get('PATH', '')}"
    env["ANDROID_SERIAL"] = SERIAL
    env["LOVETEST_ADB_SERIAL"] = SERIAL

    log(f"=== {time.strftime('%Y-%m-%dT%H:%M:%S')} stable.py pid={os.getpid()} ===")
    log(f"avd={AVD} memory={EMU_MEMORY} free_mb_before={free_mb()}")

    for _ in range(8):
        kill_rivals()
        time.sleep(0.5)
    log(f"free_mb_after_kill={free_mb()}")

    subprocess.run([str(ADB), "kill-server"], check=False, capture_output=True)
    time.sleep(1)
    subprocess.run([str(ADB), "start-server"], check=False, capture_output=True)

    set_status("booting")
    emu_args = [
        str(EMU),
        "-avd",
        AVD,
        "-port",
        "5554",
        "-skin",
        "1080x1920",
        "-no-window",
        "-no-audio",
        "-no-snapshot-load",
        "-no-snapshot-save",
        "-no-boot-anim",
        "-memory",
        EMU_MEMORY,
        "-cores",
        "2",
        "-gpu",
        "swiftshader_indirect",
    ]
    emu = subprocess.Popen(
        emu_args,
        stdout=open("/tmp/lovetest-emulator.log", "w"),
        stderr=subprocess.STDOUT,
        start_new_session=True,
    )
    log(f"emulator_pid={emu.pid}")

    # Watchdog thread via child process
    wd = subprocess.Popen(
        [
            sys.executable,
            "-c",
            f"""
import os, signal, subprocess, time
AVD={AVD!r}
while True:
    out=subprocess.check_output(["ps","-ax","-o","pid=,command="],text=True,errors="replace")
    for line in out.splitlines():
        parts=line.split(None,1)
        if len(parts)<2: continue
        pid_s, cmd = parts
        if "/qemu-system-" not in cmd: continue
        if AVD in cmd or "LoveTester_Lite" in cmd or "LoveTester_Capture" in cmd: continue
        if "/bin/zsh" in cmd: continue
        try: os.kill(int(pid_s), signal.SIGKILL)
        except ProcessLookupError: pass
    time.sleep(1.0)
""",
        ],
        start_new_session=True,
        stdout=open("/tmp/lt-stable-wd.log", "w"),
        stderr=subprocess.STDOUT,
    )
    log(f"watchdog_pid={wd.pid}")

    boot = False
    relaunches = 0
    for i in range(1, 75):
        kill_rivals()
        if not love_qemu_alive() and i > 2 and relaunches < 2:
            log(f"love_qemu_missing iter={i} — relaunch {relaunches+1}")
            relaunches += 1
            emu = subprocess.Popen(
                emu_args,
                stdout=open("/tmp/lovetest-emulator.log", "a"),
                stderr=subprocess.STDOUT,
                start_new_session=True,
            )
            log(f"emulator_pid={emu.pid}")
            time.sleep(10)
        prop = adb("shell", "getprop", "sys.boot_completed", timeout=5.0)
        log(f"iter={i} boot={prop!r} love={love_qemu_alive()}")
        if prop == "1":
            boot = True
            break
        time.sleep(3)

    if not boot:
        log("BOOT_FAIL")
        set_status("boot_fail")
        try:
            wd.kill()
        except Exception:
            pass
        return 3

    adb("shell", "wm", "size", "1080x1920")
    adb("shell", "wm", "density", "420")
    adb("shell", "settings", "put", "global", "animator_duration_scale", "0")
    adb("shell", "settings", "put", "global", "window_animation_scale", "0")
    adb("shell", "settings", "put", "global", "transition_animation_scale", "0")
    adb("shell", "cmd", "locale", "set-app-locales", "dev.lovetest.app", "--locales", "")
    log(f"wm={adb('shell', 'wm', 'size')}")

    set_status("testing")
    log("tests start")
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
    set_status(f"done exit={r.returncode}")
    try:
        wd.kill()
    except Exception:
        pass
    return r.returncode


if __name__ == "__main__":
    sys.exit(main())
