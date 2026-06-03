#!/usr/bin/env bash
# Подсчёт unit / androidTest (Compose UI + route smoke) в :app.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

python3 - <<'PY'
import re
from pathlib import Path

root = Path(".")


def count_tests_in(path: Path) -> int:
    total = 0
    if path.is_file():
        text = path.read_text(encoding="utf-8")
        return len(re.findall(r"^\s*@Test\b", text, flags=re.MULTILINE))
    if not path.is_dir():
        return 0
    for file in path.rglob("*.kt"):
        text = file.read_text(encoding="utf-8")
        total += len(re.findall(r"^\s*@Test\b", text, flags=re.MULTILINE))
    return total


def count_tests_excluding(path: Path, exclude_globs: list[str]) -> int:
    total = 0
    if not path.is_dir():
        return 0
    exclude = {p.resolve() for pattern in exclude_globs for p in path.glob(pattern)}
    for file in path.rglob("*.kt"):
        if file.resolve() in exclude:
            continue
        text = file.read_text(encoding="utf-8")
        total += len(re.findall(r"^\s*@Test\b", text, flags=re.MULTILINE))
    return total


android_test = root / "app/src/androidTest"
route_smoke_file = android_test / "java/dev/lovetest/app/DebugStartRouteInstrumentedTest.kt"

unit = count_tests_in(root / "app/src/test")
android_test_total = count_tests_in(android_test)
route_smoke = count_tests_in(route_smoke_file) if route_smoke_file.is_file() else 0
compose_ui = count_tests_excluding(android_test, ["**/DebugStartRouteInstrumentedTest.kt"])
compose_classes = sum(1 for _ in android_test.rglob("*ComposeTest*.kt")) if android_test.is_dir() else 0

print(f"unit={unit}")
print(f"android_test={android_test_total}")
print(f"compose_ui={compose_ui}")
print(f"route_smoke={route_smoke}")
print(f"compose_classes={compose_classes}")
print(
    f"summary={unit} unit · {android_test_total} instrumented "
    f"({compose_ui} Compose UI + {route_smoke} route smoke)"
)
PY
