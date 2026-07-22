#!/usr/bin/env python3
"""Boot LoveTester_Capture from snapshot → install → 4 critical (tests-first, no capture)."""
from __future__ import annotations

import os
import re
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOG = Path("/tmp/lovetest-critical-snapshot.log")
ANDROID_HOME = Path(os.environ.get("ANDROID_HOME", Path.home() / "Library/Android/sdk"))
ADB = str(ANDROID_HOME / "platform-tools/adb")
EMU = str(ANDROID_HOME / "emulator/emulator")
SERIAL = "emulator-5554"
AVD = os.environ.get("LOVETEST_CAPTURE_AVD", "LoveTester_Capture")
APK = ROOT / "app/build/outputs/apk/debug/app-debug.apk"
TAPK = ROOT / "app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
MIN_FREE = int(os.environ.get("LOVETEST_MIN_BOOT_FREE_MB", "450"))
CLS = [
    "dev.lovetest.app.ui.consent.ConsentScreenComposeTest",
    "dev.lovetest.app.navigation.CalculatorFlowComposeTest",
    "dev.lovetest.app.navigation.ProtocolFlowComposeTest",
    "dev.lovetest.app.navigation.ZodiacFlowComposeTest",
]


def log(msg: str) -> None:
    line = f"{time.strftime('%H:%M:%S')} {msg}"
    print(line, flush=True)
    with LOG.open("a") as f:
        f.write(line + "\n")


def free_mb() -> int:
    out = subprocess.check_output(["vm_stat"], text=True)
    page = 16384
    free = 0
    for line in out.splitlines():
        if "page size of" in line:
            m = re.search(r"(\d+)", line)
            if m:
                page = int(m.group(1))
        if line.startswith("Pages free:") or line.startswith("Pages speculative:"):
            free += int(line.split(":")[1].strip().rstrip("."))
    return int(free * page / 1024 / 1024)


def qemu_lovetester() -> bool:
    r = subprocess.run(["ps", "-ax", "-o", "command="], capture_output=True, text=True)
    for line in r.stdout.splitlines():
        if "/qemu-system-" in line and ("LoveTester_Capture" in line or "LoveTester_Lite" in line):
            return True
    return False


def hitchhike_api34() -> str | None:
    r = subprocess.run([ADB, "devices"], capture_output=True, text=True, timeout=15)
    for line in r.stdout.splitlines():
        parts = line.split()
        if len(parts) < 2 or not parts[0].startswith("emulator-") or parts[1] != "device":
            continue
        serial = parts[0]
        if not alive(serial):
            continue
        sdk = subprocess.run(
            [ADB, "-s", serial, "shell", "getprop", "ro.build.version.sdk"],
            capture_output=True,
            text=True,
            timeout=8,
        ).stdout.strip()
        boot = subprocess.run(
            [ADB, "-s", serial, "shell", "getprop", "sys.boot_completed"],
            capture_output=True,
            text=True,
            timeout=8,
        ).stdout.strip()
        try:
            if boot == "1" and int(sdk) >= 34:
                return serial
        except ValueError:
            pass
    return None


def alive(serial: str) -> bool:
    try:
        r = subprocess.run([ADB, "-s", serial, "get-state"], capture_output=True, text=True, timeout=8)
        return r.stdout.strip() == "device"
    except Exception:
        return False


def boot_snapshot() -> subprocess.Popen[bytes]:
    log(f"boot {AVD} snapshot default_boot mem=1024 port=5554")
    clog = Path("/tmp/lovetest-capture.log")
    with clog.open("w") as out:
        return subprocess.Popen(
            [EMU, "-avd", AVD, "-port", "5554", "-snapshot", "default_boot",
             "-no-snapshot-save", "-no-boot-anim", "-no-audio", "-gpu", "swiftshader_indirect",
             "-memory", "1024", "-cores", "2"],
            stdout=out, stderr=subprocess.STDOUT, start_new_session=True,
        )


def wait_boot(serial: str, proc: subprocess.Popen[bytes] | None, timeout: int = 300) -> bool:
    deadline = time.time() + timeout
    while time.time() < deadline:
        if alive(serial):
            boot = subprocess.run(
                [ADB, "-s", serial, "shell", "getprop", "sys.boot_completed"],
                capture_output=True, text=True, timeout=8,
            ).stdout.strip()
            if boot == "1":
                return True
        if proc and proc.poll() is not None:
            log("emu died during boot")
            log(Path("/tmp/lovetest-capture.log").read_text()[-2000:])
            return False
        time.sleep(3)
    return False


def install(serial: str) -> bool:
    for pth in (APK, TAPK):
        if not alive(serial):
            log(f"lost before install {pth.name}")
            return False
        r = subprocess.run(
            [ADB, "-s", serial, "install", "-r", "-t", str(pth)],
            capture_output=True, text=True, timeout=180,
        )
        log(f"install {pth.name} rc={r.returncode} {(r.stdout + r.stderr).strip()[:140]}")
        if r.returncode != 0:
            return False
    return True


def run_critical(serial: str) -> tuple[int, int]:
    for key in ("window_animation_scale", "transition_animation_scale", "animator_duration_scale"):
        subprocess.run([ADB, "-s", serial, "shell", "settings", "put", "global", key, "0"], capture_output=True)
    subprocess.run([ADB, "-s", serial, "shell", "cmd", "locale", "set-app-locales", "dev.lovetest.app", "ru-RU"], capture_output=True)
    ok = fail = 0
    for cls in CLS:
        if not alive(serial):
            log(f"lost before {cls}")
            fail += 1
            continue
        subprocess.run([ADB, "-s", serial, "shell", "am", "force-stop", "com.hyperlockscreen.app"], capture_output=True)
        subprocess.run([ADB, "-s", serial, "shell", "am", "force-stop", "dev.lovetest.app"], capture_output=True)
        time.sleep(1)
        log(f"test {cls}")
        try:
            r = subprocess.run(
                [ADB, "-s", serial, "shell", "am", "instrument", "-w", "-r", "-e", "class", cls,
                 "dev.lovetest.app.test/androidx.test.runner.AndroidJUnitRunner"],
                capture_output=True, text=True, timeout=360,
            )
        except subprocess.TimeoutExpired:
            log(f"timeout {cls}")
            fail += 1
            continue
        text = r.stdout + r.stderr
        log("\n".join(text.splitlines()[-25:]))
        ok += 1 if "OK (" in text else 0
        fail += 0 if "OK (" in text else 1
        time.sleep(1)
    return ok, fail


def main() -> int:
    LOG.write_text(f"=== snapshot critical {time.strftime('%Y-%m-%d %H:%M:%S')} ===\n")
    os.chdir(ROOT)
    os.environ["PATH"] = f"{ANDROID_HOME / 'platform-tools'}:{ANDROID_HOME / 'emulator'}:{os.environ.get('PATH', '')}"
    Path("/tmp/lovetest-critical-snapshot.pid").write_text(str(os.getpid()))
    if not APK.exists() or not TAPK.exists():
        log("missing apk")
        return 2

    serial = None
    proc = None
    for i in range(1, 180):
        f = free_mb()
        h = hitchhike_api34()
        lt = qemu_lovetester()
        log(f"wait {i} free={f} hitch={h} lovetester_qemu={lt}")
        if h:
            serial = h
            log(f"USE hitch {serial}")
            break
        if alive(SERIAL):
            serial = SERIAL
            log(f"USE existing {serial}")
            break
        if f >= MIN_FREE and not lt:
            proc = boot_snapshot()
            if wait_boot(SERIAL, proc):
                serial = SERIAL
                break
            proc = None
        time.sleep(10)
    else:
        log("NOT READY")
        return 4

    if not serial or not alive(serial):
        log("no stable serial")
        return 4

    log(f"GO {serial} free={free_mb()}")
    if not install(serial):
        return 3
    ok, fail = run_critical(serial)
    log(f"CRITICAL_RERUN ok={ok} fail={fail}")
    log("DONE")
    return 0 if fail == 0 else 5


if __name__ == "__main__":
    sys.exit(main())
