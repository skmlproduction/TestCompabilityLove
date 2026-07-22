#!/usr/bin/env python3
import os, re, subprocess, sys, time
from pathlib import Path

ROOT = Path("/Users/maksimsokolov/Desktop/TestAppsCursor/TestCompabilityLove")
LOG = Path("/tmp/lovetest-hitchhike-run.log")
ANDROID_HOME = Path.home() / "Library/Android/sdk"
ADB = str(ANDROID_HOME / "platform-tools/adb")
MIN_FREE = int(os.environ.get("LOVETEST_MIN_HOST_FREE_MB", "850"))

def log(m):
    line = f"{time.strftime('%H:%M:%S')} {m}"
    print(line, flush=True)
    LOG.open("a").write(line + "\n")

def free_mb():
    out = subprocess.check_output(["vm_stat"], text=True)
    page = 16384; free = 0
    for line in out.splitlines():
        if "page size of" in line:
            m = re.search(r"(\d+)", line)
            if m: page = int(m.group(1))
        if line.startswith("Pages free:") or line.startswith("Pages speculative:"):
            free += int(line.split(":")[1].strip().rstrip("."))
    return int(free * page / 1024 / 1024)

def adb(*a, timeout=60):
    return subprocess.run([ADB, *a], text=True, capture_output=True, timeout=timeout)

def pick_serial():
    r = adb("devices")
    for line in r.stdout.splitlines():
        p = line.split()
        if len(p) >= 2 and p[0].startswith("emulator-") and p[1] == "device":
            boot = adb("-s", p[0], "shell", "getprop", "sys.boot_completed")
            if boot.stdout.strip() == "1":
                return p[0]
    return None

def daemonize():
    if os.fork() > 0: sys.exit(0)
    os.setsid()
    if os.fork() > 0: sys.exit(0)
    sys.stdout.flush(); sys.stderr.flush()
    with LOG.open("a") as f:
        os.dup2(f.fileno(), 1); os.dup2(f.fileno(), 2)

def patch_heap(heap, kheap):
    gp = Path.home() / ".gradle/gradle.properties"
    bak = gp.with_suffix(gp.suffix + ".bak-lt")
    t = gp.read_text(); bak.write_text(t)
    t = re.sub(r"org\.gradle\.jvmargs=.*",
               f"org.gradle.jvmargs=-Xmx{heap}m -XX:MaxMetaspaceSize=160m -XX:-TieredCompilation -Dfile.encoding=UTF-8", t)
    t = re.sub(r"kotlin\.daemon\.jvmargs=.*\n?", "", t)
    t += f"\nkotlin.daemon.jvmargs=-Xmx{kheap}m -XX:MaxMetaspaceSize=96m -XX:-TieredCompilation -Dfile.encoding=UTF-8\n"
    t = re.sub(r"org\.gradle\.workers\.max=.*", "org.gradle.workers.max=1", t)
    t = re.sub(r"org\.gradle\.parallel=.*", "org.gradle.parallel=false", t)
    gp.write_text(t)
    return bak

def restore(bak):
    gp = Path.home() / ".gradle/gradle.properties"
    if bak.exists():
        gp.write_text(bak.read_text()); bak.unlink(missing_ok=True)

P1 = ["pair_input","consent_ads_gdpr","love_test_result_low","calculator_result","protocol_result","zodiac_result","pair_result","letters_result","victory_result","hub_main","love_test_result","premium_paywall"]
CLS = [
  "dev.lovetest.app.navigation.CalculatorFlowComposeTest",
  "dev.lovetest.app.navigation.ProtocolFlowComposeTest",
  "dev.lovetest.app.navigation.ZodiacFlowComposeTest",
  "dev.lovetest.app.ui.consent.ConsentScreenComposeTest",
]

def main():
    LOG.write_text(f"=== hitch wait {time.strftime('%Y-%m-%d %H:%M:%S')} ===\n")
    daemonize()
    Path("/tmp/lovetest-hitch.pid").write_text(str(os.getpid()))
    os.chdir(ROOT)
    os.environ["PATH"] = f"{ANDROID_HOME/'platform-tools'}:{os.environ.get('PATH','')}"
    serial = None
    for i in range(1, 180):
        f = free_mb()
        serial = pick_serial()
        log(f"iter={i} free={f} serial={serial or 'none'} need>={MIN_FREE}")
        if serial and f >= MIN_FREE:
            break
        time.sleep(10)
    else:
        log("NOT READY"); return 2
    os.environ["ANDROID_SERIAL"] = serial
    heap, kheap = (512, 256) if f >= 1000 else (384, 192)
    log(f"GO install on {serial} heap={heap} free={f}")
    bak = patch_heap(heap, kheap)
    try:
        subprocess.run([str(ROOT/"gradlew"), "--stop"], cwd=ROOT)
        r = subprocess.run([str(ROOT/"gradlew"), ":app:installDebug", ":app:installDebugAndroidTest", "--max-workers=1"], cwd=ROOT)
        log(f"install_rc={r.returncode}")
        if r.returncode != 0: return r.returncode
    finally:
        restore(bak)
    out = ROOT / "docs/screenshots/qa/emulator/ru"
    out.mkdir(parents=True, exist_ok=True)
    os.environ["LOVETEST_SCREENSHOT_OUT_DIR"] = str(out)
    os.environ["LOVETEST_USE_EMULATOR"] = "1"
    for sid in P1:
        log(f"capture {sid}")
        subprocess.run(["bash", "scripts/adb_screenshot_preview.sh", sid, "ru"], cwd=ROOT, stdin=subprocess.DEVNULL)
    adb("-s", serial, "shell", "am", "force-stop", "com.hyperlockscreen.app")
    ok=fail=0
    for cls in CLS:
        log(f"test {cls}")
        r = adb("-s", serial, "shell", "am", "instrument", "-w", "-r", "-e", "class", cls,
                "dev.lovetest.app.test/androidx.test.runner.AndroidJUnitRunner", timeout=300)
        text = r.stdout + r.stderr
        log("\n".join(text.splitlines()[-25:]))
        if "OK (" in text: ok += 1
        else: fail += 1
    log(f"CRITICAL_RERUN ok={ok} fail={fail}")
    log("DONE")
    return 0 if fail == 0 else 5

if __name__ == "__main__":
    sys.exit(main() or 0)
