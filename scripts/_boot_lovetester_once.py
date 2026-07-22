#!/usr/bin/env python3
"""Boot LoveTester_Lite (1024MB) and write status to /tmp/lt-boot-once-*."""
from __future__ import annotations

import os
import re
import signal
import subprocess
import sys
import time
from pathlib import Path

SDK = Path.home() / "Library/Android/sdk"
EMU = SDK / "emulator" / "emulator"
ADB = SDK / "platform-tools" / "adb"
STATUS = Path("/tmp/lt-boot-once-status.txt")
LOG = Path("/tmp/lt-boot-once.log")
SERIAL = "emulator-5554"
AVD = "LoveTester_Lite"


def free_mb() -> float:
    vm = subprocess.check_output(["vm_stat"], text=True)
    m = re.search(r"Pages free:\s+(\d+)", vm)
    return round(int(m.group(1)) * 16384 / 1024 / 1024, 1) if m else -1


def kill_rivals() -> None:
    for line in subprocess.check_output(
        ["ps", "-ax", "-o", "pid=,command="], text=True, errors="replace"
    ).splitlines():
        parts = line.split(None, 1)
        if len(parts) < 2:
            continue
        pid_s, cmd = parts
        if "/qemu-system-" not in cmd or "/bin/zsh" in cmd:
            continue
        if AVD in cmd or "LoveTester_Capture" in cmd:
            continue
        try:
            os.kill(int(pid_s), signal.SIGKILL)
        except ProcessLookupError:
            pass


def love_alive() -> bool:
    for line in subprocess.check_output(
        ["ps", "-ax", "-o", "command="], text=True, errors="replace"
    ).splitlines():
        if "/qemu-system-" in line and AVD in line and "/bin/zsh" not in line:
            return True
    return False


def main() -> int:
    LOG.write_text(f"free={free_mb()} avd={AVD}\n", encoding="utf-8")
    STATUS.write_text("booting\n", encoding="utf-8")
    for _ in range(6):
        kill_rivals()
        time.sleep(0.3)
    subprocess.run([str(ADB), "start-server"], check=False, capture_output=True)
    emu_log = open("/tmp/lovetest-emulator.log", "w")
    p = subprocess.Popen(
        [
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
            "1024",
            "-cores",
            "2",
            "-gpu",
            "swiftshader_indirect",
        ],
        stdout=emu_log,
        stderr=subprocess.STDOUT,
        start_new_session=True,
    )
    with LOG.open("a") as f:
        f.write(f"emu_pid={p.pid} free={free_mb()}\n")

    wd = subprocess.Popen(
        [
            sys.executable,
            "-c",
            "import os,signal,subprocess,time\n"
            f"AVD={AVD!r}\n"
            "while True:\n"
            "  out=subprocess.check_output(['ps','-ax','-o','pid=,command='],text=True,errors='replace')\n"
            "  for line in out.splitlines():\n"
            "    parts=line.split(None,1)\n"
            "    if len(parts)<2: continue\n"
            "    pid_s,cmd=parts\n"
            "    if '/qemu-system-' not in cmd or AVD in cmd or 'LoveTester_Capture' in cmd or '/bin/zsh' in cmd: continue\n"
            "    try: os.kill(int(pid_s),signal.SIGKILL)\n"
            "    except ProcessLookupError: pass\n"
            "  time.sleep(1)\n",
        ],
        start_new_session=True,
        stdout=open("/tmp/lt-boot-once-wd.log", "w"),
        stderr=subprocess.STDOUT,
    )
    with LOG.open("a") as f:
        f.write(f"wd_pid={wd.pid}\n")

    for i in range(1, 70):
        kill_rivals()
        try:
            boot = subprocess.check_output(
                [str(ADB), "-s", SERIAL, "shell", "getprop", "sys.boot_completed"],
                text=True,
                timeout=5,
                errors="replace",
            ).strip()
        except Exception:
            boot = ""
        alive = love_alive()
        with LOG.open("a") as f:
            f.write(f"iter={i} boot={boot!r} love={alive} free={free_mb()}\n")
        if boot == "1":
            STATUS.write_text("boot_ok\n", encoding="utf-8")
            return 0
        if not alive and i > 4:
            STATUS.write_text("qemu_dead\n", encoding="utf-8")
            try:
                wd.kill()
            except Exception:
                pass
            return 3
        time.sleep(3)
    STATUS.write_text("timeout\n", encoding="utf-8")
    return 2


if __name__ == "__main__":
    sys.exit(main())
