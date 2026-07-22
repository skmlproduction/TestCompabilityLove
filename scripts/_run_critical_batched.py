#!/usr/bin/env python3
"""Run critical instrumented classes one-by-one with retry (LMK-safe)."""
from __future__ import annotations

import os
import re
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ADB = Path.home() / "Library/Android/sdk/platform-tools/adb"
SERIAL = os.environ.get("ANDROID_SERIAL") or os.environ.get("LOVETEST_ADB_SERIAL") or "emulator-5558"
LOG = Path(os.environ.get("LOVETEST_CRITICAL_LOG", "/tmp/lt-critical-retry.log"))
STATUS = Path(os.environ.get("LOVETEST_CRITICAL_STATUS", "/tmp/lt-critical-retry-status.txt"))
MIN_AVAIL_KB = int(os.environ.get("LOVETEST_MIN_AVAIL_KB", "400000"))  # ~400 MB guest

DEFAULT_CLASSES = [
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


def log(msg: str) -> None:
    with LOG.open("a", encoding="utf-8") as f:
        f.write(msg + "\n")
    print(msg, flush=True)


def adb(*args: str) -> str:
    try:
        return subprocess.check_output(
            [str(ADB), "-s", SERIAL, *args], text=True, errors="replace", timeout=15
        ).strip()
    except Exception as e:
        return f"ERR:{e}"


def mem_available_kb() -> int:
    raw = adb("shell", "cat", "/proc/meminfo")
    m = re.search(r"MemAvailable:\s+(\d+)", raw)
    return int(m.group(1)) if m else 0


def cool_guest() -> None:
    """Wait for guest free RAM between classes.

    NEVER kill-all, NEVER touch other packages/AVDs/host apps.
    Only wait — instrumentation already isolates Love Test process.
    """
    time.sleep(2)
    deadline = time.time() + 30
    while time.time() < deadline:
        avail = mem_available_kb()
        if avail >= MIN_AVAIL_KB:
            log(f"guest MemAvailable={avail} kB OK")
            return
        log(f"waiting guest MemAvailable={avail} kB (need {MIN_AVAIL_KB})")
        time.sleep(3)
    log(f"guest MemAvailable still low ({mem_available_kb()} kB) — continuing")


def ensure_packages(env: dict) -> None:
    """Ensure both app + androidTest packages exist (hitchhike AVD sometimes drops them)."""
    app = adb("shell", "pm", "path", "dev.lovetest.app")
    test = adb("shell", "pm", "path", "dev.lovetest.app.test")
    if app.startswith("ERR:") or test.startswith("ERR:"):
        log(f"pm path error (skip): app={app!r} test={test!r}")
        return
    if "package:" in app and "package:" in test:
        return
    log(
        f"REINSTALL packages (app={('ok' if 'package:' in app else 'MISSING')}, "
        f"test={('ok' if 'package:' in test else 'MISSING')})"
    )
    # Soft: only HyperLock overlay — steals focus / may fight package manager under LMK.
    adb("shell", "am", "force-stop", "com.hyperlockscreen.app")
    subprocess.run(
        ["./gradlew", ":app:installDebug", ":app:installDebugAndroidTest", "--max-workers=2"],
        cwd=ROOT,
        env=env,
        stdout=LOG.open("a"),
        stderr=subprocess.STDOUT,
        check=False,
    )
    app2 = adb("shell", "pm", "path", "dev.lovetest.app")
    test2 = adb("shell", "pm", "path", "dev.lovetest.app.test")
    if "package:" in app2 and "package:" in test2:
        return
    # Fallback: direct adb install (gradle can report success while pm has no package).
    apk = ROOT / "app/build/outputs/apk/debug/app-debug.apk"
    test_apk = ROOT / "app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
    if apk.is_file():
        log("adb install -r app-debug.apk")
        subprocess.run(
            [str(ADB), "-s", SERIAL, "install", "-r", "-t", str(apk)],
            stdout=LOG.open("a"),
            stderr=subprocess.STDOUT,
            check=False,
        )
    if test_apk.is_file():
        log("adb install -r app-debug-androidTest.apk")
        subprocess.run(
            [str(ADB), "-s", SERIAL, "install", "-r", "-t", str(test_apk)],
            stdout=LOG.open("a"),
            stderr=subprocess.STDOUT,
            check=False,
        )
    app3 = adb("shell", "pm", "path", "dev.lovetest.app")
    if "package:" not in app3:
        log(f"WARN app still missing after adb install: {app3!r}")


def host_free_mb() -> float:
    try:
        out = subprocess.check_output(["vm_stat"], text=True, errors="replace")
        page = 16384.0
        free = 0
        for line in out.splitlines():
            if "page size of" in line:
                m = re.search(r"page size of (\d+)", line)
                if m:
                    page = float(m.group(1))
            if "Pages free" in line:
                free = int(line.split(":")[1].strip().rstrip("."))
        return free * page / 1024.0 / 1024.0
    except Exception:
        return -1.0


def classes_from_env() -> list[str]:
    raw = os.environ.get("LOVETEST_CRITICAL_CLASSES", "").strip()
    if not raw:
        return list(DEFAULT_CLASSES)
    return [c.strip() for c in raw.replace(",", " ").split() if c.strip()]


def main() -> int:
    classes = classes_from_env()
    LOG.write_text("", encoding="utf-8")
    STATUS.write_text("running\n", encoding="utf-8")
    env = os.environ.copy()
    env["ANDROID_SERIAL"] = SERIAL
    env["LOVETEST_ADB_SERIAL"] = SERIAL
    env["PATH"] = f"{ADB.parent}:{env.get('PATH', '')}"

    free_mb = host_free_mb()
    min_host = float(os.environ.get("LOVETEST_MIN_HOST_FREE_MB", "200"))
    log(f"host_free_MB≈{free_mb:.0f} (min {min_host:.0f})")
    if 0 <= free_mb < min_host:
        STATUS.write_text(f"host_ram_low free_mb={free_mb:.0f}\n", encoding="utf-8")
        log("HOST_RAM_LOW — skip connected (will not kill foreign apps)")
        return 4

    boot = adb("shell", "getprop", "sys.boot_completed")
    log(f"serial={SERIAL} boot={boot!r} classes={len(classes)}")
    if boot != "1":
        STATUS.write_text("no_device\n", encoding="utf-8")
        log("NO_DEVICE")
        return 3

    # Abort early if device drops — do not thrash reinstall while offline.
    def device_ok() -> bool:
        dev = subprocess.check_output([str(ADB), "devices"], text=True, errors="replace")
        return SERIAL in dev and "\tdevice" in dev

    # Soft: only HyperLock overlay on THIS serial (steals focus / DESTROYs test Activity).
    # Never kill-all, never touch other AVDs/qemu/host apps.
    adb("shell", "am", "force-stop", "com.hyperlockscreen.app")
    adb("shell", "settings", "put", "global", "animator_duration_scale", "0")
    adb("shell", "settings", "put", "global", "window_animation_scale", "0")
    adb("shell", "settings", "put", "global", "transition_animation_scale", "0")
    adb("shell", "cmd", "locale", "set-app-locales", "dev.lovetest.app", "--locales", "")

    results: list[tuple[str, bool, int]] = []
    for i, cls in enumerate(classes, 1):
        if not device_ok():
            log("DEVICE_OFFLINE — abort (no foreign kills)")
            STATUS.write_text("device_offline\n", encoding="utf-8")
            return 3
        cool_guest()
        ensure_packages(env)
        log(f"=== [{i}/{len(classes)}] {cls} ===")
        ok = False
        code = 1
        for attempt in (1, 2):
            if not device_ok():
                log("DEVICE_OFFLINE mid-attempt — abort")
                STATUS.write_text("device_offline\n", encoding="utf-8")
                return 3
            r = subprocess.run(
                [
                    "./gradlew",
                    ":app:connectedDebugAndroidTest",
                    f"-Pandroid.testInstrumentationRunnerArguments.class={cls}",
                    "--max-workers=2",
                ],
                cwd=ROOT,
                env=env,
                stdout=LOG.open("a"),
                stderr=subprocess.STDOUT,
            )
            code = r.returncode
            ok = code == 0
            log(f"ATTEMPT {attempt} {cls.rsplit('.', 1)[-1]} exit={code}")
            if ok:
                break
            cool_guest()
            ensure_packages(env)
        results.append((cls, ok, code))
        log(f"RESULT {cls.rsplit('.', 1)[-1]} {'OK' if ok else 'FAIL'}")

    fails = [c for c, ok, _ in results if not ok]
    summary = f"done ok={sum(1 for _, ok, _ in results if ok)}/{len(results)} fails={len(fails)}"
    log(summary)
    for c, ok, _ in results:
        log(f"  {'PASS' if ok else 'FAIL'} {c.rsplit('.', 1)[-1]}")
    STATUS.write_text(summary + "\n", encoding="utf-8")
    return 0 if not fails else 1


if __name__ == "__main__":
    sys.exit(main())
