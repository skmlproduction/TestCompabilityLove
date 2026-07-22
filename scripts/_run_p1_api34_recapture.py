#!/usr/bin/env python3
"""Wait quiet host → cold-boot LoveTester_Capture → install → P1 QA ×12 → optional snapshot save.

Does not kill foreign qemu/apps. Prefer cold boot (default_boot snapshot is broken).
"""
from __future__ import annotations

import os
import re
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOG = Path("/tmp/lovetest-p1-api34-recapture.log")
ANDROID_HOME = Path(os.environ.get("ANDROID_HOME", Path.home() / "Library/Android/sdk"))
ADB = str(ANDROID_HOME / "platform-tools/adb")
EMU = str(ANDROID_HOME / "emulator/emulator")
SERIAL = "emulator-5554"
AVD = "LoveTester_Capture"
APK = ROOT / "app/build/outputs/apk/debug/app-debug.apk"
OUT_QA = ROOT / "docs/screenshots/qa/emulator/ru"
MIN_FREE = int(os.environ.get("LOVETEST_MIN_BOOT_FREE_MB", "550"))
P1_DEFAULT = [
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
P1 = [
    s.strip()
    for s in os.environ.get("LOVETEST_P1_ONLY", "").split(",")
    if s.strip()
] or P1_DEFAULT


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
    r = subprocess.run(["pgrep", "-x", "qemu-system-aarch64-headless"], capture_output=True, text=True)
    if r.returncode != 0 or not r.stdout.strip():
        return 0
    return len(r.stdout.strip().splitlines())


def alive(serial: str) -> bool:
    try:
        r = subprocess.run([ADB, "-s", serial, "get-state"], capture_output=True, text=True, timeout=8)
        return r.stdout.strip() == "device"
    except Exception:
        return False


def hitch_api34() -> str | None:
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
        mem = subprocess.run(
            [ADB, "-s", serial, "shell", "getprop", "ro.boot.qemu.avd_name"],
            capture_output=True,
            text=True,
            timeout=8,
        ).stdout.strip()
        # Skip ultra-lowram PixelCap-style AVDs (224MB) — bad for QA frames.
        try:
            if boot == "1" and int(sdk) >= 34 and "PixelCap" not in mem and "SoloSnap" not in mem:
                # Prefer Capture/MBN/VisCap; still accept unknown API34 if not known lowram name.
                return serial
        except ValueError:
            pass
    return None


def boot_cold() -> subprocess.Popen[bytes]:
    log(f"boot {AVD} cold mem=896 port=5554")
    clog = Path("/tmp/lovetest-capture.log")
    with clog.open("w") as out:
        return subprocess.Popen(
            [
                EMU,
                "-avd",
                AVD,
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
        log(f"boot_wait st={st} boot={boot} free={free_mb()}")
        if st == "device" and boot == "1":
            return True
        if proc and proc.poll() is not None:
            log("emu died during boot")
            log(Path("/tmp/lovetest-capture.log").read_text()[-2000:])
            return False
        time.sleep(4)
    return False


def install(serial: str) -> bool:
    if not APK.exists():
        log("missing apk")
        return False
    r = subprocess.run(
        [ADB, "-s", serial, "install", "-r", "-t", str(APK)],
        capture_output=True,
        text=True,
        timeout=180,
    )
    log(f"install {APK.name} rc={r.returncode} {(r.stdout + r.stderr).strip()[:140]}")
    return r.returncode == 0


def capture_p1(serial: str) -> tuple[int, int]:
    env = os.environ.copy()
    env["LOVETEST_ADB_SERIAL"] = serial
    env["ANDROID_SERIAL"] = serial
    env["LOVETEST_SCREENSHOT_OUT_DIR"] = str(OUT_QA)
    env["WAIT_SEC"] = os.environ.get("LOVETEST_CAPTURE_WAIT_SEC", "1.2")
    env.pop("LOVETEST_USE_EMULATOR", None)
    subprocess.run(
        [ADB, "-s", serial, "shell", "cmd", "locale", "set-app-locales", "dev.lovetest.app", "--locales", "ru-RU"],
        capture_output=True,
    )
    # Disable animations once — less jank / faster idle.
    for key in ("window_animation_scale", "transition_animation_scale", "animator_duration_scale"):
        subprocess.run(
            [ADB, "-s", serial, "shell", "settings", "put", "global", key, "0"],
            capture_output=True,
        )
    ok = fail = 0
    for sid in P1:
        if not alive(serial):
            log(f"lost before {sid}")
            fail += 1
            continue
        log(f"capture {sid}")
        r = subprocess.run(
            ["bash", str(ROOT / "scripts/adb_screenshot_preview.sh"), sid, "ru"],
            cwd=str(ROOT),
            env=env,
            stdin=subprocess.DEVNULL,
            capture_output=True,
            text=True,
            timeout=90,
        )
        out = (r.stdout + r.stderr).strip()
        if out:
            log(out[-300:])
        if r.returncode == 0:
            ok += 1
        else:
            fail += 1
            log(f"WARN {sid} rc={r.returncode}")
    return ok, fail


def audit() -> int:
    n = 0
    for sid in P1:
        p = OUT_QA / f"{sid}.png"
        kb = p.stat().st_size // 1024 if p.exists() else 0
        flag = "OK" if kb >= 80 else ("MISSING" if kb == 0 else "SMALL")
        if kb >= 80:
            n += 1
        log(f"{flag} {sid}.png {kb}KB")
    log(f"AUDIT {n}/{len(P1)}")
    return n


def try_save_snapshot(serial: str) -> None:
    """Best-effort: save working boot snapshot for next runs."""
    if not alive(serial):
        return
    log("snapshot save emp_ready_p1 (best-effort)")
    r = subprocess.run(
        [ADB, "-s", serial, "emu", "avd", "snapshot", "save", "emp_ready_p1"],
        capture_output=True,
        text=True,
        timeout=60,
    )
    log(f"snapshot save rc={r.returncode} {(r.stdout + r.stderr).strip()[:200]}")


def main() -> int:
    LOG.write_text(f"=== p1 api34 recapture {time.strftime('%Y-%m-%d %H:%M:%S')} ===\n")
    os.chdir(ROOT)
    os.environ["PATH"] = (
        f"{ANDROID_HOME / 'platform-tools'}:{ANDROID_HOME / 'emulator'}:"
        f"{os.environ.get('PATH', '')}"
    )
    Path("/tmp/lovetest-p1-api34-recapture.pid").write_text(str(os.getpid()))
    OUT_QA.mkdir(parents=True, exist_ok=True)

    serial = None
    proc = None
    for i in range(1, 150):
        try:
            f = free_mb()
            q = qemu_count()
            try:
                h = hitch_api34()
            except Exception as exc:  # noqa: BLE001
                h = None
                log(f"hitch err: {exc}")
            log(f"wait {i} free={f} qemu={q} hitch={h}")
            if h and h != SERIAL:
                avd = subprocess.run(
                    [ADB, "-s", h, "shell", "getprop", "ro.boot.qemu.avd_name"],
                    capture_output=True,
                    text=True,
                    timeout=8,
                ).stdout.strip()
                if "LoveTester" in avd or "MBN_Gate" in avd or "VisCap" in avd:
                    serial = h
                    log(f"USE hitch {serial} avd={avd}")
                    break
            if alive(SERIAL):
                serial = SERIAL
                log(f"USE existing {SERIAL}")
                break
            if q == 0 and f >= MIN_FREE:
                proc = boot_cold()
                if wait_boot(SERIAL, proc):
                    serial = SERIAL
                    break
                proc = None
        except Exception as exc:  # noqa: BLE001
            log(f"wait loop err: {exc}")
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
    ok, fail = capture_p1(serial)
    log(f"CAPTURE_RERUN ok={ok} fail={fail}")
    n = audit()
    expected = len(P1)
    if n >= expected and serial == SERIAL and expected == len(P1_DEFAULT):
        try_save_snapshot(serial)
    log("DONE")
    return 0 if fail == 0 and n >= expected else 5


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except KeyboardInterrupt:
        log("interrupted")
        raise SystemExit(130)
