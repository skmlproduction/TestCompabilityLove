#!/usr/bin/env python3
"""Verify Love Tester release artifact before Play upload packaging."""
from __future__ import annotations

import argparse
import os
import re
import subprocess
import sys
import zipfile
from pathlib import Path


BAD_ENTRY_RE = re.compile(
    r"(mockk|junit|androidTest|testOnly|bytebuddy|objenesis)",
    re.IGNORECASE,
)


def find_zipalign(root: Path) -> Path | None:
    candidates: list[Path] = []
    for env_name in ("ANDROID_HOME", "ANDROID_SDK_ROOT"):
        value = os.environ.get(env_name)
        if value:
            candidates.extend(sorted((Path(value) / "build-tools").glob("*/zipalign")))
    local_props = root / "local.properties"
    if local_props.is_file():
        for line in local_props.read_text(encoding="utf-8", errors="ignore").splitlines():
            if line.startswith("sdk.dir="):
                sdk = Path(line.split("=", 1)[1].strip())
                candidates.extend(sorted((sdk / "build-tools").glob("*/zipalign")))
    default_sdk = Path.home() / "Library" / "Android" / "sdk"
    candidates.extend(sorted((default_sdk / "build-tools").glob("*/zipalign")))
    return candidates[-1] if candidates else None


def verify_zip(path: Path) -> tuple[list[str], list[str]]:
    with zipfile.ZipFile(path) as archive:
        names = archive.namelist()
    bad = [name for name in names if BAD_ENTRY_RE.search(name)]
    native_libs = [name for name in names if name.endswith(".so")]
    return bad, native_libs


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("artifact", help="AAB/APK path")
    parser.add_argument("--skip-zipalign", action="store_true")
    args = parser.parse_args()

    root = Path(__file__).resolve().parents[1]
    artifact = Path(args.artifact)
    if not artifact.is_absolute():
        artifact = (root / artifact).resolve()
    if not artifact.is_file():
        print(f"verify_release_artifact: FAIL missing artifact: {artifact}", file=sys.stderr)
        return 1
    if artifact.stat().st_size < 1_000_000:
        print(
            f"verify_release_artifact: FAIL artifact too small: {artifact.stat().st_size} B",
            file=sys.stderr,
        )
        return 1

    try:
        bad_entries, native_libs = verify_zip(artifact)
    except zipfile.BadZipFile as exc:
        print(f"verify_release_artifact: FAIL invalid zip: {exc}", file=sys.stderr)
        return 1

    if bad_entries:
        print("verify_release_artifact: FAIL test-only entries found:", file=sys.stderr)
        for entry in bad_entries[:50]:
            print(f"  {entry}", file=sys.stderr)
        if len(bad_entries) > 50:
            print(f"  ... {len(bad_entries) - 50} more", file=sys.stderr)
        return 1

    if not args.skip_zipalign:
        zipalign = find_zipalign(root)
        if zipalign is None:
            print("verify_release_artifact: WARN zipalign not found; zip-only checks passed")
        else:
            result = subprocess.run(
                [str(zipalign), "-c", "-P", "16", "4", str(artifact)],
                cwd=str(root),
                text=True,
                capture_output=True,
                check=False,
            )
            if result.returncode != 0:
                if result.stdout:
                    print(result.stdout, file=sys.stderr)
                if result.stderr:
                    print(result.stderr, file=sys.stderr)
                print("verify_release_artifact: FAIL zipalign check failed", file=sys.stderr)
                return result.returncode

    print(
        "verify_release_artifact: OK "
        f"{artifact.relative_to(root)} size={artifact.stat().st_size} "
        f"native_libs={len(native_libs)} bad_entries=0"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
