#!/usr/bin/env python3
"""
Love Tester — проверки инвентаря UI.

  python3 scripts/verify_ui_inventory.py --inventory-only   # до F3 (только CSV)
  python3 scripts/verify_ui_inventory.py                    # полная проверка после scaffold

Запуск из корня репозитория.
"""
from __future__ import annotations

import argparse
import csv
import re
import struct
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SCREENSHOT_CSV = ROOT / "docs/product/screens_catalog.csv"
ROUTES_PATH = ROOT / "app/src/main/java/dev/lovetest/app/navigation/Routes.kt"
NAV_PATH = ROOT / "app/src/main/java/dev/lovetest/app/navigation/LoveTestNavHost.kt"
MANIFEST_PATH = ROOT / "app/src/main/AndroidManifest.xml"

REQUIRED_CSV_COLUMNS = {
    "screen_no",
    "screen_id",
    "route_const",
    "route_path",
    "kotlin_ui_file",
    "manifest_activity_or_service",
    "top_bar_or_title_strings",
    "screenshot_ru_relative",
    "screenshot_en_relative",
    "design_svg",
}


def verify_design_svg_files() -> list[str]:
    errs: list[str] = []
    design_dir = ROOT / "docs/design"
    with SCREENSHOT_CSV.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f):
            sid = row.get("screen_id", "")
            svg = (row.get("design_svg") or "").strip()
            if not svg:
                errs.append(f"screens_catalog.csv: пустой design_svg для {sid!r}")
                continue
            path = design_dir / svg
            if not path.is_file():
                errs.append(f"Нет SVG для screen_id={sid!r}: {path}")
    return errs


def verify_csv_structure() -> list[str]:
    errs: list[str] = []
    if not SCREENSHOT_CSV.is_file():
        return [f"Нет файла {SCREENSHOT_CSV}"]
    with SCREENSHOT_CSV.open(encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        if not reader.fieldnames:
            return ["screens_catalog.csv: пустой заголовок"]
        missing_cols = REQUIRED_CSV_COLUMNS - set(reader.fieldnames)
        if missing_cols:
            errs.append(f"screens_catalog.csv: нет колонок {sorted(missing_cols)}")
        seen_ids: set[str] = set()
        seen_nos: set[str] = set()
        rows = list(reader)
        if len(rows) < 34:
            errs.append(f"screens_catalog.csv: ожидалось ≥34 screen_id, есть {len(rows)}")
        for row in rows:
            sno = (row.get("screen_no") or "").strip()
            sid = (row.get("screen_id") or "").strip()
            if sno:
                if sno in seen_nos:
                    errs.append(f"screens_catalog.csv: дубликат screen_no={sno!r}")
                seen_nos.add(sno)
                if not sno.isdigit():
                    errs.append(f"screen_no не число: {sno!r}")
            if not sid:
                errs.append("screens_catalog.csv: пустой screen_id")
                continue
            if sid in seen_ids:
                errs.append(f"screens_catalog.csv: дубликат screen_id={sid!r}")
            seen_ids.add(sid)
            if not re.fullmatch(r"[a-z][a-z0-9_]*", sid):
                errs.append(f"screen_id не snake_case: {sid!r}")
        if seen_nos:
            nums = sorted(int(n) for n in seen_nos)
            expected = {str(i) for i in range(1, nums[-1] + 1)}
            if seen_nos != expected:
                missing = sorted(expected - seen_nos, key=int)
                extra = sorted(seen_nos - expected, key=int)
                if missing:
                    errs.append(f"screens_catalog.csv: нет screen_no: {missing}")
                if extra:
                    errs.append(f"screens_catalog.csv: лишние screen_no: {extra}")
    return errs


def parse_route_constants(routes_kt: str) -> dict[str, str]:
    out: dict[str, str] = {}
    for m in re.finditer(r"const val (\w+)\s*=\s*\"([^\"]+)\"", routes_kt):
        out[m.group(1)] = m.group(2)
    return out


def parse_all_destinations(routes_kt: str) -> list[str]:
    m = re.search(
        r"fun allDestinations\(\): List<String> = listOf\(\s*((?:\s*\w+\s*,?\s*)+)\)",
        routes_kt,
        re.DOTALL,
    )
    if not m:
        raise SystemExit("Routes.kt: не найдена allDestinations()")
    inner = m.group(1)
    routes = []
    for part in inner.split(","):
        part = part.strip()
        if not part:
            continue
        mm = re.match(r"Routes\.(\w+)", part)
        if mm:
            routes.append(mm.group(1))
            continue
        mm2 = re.match(r"(\w+)$", part)
        if mm2:
            routes.append(mm2.group(1))
            continue
        raise SystemExit(f"Routes.kt: неожиданный элемент в allDestinations: {part!r}")
    return routes


def parse_navhost_composable_route_names(nav_kt: str) -> list[str]:
    names = []
    for m in re.finditer(r"composable\s*\(\s*Routes\.(\w+)\s*\)", nav_kt):
        names.append(m.group(1))
    return names


def parse_manifest_component_names(manifest: str) -> set[str]:
    names: set[str] = set()
    for m in re.finditer(
        r"<(activity|service|receiver)\s[^>]*android:name=\"([^\"]+)\"",
        manifest,
    ):
        raw = m.group(2)
        if raw.startswith("."):
            names.add(f"dev.lovetest.app{raw}")
        else:
            names.add(raw)
    return names


def verify_screenshot_files_from_catalog() -> list[str]:
    errs: list[str] = []
    with SCREENSHOT_CSV.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f):
            sid = row.get("screen_id", "")
            for key in ("screenshot_ru_relative", "screenshot_en_relative"):
                raw = (row.get(key) or "").strip()
                if not raw or "N/A" in raw.upper():
                    continue
                p = ROOT / raw
                if not p.is_file():
                    errs.append(f"Нет скриншота {key} для screen_id={sid!r}: {p}")
    return errs


def _png_pixel_size(path: Path) -> tuple[int, int] | None:
    try:
        head = path.read_bytes()[:24]
    except OSError:
        return None
    if len(head) < 24 or head[:8] != b"\x89PNG\r\n\x1a\n":
        return None
    return struct.unpack(">II", head[16:24])


def warn_store_placeholder_pngs() -> list[str]:
    warns: list[str] = []
    with SCREENSHOT_CSV.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f):
            for key in ("screenshot_ru_relative", "screenshot_en_relative"):
                raw = (row.get(key) or "").strip()
                if not raw or "N/A" in raw.upper():
                    continue
                p = ROOT / raw
                if not p.is_file():
                    continue
                size = _png_pixel_size(p)
                if size == (1080, 1920) and p.stat().st_size < 32_000:
                    warns.append(
                        f"похоже на шаблон (не Store UI): {p.relative_to(ROOT)} "
                        f"({p.stat().st_size} bytes)",
                    )
    return warns


def verify_kotlin_graph() -> list[str]:
    routes_kt = ROUTES_PATH.read_text(encoding="utf-8")
    nav_kt = NAV_PATH.read_text(encoding="utf-8")
    manifest = MANIFEST_PATH.read_text(encoding="utf-8")

    const_to_path = parse_route_constants(routes_kt)
    listed = parse_all_destinations(routes_kt)
    composable_names = parse_navhost_composable_route_names(nav_kt)

    listed_set = {const_to_path[n] for n in listed if n in const_to_path}
    composable_set = {const_to_path[n] for n in composable_names if n in const_to_path}

    errors: list[str] = []
    missing_in_nav = listed_set - composable_set
    extra_in_nav = composable_set - listed_set
    if missing_in_nav:
        errors.append(
            f"Маршруты в allDestinations(), но нет composable(): {sorted(missing_in_nav)}",
        )
    if extra_in_nav:
        errors.append(
            f"composable() без allDestinations(): {sorted(extra_in_nav)}",
        )

    expected_manifest = {"dev.lovetest.app.MainActivity"}
    found = parse_manifest_component_names(manifest)
    missing_manifest = expected_manifest - found
    if missing_manifest:
        errors.append(f"AndroidManifest: не найдены: {sorted(missing_manifest)}")

    ru_path = ROOT / "app/src/main/res/values/strings.xml"
    en_path = ROOT / "app/src/main/res/values-en/strings.xml"
    if ru_path.exists() and en_path.exists():
        import xml.etree.ElementTree as ET

        def names_xml(p: Path) -> set[str]:
            root = ET.parse(p).getroot()
            return {e.attrib["name"] for e in root.findall("string") if "name" in e.attrib}

        only_ru = names_xml(ru_path) - names_xml(en_path)
        if only_ru:
            errors.append(
                f"strings: ключи только в values/: {sorted(only_ru)[:20]}"
                + (" …" if len(only_ru) > 20 else ""),
            )
    return errors


PROTOCOL_EN_SVGS = (
    "screen52_love_test_protocol_input_en_m3.svg",
    "screen53_love_test_protocol_calculating_en_m3.svg",
    "screen54_love_test_protocol_result_en_m3.svg",
    "screen55_love_test_protocol_result_low_en_m3.svg",
    "screen56_love_test_onboarding_protocol_en_m3.svg",
)


def warn_missing_en_protocol_svgs() -> list[str]:
    design = ROOT / "docs/design"
    warns: list[str] = []
    for name in PROTOCOL_EN_SVGS:
        if not (design / name).is_file():
            warns.append(f"Нет EN SVG протокола: docs/design/{name}")
    return warns


def main() -> int:
    parser = argparse.ArgumentParser(description="Love Tester UI inventory checks")
    parser.add_argument(
        "--inventory-only",
        action="store_true",
        help="Только CSV (до Android scaffold F3).",
    )
    parser.add_argument(
        "--require-screenshots",
        action="store_true",
        help="Требовать PNG из каталога (после materializeScreenshotPlaceholders).",
    )
    parser.add_argument(
        "--fail-on-placeholders",
        action="store_true",
        help="1080×1920 малый файл = ошибка (перед Play).",
    )
    args = parser.parse_args()

    errors = verify_csv_structure()
    errors.extend(verify_design_svg_files())
    if errors:
        print("verify_ui_inventory: FAIL", file=sys.stderr)
        for e in errors:
            print(f"  - {e}", file=sys.stderr)
        return 1

    scaffold_ready = ROUTES_PATH.is_file() and NAV_PATH.is_file()
    if args.inventory_only or not scaffold_ready:
        if not scaffold_ready and not args.inventory_only:
            print(
                "verify_ui_inventory: SKIP kotlin/manifest (F3 pending); "
                "CSV OK. Используйте --inventory-only явно.",
            )
        else:
            with SCREENSHOT_CSV.open(encoding="utf-8", newline="") as f:
                catalog_count = sum(1 for _ in csv.DictReader(f))
            print(f"verify_ui_inventory: OK (CSV + design SVG {catalog_count}/{catalog_count})")
        return 0

    errors.extend(verify_kotlin_graph())
    if args.require_screenshots:
        errors.extend(verify_screenshot_files_from_catalog())
    placeholder_warns = warn_store_placeholder_pngs()
    en_svg_warns = warn_missing_en_protocol_svgs()
    if args.fail_on_placeholders and placeholder_warns:
        errors.extend(placeholder_warns)

    if errors:
        print("verify_ui_inventory: FAIL", file=sys.stderr)
        for e in errors:
            print(f"  - {e}", file=sys.stderr)
        return 1

    msg = "verify_ui_inventory: OK (Routes ↔ NavHost, manifest, CSV)"
    if placeholder_warns and not args.fail_on_placeholders:
        msg += f"; WARN: {len(placeholder_warns)} PNG-шаблон(ов)"
    if en_svg_warns:
        for w in en_svg_warns:
            print(f"  warn: {w}", file=sys.stderr)
    print(msg)
    return 0


if __name__ == "__main__":
    sys.exit(main())
