#!/usr/bin/env python3
"""
F6 — матрица CSV ↔ SVG ↔ Kotlin ↔ PNG ↔ DebugUiPreview.

  python3 scripts/audit_screens_matrix.py
  python3 scripts/audit_screens_matrix.py --write docs/product/AUDIT_REPORT.md
"""
from __future__ import annotations

import argparse
import csv
import re
import struct
import sys
from dataclasses import dataclass, field
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CSV_PATH = ROOT / "docs/product/screens_catalog.csv"
DESIGN_DIR = ROOT / "docs/design"
FEATURES_DIR = ROOT / "app/src/main/java/dev/lovetest/app/ui"
DEBUG_PREVIEW = ROOT / "app/src/main/java/dev/lovetest/app/debug/DebugUiPreview.kt"
PLACEHOLDER_MAX_BYTES = 32_000


@dataclass
class Issue:
    severity: str  # P0, P1, P2
    screen_id: str
    message: str


@dataclass
class AuditResult:
    rows: list[dict] = field(default_factory=list)
    issues: list[Issue] = field(default_factory=list)


def _png_info(path: Path) -> tuple[bool, str, int]:
    if not path.is_file():
        return False, "missing", 0
    size = path.stat().st_size
    try:
        head = path.read_bytes()[:24]
    except OSError:
        return False, "read_error", size
    if len(head) < 24 or head[:8] != b"\x89PNG\r\n\x1a\n":
        return False, "not_png", size
    w, h = struct.unpack(">II", head[16:24])
    if (w, h) != (1080, 1920):
        return False, f"size_{w}x{h}", size
    if size < PLACEHOLDER_MAX_BYTES:
        return False, "placeholder", size
    return True, "ok", size


def _parse_debug_routes(text: str) -> set[str]:
    block = re.search(
        r"fun routeFor\(screenId: String\).*?when \(screenId\) \{(.*?)else -> null",
        text,
        re.DOTALL,
    )
    if not block:
        return set()
    return set(re.findall(r'"([a-z][a-z0-9_]*)"', block.group(1)))


def _find_kotlin(path_fragment: str) -> Path | None:
    name = path_fragment.strip()
    if not name or name == "N/A":
        return None
    for base in (FEATURES_DIR, ROOT / "app/src/main/java/dev/lovetest/app/ui"):
        for p in ROOT.glob(f"**/{name}"):
            if "build" not in p.parts:
                return p
    direct = ROOT / "app/src/main/java/dev/lovetest/app/ui" / name.replace(".kt", "")
    return None


def run_audit() -> AuditResult:
    result = AuditResult()
    debug_text = DEBUG_PREVIEW.read_text(encoding="utf-8") if DEBUG_PREVIEW.is_file() else ""
    debug_ids = _parse_debug_routes(debug_text)

    privacy_url = ""
    gradle_props = ROOT / "gradle.properties"
    if gradle_props.is_file():
        for line in gradle_props.read_text(encoding="utf-8").splitlines():
            if line.strip().startswith("lovetest.privacy.policy.url="):
                privacy_url = line.split("=", 1)[1].strip()
                break
    bundled_privacy = ROOT / "app/src/main/assets/legal/privacy_policy.html"
    if not privacy_url or privacy_url.startswith("#"):
        if bundled_privacy.is_file():
            result.issues.append(
                Issue(
                    "P2",
                    "—",
                    "lovetest.privacy.policy.url не задан — используется bundled assets/legal (для Play укажите URL)",
                ),
            )
        else:
            result.issues.append(
                Issue("P1", "—", "lovetest.privacy.policy.url не задан и нет bundled privacy_policy.html"),
            )
    elif privacy_url == "https://example.com/privacy":
        result.issues.append(
            Issue(
                "P2",
                "—",
                "lovetest.privacy.policy.url — placeholder example.com (замените на публичный HTTPS)",
            ),
        )

    with CSV_PATH.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f):
            sid = row["screen_id"]
            kotlin_file = (row.get("kotlin_ui_file") or "").strip()
            svg = (row.get("design_svg") or "").strip()
            route = (row.get("route_path") or "").strip()

            kotlin_ok = kotlin_file in ("N/A", "") or bool(
                list(ROOT.glob(f"**/{kotlin_file}"))
            )
            svg_ok = bool(svg) and (DESIGN_DIR / svg).is_file()

            ru_rel = (row.get("screenshot_ru_relative") or "").strip()
            en_rel = (row.get("screenshot_en_relative") or "").strip()
            ru_path = ROOT / ru_rel if ru_rel and "N/A" not in ru_rel.upper() else None
            en_path = ROOT / en_rel if en_rel and "N/A" not in en_rel.upper() else None

            ru_ok, ru_st, ru_bytes = _png_info(ru_path) if ru_path else (True, "n/a", 0)
            en_ok, en_st, en_bytes = _png_info(en_path) if en_path else (True, "n/a", 0)

            debug_ok = sid in debug_ids or route in ("N/A", "")

            result.rows.append(
                {
                    "screen_id": sid,
                    "screen_no": row.get("screen_no", ""),
                    "kotlin": "✅" if kotlin_ok else "❌",
                    "svg": "✅" if svg_ok else "❌",
                    "png_ru": ru_st,
                    "png_en": en_st,
                    "debug": "✅" if debug_ok else "⚠️",
                },
            )

            if not kotlin_ok:
                result.issues.append(Issue("P0", sid, f"Нет файла {kotlin_file}"))
            if not svg_ok:
                result.issues.append(Issue("P0", sid, f"Нет SVG {svg}"))
            if ru_path and not ru_ok:
                sev = "P0" if ru_st == "missing" else "P1"
                result.issues.append(Issue(sev, sid, f"PNG RU: {ru_st} ({ru_bytes} B)"))
            if en_path and not en_ok:
                sev = "P0" if en_st == "missing" else "P1"
                result.issues.append(Issue(sev, sid, f"PNG EN: {en_st} ({en_bytes} B)"))
            if not debug_ok and route != "N/A":
                result.issues.append(
                    Issue("P2", sid, "Нет DEBUG_UI_PREVIEW в DebugUiPreview.kt"),
                )

    # Dead code checks
    generic = ROOT / "app/src/main/java/dev/lovetest/app/ui/features/TestResultScreens.kt"
    if generic.is_file() and "GenericPercentResultScreen" in generic.read_text(encoding="utf-8"):
        nav = (ROOT / "app/src/main/java/dev/lovetest/app/navigation/LoveTestNavHost.kt").read_text(
            encoding="utf-8",
        )
        if "GenericPercentResultScreen" not in nav:
            result.issues.append(
                Issue("P2", "—", "GenericPercentResultScreen не используется в NavHost"),
            )

    for en_svg in (
        "screen52_love_test_protocol_input_en_m3.svg",
        "screen53_love_test_protocol_calculating_en_m3.svg",
        "screen54_love_test_protocol_result_en_m3.svg",
        "screen55_love_test_protocol_result_low_en_m3.svg",
        "screen56_love_test_onboarding_protocol_en_m3.svg",
    ):
        if not (DESIGN_DIR / en_svg).is_file():
            result.issues.append(Issue("P2", "—", f"Нет EN SVG {en_svg}"))

    return result


def render_markdown(result: AuditResult) -> str:
    from datetime import date

    p0 = [i for i in result.issues if i.severity == "P0"]
    p1 = [i for i in result.issues if i.severity == "P1"]
    p2 = [i for i in result.issues if i.severity == "P2"]

    lines = [
        "# Audit report — Love Tester (F6)",
        "",
        f"Дата: {date.today().isoformat()}",
        "",
        "## Сводка",
        "",
        f"| Уровень | Количество |",
        f"|---------|------------|",
        f"| P0 | {len(p0)} |",
        f"| P1 | {len(p1)} |",
        f"| P2 | {len(p2)} |",
        "",
        "## Матрица экранов",
        "",
        "| № | screen_id | Kotlin | SVG | PNG RU | PNG EN | Debug |",
        "|---|-----------|--------|-----|--------|--------|-------|",
    ]
    for r in result.rows:
        lines.append(
            f"| {r['screen_no']} | `{r['screen_id']}` | {r['kotlin']} | {r['svg']} | "
            f"{r['png_ru']} | {r['png_en']} | {r['debug']} |",
        )

    for level, items in ("P0", p0), ("P1", p1), ("P2", p2):
        lines.extend(["", f"## {level}", ""])
        if not items:
            lines.append("_Нет._")
            continue
        for i in items:
            sid = f"`{i.screen_id}`" if i.screen_id != "—" else "—"
            lines.append(f"- {sid}: {i.message}")

    lines.extend(
        [
            "",
            "## Рекомендуемые действия",
            "",
            "1. `./gradlew captureScreenshotCatalogRu` и `En` на эмуляторе 1080×1920",
            "2. `./gradlew verifyLoveTestBeforeStore`",
            "3. Задать `lovetest.privacy.policy.url` в `gradle.properties` (`./scripts/init_store_config.sh`)",
            "4. `./gradlew releaseGateLoveTest` → `bundleRelease`",
            "5. Internal track → Closed testing → Production",
            "",
        ],
    )
    return "\n".join(lines)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write", metavar="PATH", help="Записать отчёт в markdown")
    args = parser.parse_args()

    result = run_audit()
    md = render_markdown(result)

    if args.write:
        out = ROOT / args.write
        out.parent.mkdir(parents=True, exist_ok=True)
        out.write_text(md, encoding="utf-8")
        print(f"audit_screens_matrix: wrote {out}")

    p0 = sum(1 for i in result.issues if i.severity == "P0")
    print(f"audit_screens_matrix: {len(result.rows)} screens, P0={p0}, "
          f"P1={sum(1 for i in result.issues if i.severity == 'P1')}, "
          f"P2={sum(1 for i in result.issues if i.severity == 'P2')}")
    if not args.write:
        print(md)
    return 1 if p0 else 0


if __name__ == "__main__":
    sys.exit(main())
