#!/usr/bin/env python3
"""Wait for host RAM, boot LoveTester_Capture from snapshot, install, P1 capture, critical rerun.

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
LOG = Path(os.environ.get("LOVETEST_PIPELINE_LOG", "/tmp/lovetest-boot-pipeline.log"))
AVD = os.environ.get("LOVETEST_CAPTURE_AVD", "LoveTester_Capture")
EMU_PORT = int(os.environ.get("LOVETEST_EMU_PORT", "5554"))
SERIAL = f"emulator-{EMU_PORT}"
MIN_FREE = int(os.environ.get("LOVETEST_MIN_HOST_FREE_MB", "700"))
ANDROID_HOME = Path(os.environ.get("ANDROID_HOME", Path.home() / "Library/Android/sdk"))
ADB = str(ANDROID_HOME / "platform-tools/adb")
EMULATOR = str(ANDROID_HOME / "emulator/emulator")

P1_SCREENS = [
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
FAILED_CLASSES = [
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


def host_free_mb() -> int:
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


def adb(*args: str, check: bool = False, timeout: int | None = 30) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        [ADB, *args],
        text=True,
        capture_output=True,
        check=check,
        timeout=timeout,
    )


def device_state(serial: str) -> str:
    r = adb("devices")
    for line in r.stdout.splitlines():
        parts = line.split()
        if len(parts) >= 2 and parts[0] == serial:
            return parts[1]
    return "none"


def patch_gradle_heap(heap: int, kheap: int) -> Path:
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


def restore_gradle(bak: Path) -> None:
    gp = Path.home() / ".gradle" / "gradle.properties"
    if bak.exists():
        gp.write_text(bak.read_text())
        bak.unlink(missing_ok=True)


def daemonize() -> None:
    """Double-fork so Cursor/parent shell exit does not SIGKILL the waiter."""
    if os.environ.get("LOVETEST_NO_DAEMON") == "1":
        return
    if os.fork() > 0:
        sys.exit(0)
    os.setsid()
    if os.fork() > 0:
        sys.exit(0)
    sys.stdout.flush()
    sys.stderr.flush()
    with open(LOG, "a") as logf:
        os.dup2(logf.fileno(), 1)
        os.dup2(logf.fileno(), 2)


def main() -> int:
    LOG.write_text(f"=== poll start {time.strftime('%Y-%m-%d %H:%M:%S')} ===\n")
    daemonize()
    Path(f"/tmp/lovetest-p1-pipeline.pid").write_text(str(os.getpid()))
    os.environ["PATH"] = f"{ANDROID_HOME / 'platform-tools'}:{ANDROID_HOME / 'emulator'}:{os.environ.get('PATH', '')}"
    os.chdir(ROOT)

    ready = 0
    for i in range(1, 181):
        free = host_free_mb()
        q = qemu_count()
        log(f"iter={i} free={free} qemu={q} pid={os.getpid()}")
        if free >= MIN_FREE and q == 0:
            ready = 1
            break
        # Hitchhike even under moderate pressure — device online is rarer than free RAM.
        if free >= 150:
            try:
                # Prefer our Capture serial; else any online emulator (user OK'd existing AVD).
                candidates = [SERIAL]
                r = adb("devices")
                for line in r.stdout.splitlines():
                    parts = line.split()
                    if (
                        len(parts) >= 2
                        and parts[0].startswith("emulator-")
                        and parts[1] == "device"
                        and parts[0] not in candidates
                    ):
                        candidates.append(parts[0])
                hitch = False
                for cand in candidates:
                    if device_state(cand) != "device":
                        continue
                    boot = adb("-s", cand, "shell", "getprop", "sys.boot_completed")
                    if boot.stdout.strip() == "1":
                        # Hitchhike overrides pin for this run.
                        globals()["SERIAL"] = cand
                        log(f"HITCHHIKE {cand} free={free}")
                        ready = 2
                        hitch = True
                        break
                if hitch:
                    break
            except Exception as exc:  # noqa: BLE001
                log(f"adb skip: {exc}")
        time.sleep(10)

    if ready == 0:
        log(f"NOT READY free={host_free_mb()}")
        return 2

    if ready == 1:
        log(f"Booting {AVD} snapshot on port {EMU_PORT}")
        capture_log = Path("/tmp/lovetest-capture.log")
        with capture_log.open("w") as out:
            proc = subprocess.Popen(
                [
                    EMULATOR,
                    "-avd",
                    AVD,
                    "-port",
                    str(EMU_PORT),
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
        log(f"emulator pid={proc.pid}")
        for j in range(1, 73):
            st = device_state(SERIAL)
            boot = "?"
            if st == "device":
                boot = adb("-s", SERIAL, "shell", "getprop", "sys.boot_completed").stdout.strip() or "?"
            log(f"boot_wait j={j} st={st} boot={boot} free={host_free_mb()}")
            if st == "device" and boot == "1":
                wm = adb("-s", SERIAL, "shell", "wm", "size")
                log(f"EMULATOR READY {SERIAL} {wm.stdout.strip()}")
                break
            if proc.poll() is not None:
                log("emulator died")
                log(capture_log.read_text()[-4000:])
                return 3
            time.sleep(5)
        else:
            log("boot timeout")
            return 4

    free = host_free_mb()
    heap, kheap = 384, 192
    if free >= 1200:
        heap, kheap = 768, 384
    elif free >= 900:
        heap, kheap = 512, 256
    log(f"install free={free} heap={heap} on {SERIAL}")
    bak = patch_gradle_heap(heap, kheap)
    try:
        subprocess.run([str(ROOT / "gradlew"), "--stop"], cwd=ROOT, check=False)
        r = subprocess.run(
            [
                str(ROOT / "gradlew"),
                ":app:installDebug",
                ":app:installDebugAndroidTest",
                "--max-workers=1",
            ],
            cwd=ROOT,
            check=False,
        )
        log(f"install_rc={r.returncode}")
        if r.returncode != 0:
            return r.returncode
    finally:
        restore_gradle(bak)

    out_qa = Path(
        os.environ.get(
            "LOVETEST_SCREENSHOT_OUT_DIR",
            str(ROOT / "docs/screenshots/qa/emulator/ru"),
        )
    )
    out_qa.mkdir(parents=True, exist_ok=True)
    os.environ["LOVETEST_SCREENSHOT_OUT_DIR"] = str(out_qa)
    os.environ["LOVETEST_USE_EMULATOR"] = "1"
    os.environ["ANDROID_SERIAL"] = SERIAL
    os.environ["LOVETEST_ADB_SERIAL"] = SERIAL

    for sid in P1_SCREENS:
        log(f"=== capture {sid} ===")
        r = subprocess.run(
            ["bash", "scripts/adb_screenshot_preview.sh", sid, "ru"],
            cwd=ROOT,
            stdin=subprocess.DEVNULL,
            check=False,
        )
        if r.returncode != 0:
            log(f"WARN capture {sid} rc={r.returncode}")

    adb("-s", SERIAL, "shell", "am", "force-stop", "com.hyperlockscreen.app")
    ok = fail = 0
    for cls in FAILED_CLASSES:
        log(f"=== {cls} ===")
        r = adb(
            "-s",
            SERIAL,
            "shell",
            "am",
            "instrument",
            "-w",
            "-r",
            "-e",
            "class",
            cls,
            "dev.lovetest.app.test/androidx.test.runner.AndroidJUnitRunner",
            timeout=300,
        )
        tail = "\n".join((r.stdout + r.stderr).splitlines()[-30:])
        log(tail)
        if "OK (" in (r.stdout + r.stderr):
            ok += 1
        else:
            fail += 1
    log(f"CRITICAL_RERUN ok={ok} fail={fail}")
    log(f"=== PIPELINE DONE {time.strftime('%Y-%m-%d %H:%M:%S')} ===")
    return 0 if fail == 0 else 5


if __name__ == "__main__":
    sys.exit(main())
