#!/usr/bin/env python3
"""Atomic: wait quiet RAM → boot LoveTester_Capture → install → 4 critical (no gap).

Does not kill foreign qemu/apps.
"""
from __future__ import annotations

import os
import re
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOG = Path("/tmp/lovetest-critical-atomic.log")
ANDROID_HOME = Path(os.environ.get("ANDROID_HOME", Path.home() / "Library/Android/sdk"))
ADB = str(ANDROID_HOME / "platform-tools/adb")
EMU = str(ANDROID_HOME / "emulator/emulator")
SERIAL = "emulator-5554"
APK = ROOT / "app/build/outputs/apk/debug/app-debug.apk"
TAPK = ROOT / "app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
MIN_FREE = int(os.environ.get("LOVETEST_MIN_BOOT_FREE_MB", "550"))

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


def qemu_count() -> int:
    n = 0
    for name in ("qemu-system-aarch64", "qemu-system-aarch64-headless"):
        r = subprocess.run(["pgrep", "-x", name], capture_output=True, text=True)
        if r.returncode == 0 and r.stdout.strip():
            n += len(r.stdout.strip().splitlines())
    return n


def alive(serial: str) -> bool:
    try:
        r = subprocess.run(
            [ADB, "-s", serial, "get-state"],
            capture_output=True,
            text=True,
            timeout=8,
        )
        return r.stdout.strip() == "device"
    except Exception:
        return False


def hitchhike_api34() -> str | None:
    r = subprocess.run([ADB, "devices"], capture_output=True, text=True, timeout=15)
    for line in r.stdout.splitlines():
        parts = line.split()
        if len(parts) >= 2 and parts[0].startswith("emulator-") and parts[1] == "device":
            sdk = subprocess.run(
                [ADB, "-s", parts[0], "shell", "getprop", "ro.build.version.sdk"],
                capture_output=True,
                text=True,
                timeout=8,
            ).stdout.strip()
            try:
                if int(sdk) >= 34:
                    return parts[0]
            except ValueError:
                pass
    return None


def boot_capture() -> subprocess.Popen[bytes] | None:
    log("boot LoveTester_Capture cold mem=896")
    clog = Path("/tmp/lovetest-capture.log")
    with clog.open("w") as out:
        return subprocess.Popen(
            [
                EMU,
                "-avd",
                "LoveTester_Capture",
                "-port",
                "5554",
                "-no-snapshot-load",
                "-no-snapshot-save",
                "-no-boot-anim",
                "-no-audio",
                "-gpu",
                "swiftshader_indirect",
                "-memory",
                "896",
                "-cores",
                "2",
            ],
            stdout=out,
            stderr=subprocess.STDOUT,
            start_new_session=True,
        )


def wait_boot(serial: str, proc: subprocess.Popen[bytes] | None, timeout: int = 360) -> bool:
    deadline = time.time() + timeout
    while time.time() < deadline:
        st = "none"
        r = subprocess.run([ADB, "devices"], capture_output=True, text=True, timeout=15)
        for line in r.stdout.splitlines():
            parts = line.split()
            if len(parts) >= 2 and parts[0] == serial:
                st = parts[1]
        boot = "?"
        if st == "device":
            boot = (
                subprocess.run(
                    [ADB, "-s", serial, "shell", "getprop", "sys.boot_completed"],
                    capture_output=True,
                    text=True,
                    timeout=8,
                ).stdout.strip()
                or "?"
            )
        if st == "device" and boot == "1":
            return True
        if proc and proc.poll() is not None:
            log("emu died during boot")
            log(Path("/tmp/lovetest-capture.log").read_text()[-1500:])
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
            capture_output=True,
            text=True,
            timeout=180,
        )
        log(f"install {pth.name} rc={r.returncode} {(r.stdout + r.stderr).strip()[:120]}")
        if r.returncode != 0:
            return False
    return True


def run_critical(serial: str) -> tuple[int, int]:
    for key in ("window_animation_scale", "transition_animation_scale", "animator_duration_scale"):
        subprocess.run(
            [ADB, "-s", serial, "shell", "settings", "put", "global", key, "0"],
            capture_output=True,
        )
    ok = fail = 0
    for cls in CLS:
        if not alive(serial):
            log(f"lost before {cls}")
            fail += 1
            continue
        subprocess.run(
            [ADB, "-s", serial, "shell", "am", "force-stop", "com.hyperlockscreen.app"],
            capture_output=True,
        )
        subprocess.run(
            [ADB, "-s", serial, "shell", "am", "force-stop", "dev.lovetest.app"],
            capture_output=True,
        )
        time.sleep(1)
        log(f"test {cls}")
        try:
            r = subprocess.run(
                [
                    ADB,
                    "-s",
                    serial,
                    "shell",
                    "am",
                    "instrument",
                    "-w",
                    "-r",
                    "-e",
                    "class",
                    cls,
                    "dev.lovetest.app.test/androidx.test.runner.AndroidJUnitRunner",
                ],
                capture_output=True,
                text=True,
                timeout=360,
            )
        except subprocess.TimeoutExpired:
            log(f"timeout {cls}")
            fail += 1
            continue
        text = r.stdout + r.stderr
        log("\n".join(text.splitlines()[-25:]))
        if "OK (" in text:
            ok += 1
        else:
            fail += 1
        time.sleep(1)
    return ok, fail


def main() -> int:
    LOG.write_text(f"=== atomic {time.strftime('%Y-%m-%d %H:%M:%S')} ===\n")
    os.chdir(ROOT)
    os.environ["PATH"] = (
        f"{ANDROID_HOME / 'platform-tools'}:{ANDROID_HOME / 'emulator'}:"
        f"{os.environ.get('PATH', '')}"
    )
    Path("/tmp/lovetest-critical-atomic.pid").write_text(str(os.getpid()))

    if not APK.exists() or not TAPK.exists():
        log("missing apk")
        return 2

    serial = None
    proc = None
    for i in range(1, 120):
        f = free_mb()
        q = qemu_count()
        try:
            h = hitchhike_api34()
        except Exception as exc:  # noqa: BLE001
            h = None
            log(f"adb: {exc}")
        log(f"wait {i} free={f} qemu={q} hitch={h}")
        if h:
            serial = h
            log(f"USE {serial}")
            break
        if q == 0 and f >= MIN_FREE:
            proc = boot_capture()
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
