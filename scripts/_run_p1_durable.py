#!/usr/bin/env python3
"""Durable P1 verify: wait for quiet host → assemble → Capture snapshot → install → QA → critical.

Does not kill foreign qemu/apps. Does not change MIUI/wm/fonts.
"""
from __future__ import annotations

import os
import re
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOG = Path(os.environ.get("LOVETEST_DURABLE_LOG", "/tmp/lovetest-p1-durable.log"))
MIN_FREE = int(os.environ.get("LOVETEST_MIN_HOST_FREE_MB", "1100"))
ANDROID_HOME = Path(os.environ.get("ANDROID_HOME", Path.home() / "Library/Android/sdk"))
ADB = str(ANDROID_HOME / "platform-tools/adb")
EMU = str(ANDROID_HOME / "emulator/emulator")
SERIAL = "emulator-5554"

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
CLS = [
    "dev.lovetest.app.navigation.CalculatorFlowComposeTest",
    "dev.lovetest.app.navigation.ProtocolFlowComposeTest",
    "dev.lovetest.app.navigation.ZodiacFlowComposeTest",
    "dev.lovetest.app.ui.consent.ConsentScreenComposeTest",
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


def sibling_assemble() -> int:
    r = subprocess.run(["ps", "-ax", "-o", "command="], capture_output=True, text=True)
    n = 0
    for line in r.stdout.splitlines():
        if "TestCompabilityLove" in line:
            continue
        if "gradlew" in line and "TestAppsCursor" in line:
            n += 1
        elif "gradlemain_assemble" in line:
            n += 1
    return n


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


def patch_heap(heap: int, kheap: int) -> Path:
    gp = Path.home() / ".gradle" / "gradle.properties"
    bak = gp.with_suffix(gp.suffix + ".bak-lt")
    text = gp.read_text()
    bak.write_text(text)
    text = re.sub(
        r"org\.gradle\.jvmargs=.*",
        f"org.gradle.jvmargs=-Xmx{heap}m -XX:MaxMetaspaceSize=192m "
        f"-XX:-TieredCompilation -Dfile.encoding=UTF-8",
        text,
    )
    text = re.sub(r"kotlin\.daemon\.jvmargs=.*\n?", "", text)
    text += (
        f"\nkotlin.daemon.jvmargs=-Xmx{kheap}m -XX:MaxMetaspaceSize=128m "
        f"-XX:-TieredCompilation -Dfile.encoding=UTF-8\n"
    )
    text = re.sub(r"org\.gradle\.workers\.max=.*", "org.gradle.workers.max=1", text)
    text = re.sub(r"org\.gradle\.parallel=.*", "org.gradle.parallel=false", text)
    gp.write_text(text)
    return bak


def restore(bak: Path) -> None:
    gp = Path.home() / ".gradle" / "gradle.properties"
    if bak.exists():
        gp.write_text(bak.read_text())
        bak.unlink(missing_ok=True)


def main() -> int:
    LOG.write_text(f"=== durable {time.strftime('%Y-%m-%d %H:%M:%S')} ===\n")
    daemonize()
    Path("/tmp/lovetest-durable.pid").write_text(str(os.getpid()))
    os.chdir(ROOT)
    os.environ["PATH"] = (
        f"{ANDROID_HOME / 'platform-tools'}:{ANDROID_HOME / 'emulator'}:"
        f"{os.environ.get('PATH', '')}"
    )

    log(f"durable start pid={os.getpid()}")
    # Phase A: assemble when free is enough (foreign qemu OK — we don't boot yet).
    for i in range(1, 240):
        f = free_mb()
        q = qemu_count()
        g = sibling_assemble()
        log(f"wait_assemble iter={i} free={f} qemu={q} sibling_gradle={g}")
        if f >= 1400 and (g == 0 or f >= 1600):
            break
        time.sleep(15)
    else:
        log("NOT READY for assemble")
        return 2

    bak = patch_heap(256, 128)
    try:
        subprocess.run([str(ROOT / "gradlew"), "--stop"], cwd=ROOT, check=False)
        r = subprocess.run(
            [
                str(ROOT / "gradlew"),
                ":app:assembleDebug",
                ":app:assembleDebugAndroidTest",
                "--max-workers=1",
            ],
            cwd=ROOT,
            check=False,
        )
        log(f"assemble_rc={r.returncode}")
        if r.returncode != 0:
            return r.returncode
    finally:
        restore(bak)

    # Phase B: hitchhike any online emulator, else boot Capture when qemu=0.
    serial = None
    for i in range(1, 180):
        f = free_mb()
        q = qemu_count()
        r = subprocess.run([ADB, "devices"], capture_output=True, text=True)
        online = []
        for line in r.stdout.splitlines():
            parts = line.split()
            if len(parts) >= 2 and parts[0].startswith("emulator-") and parts[1] == "device":
                boot = subprocess.run(
                    [ADB, "-s", parts[0], "shell", "getprop", "sys.boot_completed"],
                    capture_output=True,
                    text=True,
                ).stdout.strip()
                if boot == "1":
                    online.append(parts[0])
        log(f"wait_device iter={i} free={f} qemu={q} online={online}")
        if online:
            serial = online[0]
            log(f"HITCHHIKE {serial}")
            break
        if q == 0 and f >= 900:
            log("boot Capture")
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
            for j in range(1, 72):
                st = "none"
                rr = subprocess.run([ADB, "devices"], capture_output=True, text=True)
                for line in rr.stdout.splitlines():
                    parts = line.split()
                    if len(parts) >= 2 and parts[0] == SERIAL:
                        st = parts[1]
                boot = "?"
                if st == "device":
                    boot = (
                        subprocess.run(
                            [ADB, "-s", SERIAL, "shell", "getprop", "sys.boot_completed"],
                            capture_output=True,
                            text=True,
                        ).stdout.strip()
                        or "?"
                    )
                log(f"boot j={j} st={st} boot={boot} free={free_mb()}")
                if st == "device" and boot == "1":
                    serial = SERIAL
                    break
                if proc.poll() is not None:
                    log("emu died")
                    log(clog.read_text()[-2000:])
                    break
                time.sleep(5)
            if serial:
                break
        time.sleep(10)
    else:
        log("NOT READY for device")
        return 4

    if not serial:
        log("no serial")
        return 4

    log("adb install")
    apk = ROOT / "app/build/outputs/apk/debug/app-debug.apk"
    tapk = ROOT / "app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
    for pth in (apk, tapk):
        r = subprocess.run(
            [ADB, "-s", serial, "install", "-r", "-t", str(pth)],
            capture_output=True,
            text=True,
        )
        log(f"install {pth.name}: {(r.stdout + r.stderr).strip()} rc={r.returncode}")

    os.environ["ANDROID_SERIAL"] = serial
    os.environ["LOVETEST_ADB_SERIAL"] = serial
    os.environ["LOVETEST_USE_EMULATOR"] = "1"
    outq = ROOT / "docs/screenshots/qa/emulator/ru"
    outq.mkdir(parents=True, exist_ok=True)
    os.environ["LOVETEST_SCREENSHOT_OUT_DIR"] = str(outq)
    for sid in P1:
        log(f"capture {sid}")
        subprocess.run(
            ["bash", "scripts/adb_screenshot_preview.sh", sid, "ru"],
            cwd=ROOT,
            stdin=subprocess.DEVNULL,
            check=False,
        )
    ok = fail = 0
    for cls in CLS:
        log(f"test {cls}")
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
            timeout=300,
        )
        text = r.stdout + r.stderr
        log("\n".join(text.splitlines()[-20:]))
        if "OK (" in text:
            ok += 1
        else:
            fail += 1
    log(f"CRITICAL_RERUN ok={ok} fail={fail}")
    log("PIPELINE DONE")
    return 0 if fail == 0 else 5


if __name__ == "__main__":
    sys.exit(main())
