#!/usr/bin/env python3
"""
Создаёт placeholder PNG 1080×1920 для всех путей из screens_catalog.csv.

  python3 scripts/write_screenshot_placeholders.py

Файлы маленькие (~8–12 KB) — verify_ui_inventory помечает их как шаблоны
до реальной съёмки через capture_screenshot_catalog.sh.
"""
from __future__ import annotations

import csv
import struct
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CSV_PATH = ROOT / "docs/product/screens_catalog.csv"
WIDTH = 1080
HEIGHT = 1920
# M3 pink tint #FCE4EC
FILL = bytes([0xFC, 0xE4, 0xEC])


def _png_chunk(tag: bytes, data: bytes) -> bytes:
    body = tag + data
    crc = zlib.crc32(body) & 0xFFFFFFFF
    return struct.pack(">I", len(data)) + body + struct.pack(">I", crc)


def write_placeholder_png(path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    row = bytearray()
    stride = WIDTH * 3
    for _ in range(HEIGHT):
        row.append(0)  # filter none
        row.extend(FILL * WIDTH)
    compressed = zlib.compress(bytes(row), 9)
    ihdr = struct.pack(">IIBBBBB", WIDTH, HEIGHT, 8, 2, 0, 0, 0)
    png = b"\x89PNG\r\n\x1a\n"
    png += _png_chunk(b"IHDR", ihdr)
    png += _png_chunk(b"IDAT", compressed)
    png += _png_chunk(b"IEND", b"")
    path.write_bytes(png)


def main() -> int:
    if not CSV_PATH.is_file():
        print(f"Missing {CSV_PATH}", flush=True)
        return 1
    paths: set[Path] = set()
    with CSV_PATH.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f):
            for key in ("screenshot_ru_relative", "screenshot_en_relative"):
                raw = (row.get(key) or "").strip()
                if not raw or "N/A" in raw.upper():
                    continue
                paths.add(ROOT / raw)
    for p in sorted(paths):
        write_placeholder_png(p)
    print(f"write_screenshot_placeholders: OK ({len(paths)} PNG)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
