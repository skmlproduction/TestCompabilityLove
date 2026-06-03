#!/usr/bin/env python3
"""
Копирует PNG из каталога артефакта (screen_id.png) в пути docs/screenshots/*
по docs/product/screens_catalog.csv.

Пример:
  python3 scripts/apply_emulator_screenshot_artifact.py ./ci-screenshots --locales en
  python3 scripts/apply_emulator_screenshot_artifact.py ~/Downloads/screenshots-en --locales ru en
"""
from __future__ import annotations

import argparse
import csv
import shutil
import struct
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CSV_PATH = ROOT / "docs/product/screens_catalog.csv"
_PLAY_MIN_SHORT_SIDE = 320
_PLACEHOLDER_MAX_BYTES = 32_000


def read_png_info(path: Path) -> tuple[int, int, int] | None:
    try:
        data = path.read_bytes()
    except OSError:
        return None
    if len(data) < 24 or data[:8] != b"\x89PNG\r\n\x1a\n":
        return None
    w, h = struct.unpack(">II", data[16:24])
    return w, h, len(data)


def catalog_targets_by_screen_id() -> dict[str, dict[str, Path | None]]:
    out: dict[str, dict[str, Path | None]] = {}
    with CSV_PATH.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f):
            sid = (row.get("screen_id") or "").strip()
            if not sid:
                continue
            bucket = out.setdefault(sid, {"ru": None, "en": None})
            ru = (row.get("screenshot_ru_relative") or "").strip()
            en = (row.get("screenshot_en_relative") or "").strip()
            if ru and "N/A" not in ru.upper():
                bucket["ru"] = ROOT / ru
            if en and "N/A" not in en.upper():
                bucket["en"] = ROOT / en
    return out


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Copy CI emulator PNGs (screen_id.png) into docs/screenshots/* from CSV.",
    )
    parser.add_argument("artifact_dir", type=Path, help="Directory with screen_id.png files.")
    parser.add_argument(
        "--locales",
        nargs="+",
        choices=("ru", "en"),
        default=["en"],
        help="Target locale folders from CSV (default: en).",
    )
    parser.add_argument("--dry-run", action="store_true", help="Print actions without writing.")
    args = parser.parse_args()

    if not CSV_PATH.is_file():
        print(f"Missing {CSV_PATH}", file=sys.stderr)
        return 1

    ad = args.artifact_dir.resolve()
    if not ad.is_dir():
        print(f"Not a directory: {ad}", file=sys.stderr)
        return 1

    catalog = catalog_targets_by_screen_id()
    copied = 0
    for png in sorted(ad.glob("*.png")):
        sid = png.stem
        if sid not in catalog:
            print(f"skip (unknown screen_id): {png.name}", file=sys.stderr)
            continue

        info = read_png_info(png)
        if info is None:
            print(f"skip (not PNG): {png.name}", file=sys.stderr)
            continue
        w, h, size = info
        short = min(w, h)
        if short < _PLAY_MIN_SHORT_SIDE:
            print(
                f"warn: {png.name} is {w}x{h} (short side {short} < {_PLAY_MIN_SHORT_SIDE})",
                file=sys.stderr,
            )
        if (w, h) == (1080, 1920) and size < _PLACEHOLDER_MAX_BYTES:
            print(f"warn: {png.name} looks like placeholder ({size} B)", file=sys.stderr)

        for loc in args.locales:
            dest = catalog[sid].get(loc)
            if dest is None:
                print(f"skip {png.name} -> no {loc} path in CSV", file=sys.stderr)
                continue
            dest.parent.mkdir(parents=True, exist_ok=True)
            if args.dry_run:
                print(f"would copy {png.name} -> {dest.relative_to(ROOT)}")
            else:
                shutil.copy2(png, dest)
                print(f"copied {png.name} -> {dest.relative_to(ROOT)}")
            copied += 1

    if copied == 0:
        print("apply_emulator_screenshot_artifact: no files copied.", file=sys.stderr)
        return 1
    print(f"apply_emulator_screenshot_artifact: done ({copied} target(s)).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
