#!/usr/bin/env bash
# Список screen_id из каталога для съёмки.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
python3 - "$ROOT" <<'PY'
import csv
from pathlib import Path
root = Path(__import__("sys").argv[1])
with open(root / "docs/product/screens_catalog.csv", encoding="utf-8") as f:
    for row in csv.DictReader(f):
        sid = row["screen_id"]
        en = row.get("screenshot_en_relative", "")
        en_tag = "" if "N/A" in en.upper() else " ru+en"
        print(f"{row['screen_no']:>2}. {sid}{en_tag}")
PY
