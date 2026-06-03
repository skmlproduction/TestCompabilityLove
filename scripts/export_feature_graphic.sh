#!/usr/bin/env bash
# Экспорт feature graphic 1024×500 PNG для Google Play.
# Usage: ./scripts/export_feature_graphic.sh [output.png]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SVG="${ROOT}/docs/store/feature_graphic_love_tester_m3.svg"
OUT="${1:-${ROOT}/docs/store/feature_graphic.png}"

if [[ ! -f "${SVG}" ]]; then
  echo "Missing ${SVG}" >&2
  exit 1
fi

mkdir -p "$(dirname "${OUT}")"

if command -v rsvg-convert >/dev/null 2>&1; then
  rsvg-convert -w 1024 -h 500 "${SVG}" -o "${OUT}"
elif command -v magick >/dev/null 2>&1; then
  magick -background none "${SVG}" -resize 1024x500 "${OUT}"
elif command -v convert >/dev/null 2>&1; then
  convert -background none "${SVG}" -resize 1024x500 "${OUT}"
else
  echo "export_feature_graphic: install rsvg-convert or ImageMagick, then re-run." >&2
  echo "  brew install librsvg    # rsvg-convert" >&2
  echo "  brew install imagemagick  # magick" >&2
  echo "Or export manually from Figma/Inkscape: ${SVG}" >&2
  exit 1
fi

python3 - <<PY
import struct
from pathlib import Path
p = Path("${OUT}")
head = p.read_bytes()[:24]
w, h = struct.unpack(">II", head[16:24])
print(f"export_feature_graphic: {p} ({w}x{h}, {p.stat().st_size} bytes)")
PY
