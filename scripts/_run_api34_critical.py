#!/usr/bin/env python3
"""Wait for quiet host, boot LoveTester_Capture (API34), install P1 APKs, rerun 4 critical classes.

Does not kill foreign qemu/apps. Prefers hitchhike of any emulator with SDK >= 34.
"""
from __future__ import annotations

import os
import re
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOG = Path(os.environ.get("LOVETEST_API34_LOG", "/tmp/lovetest-api34-critical.log"))
ANDROID_HOME = Path(os.environ.get("ANDROID_HOME", Path.home() / "Library/Android/sdk"))
ADB = str(ANDROID_HOME / "platform-tools/adb")
EMU = str(ANDROID_HOME / "emulator/emulator")
MIN_BOOT_FREE = int(os.environ.get("LOVETEST_MIN_BOOT_FREE_MB", "900"))
APK = ROOT / "app/build/outputs/apk/debug/app-debug.apk"
TAPK = ROOT / "app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"

CLS = [
    "dev.lovetest.app.navigation.CalculatorFlowComposeTest",
    "dev.lovetest.app.navigation.ProtocolFlowComposeTest",
    "dev.lovetest.app.navigation.ZodiacFlowComposeTest",
    "dev.lovetest.app.ui.consent.ConsentScreenComposeTest",
]
P1 = [
    "pair_input",
    "consent_ads_gdpr",
    "love_test_result_low",
    "calculator_result",
    "protocol_result",
    "zodiac_result",
    "pair_result",
    "letters_result",
    "victory_result",
    "hub_main",
    "love_test_result",
    "premium_paywall",
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


def sdk_level(serial: str) -> int:
    r = subprocess.run(
        [ADB, "-s", serial, "shell", "getprop", "ro.build.version.sdk"],
        capture_output=True,
        text=True,
        timeout=15,
    )
    try:
        return int(r.stdout.strip() or "0")
    except ValueError:
        return 0


def online_api34() -> list[str]:
    r = subprocess.run([ADB, "devices"], capture_output=True, text=True, timeout=20)
    out: list[str] = []
    for line in r.stdout.splitlines():
        parts = line.split()
        if len(parts) >= 2 and parts[0].startswith("emulator-") and parts[1] == "device":
            if sdk_level(parts[0]) >= 34:
                boot = subprocess.run(
                    [ADB, "-s", parts[0], "shell", "getprop", "sys.boot_completed"],
                    capture_output=True,
                    text=True,
                    timeout=10,
                ).stdout.strip()
                if boot == "1" or boot == "":
                    out.append(parts[0])
    return out


def daemonize() -> None:
    if os.environ.get("LOVETEST_NO_DAEMON") == "1":
        return
    if os.fork() > 0:
        sys.exit(0)
    os.setsid()
    if os.fork() > 0:
        sys.exit(0)
    sys.stdout.flush()
    sys.stderr.flush()
    with LOG.open("a") as f:
        os.dup2(f.fileno(), 1)
        os.dup2(f.fileno(), 2)


def boot_capture() -> str | None:
    log("boot LoveTester_Capture snapshot port 5554")
    clog = Path("/tmp/lovetest-capture.log")
    with clog.open("w") as out:
        proc = subprocess.Popen(
            [
                EMU,
                "-avd",
                "LoveTester_Capture",
                "-port",
                "5554",
                "-snapshot",
                "default_boot",
                "-no-snapshot-save",
                "-no-boot-anim",
                "-no-audio",
                "-gpu",
                "swiftshader_indirect",
                "-memory",
                "1024",
                "-cores",
                "2",
            ],
            stdout=out,
            stderr=subprocess.STDOUT,
            start_new_session=True,
        )
    serial = "emulator-5554"
    for j in range(1, 72):
        r = subprocess.run([ADB, "devices"], capture_output=True, text=True)
        st = "none"
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
                ).stdout.strip()
                or "?"
            )
        log(f"boot j={j} st={st} boot={boot} free={free_mb()} sdk={sdk_level(serial) if st=='device' else '?'}")
        if st == "device" and boot == "1" and sdk_level(serial) >= 34:
            return serial
        if proc.poll() is not None:
            log("emu died")
            log(clog.read_text()[-2000:])
            return None
        time.sleep(5)
    return None


def main() -> int:
    LOG.write_text(f"=== api34-critical {time.strftime('%Y-%m-%d %H:%M:%S')} ===\n")
    daemonize()
    Path("/tmp/lovetest-api34-critical.pid").write_text(str(os.getpid()))
    os.chdir(ROOT)
    os.environ["PATH"] = (
        f"{ANDROID_HOME / 'platform-tools'}:{ANDROID_HOME / 'emulator'}:"
        f"{os.environ.get('PATH', '')}"
    )

    if not APK.exists() or not TAPK.exists():
        log(f"missing apk apk={APK.exists()} tapk={TAPK.exists()}")
        return 2

    log(f"start pid={os.getpid()} apk={time.ctime(APK.stat().st_mtime)}")
    serial = None
    for i in range(1, 180):
        f = free_mb()
        q = qemu_count()
        try:
            api34 = online_api34()
        except Exception as exc:  # noqa: BLE001
            api34 = []
            log(f"adb skip: {exc}")
        log(f"wait iter={i} free={f} qemu={q} api34={api34}")
        if api34:
            serial = api34[0]
            log(f"HITCHHIKE {serial} sdk={sdk_level(serial)}")
            break
        # Boot Capture only when no foreign qemu (or only if free is huge)
        if q == 0 and f >= MIN_BOOT_FREE:
            serial = boot_capture()
            if serial:
                break
        time.sleep(12)
    else:
        log("NOT READY")
        return 4

    if not serial:
        log("no serial")
        return 4

    for pth in (APK, TAPK):
        r = subprocess.run(
            [ADB, "-s", serial, "install", "-r", "-t", str(pth)],
            capture_output=True,
            text=True,
            timeout=180,
        )
        log(f"install {pth.name}: {(r.stdout + r.stderr).strip()[:240]} rc={r.returncode}")

    subprocess.run(
        [ADB, "-s", serial, "shell", "am", "force-stop", "com.hyperlockscreen.app"],
        capture_output=True,
    )
    # Force RU app locales for QA path
    subprocess.run(
        [ADB, "-s", serial, "shell", "cmd", "locale", "set-app-locales", "dev.lovetest.app", "--locales", "ru-RU"],
        capture_output=True,
    )

    os.environ["LOVETEST_ADB_SERIAL"] = serial
    os.environ["ANDROID_SERIAL"] = serial
    os.environ.pop("LOVETEST_USE_EMULATOR", None)
    outq = ROOT / "docs/screenshots/qa/emulator/ru"
    outq.mkdir(parents=True, exist_ok=True)
    os.environ["LOVETEST_SCREENSHOT_OUT_DIR"] = str(outq)

    ok_cap = 0
    for sid in P1:
        log(f"capture {sid}")
        r = subprocess.run(
            ["bash", "scripts/adb_screenshot_preview.sh", sid, "ru"],
            cwd=ROOT,
            stdin=subprocess.DEVNULL,
            capture_output=True,
            text=True,
            check=False,
        )
        if r.returncode == 0:
            ok_cap += 1
            log(f"capture OK {sid}")
        else:
            log(f"capture FAIL {sid}: {(r.stdout + r.stderr)[-180:]}")
    log(f"captures_ok={ok_cap}/{len(P1)}")

    ok = fail = 0
    for cls in CLS:
        log(f"test {cls}")
        subprocess.run(
            [ADB, "-s", serial, "shell", "am", "force-stop", "com.hyperlockscreen.app"],
            capture_output=True,
        )
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
        text = r.stdout + r.stderr
        log("\n".join(text.splitlines()[-30:]))
        if "OK (" in text:
            ok += 1
        else:
            fail += 1
        time.sleep(2)
    log(f"CRITICAL_RERUN ok={ok} fail={fail}")
    log("DONE")
    return 0 if fail == 0 else 5


if __name__ == "__main__":
    sys.exit(main())
