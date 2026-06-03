#!/usr/bin/env bash
# Сводный чеклист Play Console с текущим статусом репозитория.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"
# shellcheck source=scripts/android_sdk_env.sh
source "${ROOT}/scripts/android_sdk_env.sh"

echo "=== Love Tester — Play Console checklist ==="
echo ""

python3 - <<'PY'
import csv
import re
import struct
from pathlib import Path

root = Path(".")

def read_prop(key: str) -> str:
    props = root / "gradle.properties"
    if not props.is_file():
        return ""
    for line in props.read_text(encoding="utf-8").splitlines():
        if line.strip().startswith(f"{key}="):
            return line.split("=", 1)[1].strip()
    return ""

privacy = read_prop("lovetest.privacy.policy.url")
if not privacy:
    privacy_status = "MISSING"
elif privacy.lower() == "https://example.com/privacy":
    privacy_status = "PLACEHOLDER (example.com)"
else:
    privacy_status = f"OK ({privacy})"

ks = root / "keystore.properties"
store = ""
if ks.is_file():
    for line in ks.read_text(encoding="utf-8").splitlines():
        if line.startswith("storeFile="):
            store = line.split("=", 1)[1].strip()
            break
    keystore_status = f"OK ({store})" if store and (root / store).is_file() else "INVALID storeFile"
else:
    keystore_status = "MISSING"

placeholder = real = missing = catalog_total = 0
with (root / "docs/product/screens_catalog.csv").open(encoding="utf-8", newline="") as f:
    for row in csv.DictReader(f):
        for key in ("screenshot_ru_relative", "screenshot_en_relative"):
            rel = (row.get(key) or "").strip()
            if not rel or "N/A" in rel.upper():
                continue
            catalog_total += 1
            p = root / rel
            if not p.is_file():
                missing += 1
                continue
            head = p.read_bytes()[:24]
            if len(head) < 24 or head[:8] != b"\x89PNG\r\n\x1a\n":
                missing += 1
                continue
            w, h = struct.unpack(">II", head[16:24])
            if (w, h) == (1080, 1920) and p.stat().st_size < 32_000:
                placeholder += 1
            elif w == 1080 and h >= 1920 and p.stat().st_size >= 32_000:
                real += 1
            else:
                missing += 1

if placeholder == 0 and real >= catalog_total and catalog_total > 0:
    png_status = f"OK ({real}/{catalog_total})"
elif real > 0:
    png_status = f"PARTIAL real={real}, placeholder={placeholder}, missing={missing}"
else:
    png_status = f"NEED CAPTURE placeholder={placeholder}, missing={missing}"

is_debug_ks = "debug" in keystore_status.lower() or "upload-debug" in keystore_status.lower()
if keystore_status.startswith("OK") and is_debug_ks:
    keystore_status = f"DEBUG ({store}) — замените на production upload key"
elif keystore_status.startswith("OK"):
    keystore_status = f"OK ({store})"

aab = sorted((root / "app/build/outputs/bundle/release").glob("*.aab")) if (root / "app/build/outputs/bundle/release").is_dir() else []
fg = root / "docs/store/feature_graphic.png"
legal = root / "build/legal-host/index.html"

upload_dir = root / "build/store-upload"
upload_aab = sorted(upload_dir.glob("*.aab")) if upload_dir.is_dir() else []
zip_path = root / "build/love-tester-store-upload.zip"
if upload_aab and zip_path.is_file() and zip_path.stat().st_size >= 1_000_000:
    upload_status = f"OK ({upload_dir.name}/ + {zip_path.name})"
elif upload_aab:
    upload_status = f"PARTIAL ({upload_dir.name}/ — ./gradlew zipStoreUploadLoveTest)"
elif upload_dir.is_dir():
    upload_status = "PARTIAL (пусто — ./scripts/pack_store_upload.sh)"
else:
    upload_status = "not built — ./gradlew packStoreUploadLoveTest"

checks = [
    ("Privacy URL", privacy_status),
    ("Keystore", keystore_status),
    ("Store PNG", png_status),
    ("Feature graphic", f"OK ({fg.stat().st_size} B)" if fg.is_file() and fg.stat().st_size >= 50_000 else "MISSING/small"),
    ("Release AAB", f"OK ({aab[-1].name})" if aab else "not built — ./gradlew bundleReleaseLoveTest"),
    ("Legal host export", "OK" if legal.is_file() else "./gradlew exportPrivacyForHosting"),
    ("Upload pack", upload_status),
]

def is_ok(label: str, status: str) -> bool:
    if status.startswith("OK"):
        return True
    return False

for label, status in checks:
    mark = "✓" if is_ok(label, status) else "○"
    print(f"  [{mark}] {label}: {status}")

adb = "OK" if __import__("shutil").which("adb") else "not in PATH — ./scripts/setup_android_sdk.sh"
adb_mark = "✓" if adb == "OK" else "○"
print(f"  [{adb_mark}] adb: {adb}")

import subprocess
git_ok = False
try:
    subprocess.run(["git", "rev-parse", "--is-inside-work-tree"], capture_output=True, check=True)
    git_ok = True
except (subprocess.CalledProcessError, FileNotFoundError):
    pass
if git_ok:
    head = subprocess.run(
        ["git", "rev-parse", "HEAD"],
        capture_output=True,
        text=True,
    )
    remote = subprocess.run(
        ["git", "remote", "get-url", "origin"],
        capture_output=True,
        text=True,
    )
    if head.returncode != 0:
        git_status = "no commits — git add . && git commit"
        git_needs_push = False
        git_has_remote = remote.returncode == 0
    elif remote.returncode != 0:
        git_status = "no remote — ./scripts/first_push.sh USER REPO"
        git_needs_push = False
        git_has_remote = False
    else:
        git_has_remote = True
        upstream = subprocess.run(
            ["git", "rev-parse", "--abbrev-ref", "@{u}"],
            capture_output=True,
            text=True,
        )
        if upstream.returncode != 0:
            git_status = f"needs push ({remote.stdout.strip()})"
            git_needs_push = True
        else:
            ahead = subprocess.run(
                ["git", "rev-list", "--count", "@{u}..HEAD"],
                capture_output=True,
                text=True,
            )
            ahead_count = int(ahead.stdout.strip() or "0") if ahead.returncode == 0 else 0
            if ahead_count > 0:
                git_status = f"ahead {ahead_count} — git push -u origin main"
                git_needs_push = True
            else:
                git_status = f"OK ({remote.stdout.strip()})"
                git_needs_push = False
else:
    git_status = "not init — ./scripts/init_git_for_github.sh"
    git_needs_push = False
    git_has_remote = False
git_mark = "✓" if git_status.startswith("OK") else "○"
print(f"  [{git_mark}] git: {git_status}")

import re

def count_tests_in(path: Path) -> int:
    if path.is_file():
        text = path.read_text(encoding="utf-8")
        return len(re.findall(r"^\s*@Test\b", text, flags=re.MULTILINE))
    total = 0
    if not path.is_dir():
        return 0
    for file in path.rglob("*.kt"):
        text = file.read_text(encoding="utf-8")
        total += len(re.findall(r"^\s*@Test\b", text, flags=re.MULTILINE))
    return total

def count_tests_excluding(path: Path, exclude_name: str) -> int:
    total = 0
    if not path.is_dir():
        return 0
    for file in path.rglob("*.kt"):
        if file.name == exclude_name:
            continue
        text = file.read_text(encoding="utf-8")
        total += len(re.findall(r"^\s*@Test\b", text, flags=re.MULTILINE))
    return total

unit_tests = count_tests_in(root / "app/src/test")
android_test = count_tests_in(root / "app/src/androidTest")
compose_ui_tests = count_tests_excluding(root / "app/src/androidTest", "DebugStartRouteInstrumentedTest.kt")
route_smoke_tests = count_tests_in(root / "app/src/androidTest/java/dev/lovetest/app/DebugStartRouteInstrumentedTest.kt")
compose_classes = sum(
    1 for _ in (root / "app/src/androidTest").rglob("*ComposeTest*.kt")
) if (root / "app/src/androidTest").is_dir() else 0
quality_status = (
    f"OK ({unit_tests} unit · {android_test} instrumented: "
    f"{compose_ui_tests} Compose UI + {route_smoke_tests} route smoke)"
)
quality_mark = "✓" if unit_tests >= 20 and android_test >= 45 else "○"
print(f"  [{quality_mark}] Tests: {quality_status}")
print(f"      Route smoke: ./scripts/run_route_smoke_tests.sh")
print(f"      Compose UI: ./scripts/run_compose_ui_tests.sh")

print("")
print("Блокеры Play:")
blockers = []
if git_ok and (not git_status.startswith("OK") or git_needs_push):
    if "no remote" in git_status or "not init" in git_status:
        blockers.append("Git remote — ./scripts/first_push.sh USER REPO")
    elif "no commits" in git_status:
        blockers.append("Git commit — git add . && git commit")
    elif git_needs_push:
        blockers.append("Git push — git push -u origin main → ./scripts/post_push.sh")
if not privacy or privacy.lower() == "https://example.com/privacy":
    if git_ok and git_status.startswith("OK") and not git_needs_push:
        blockers.append("Privacy URL — ./scripts/post_push.sh → post_privacy_setup.sh")
    else:
        blockers.append("Privacy URL — после push: ./scripts/post_push.sh")
if "DEBUG" in keystore_status or keystore_status in ("MISSING",) or keystore_status.startswith("INVALID"):
    if "DEBUG" in keystore_status:
        blockers.append("Production keystore — ./scripts/generate_upload_keystore.sh")
    elif keystore_status != f"OK ({store})":
        blockers.append(f"Keystore — {keystore_status}")
if not png_status.startswith("OK"):
    blockers.append(f"Store PNG — {png_status}")
if blockers:
    for b in blockers:
        print(f"  • {b}")
else:
    print("  (нет — загрузите build/store-upload/)")
PY

echo ""
echo "Команды:"
echo "  ./scripts/play_console_next.sh              # один следующий шаг"
echo "  ./scripts/first_push.sh USER REPO [--fast]    # до первого push"
echo "  ./scripts/post_push.sh                        # после git push"
echo "  ./scripts/post_privacy_setup.sh https://…/"
echo "  ./gradlew finalizeStoreReleaseLoveTest"
echo "  docs/store/PLAY_READY.md · INTERNAL_TESTING.md"
