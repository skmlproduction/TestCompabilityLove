#!/usr/bin/env bash
# Печатает SHA-256 upload-сертификата из keystore.properties (пароли не выводятся).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROPS="${ROOT}/keystore.properties"

if [[ ! -f "${PROPS}" ]]; then
  echo "keystore.properties not found" >&2
  exit 1
fi

python3 - "${ROOT}" <<'PY'
import re
import subprocess
import sys
from pathlib import Path

root = Path(sys.argv[1])
props = {}
for line in (root / "keystore.properties").read_text(encoding="utf-8").splitlines():
    line = line.strip()
    if not line or line.startswith("#") or "=" not in line:
        continue
    k, v = line.split("=", 1)
    props[k.strip()] = v.strip()

ks = root / props["storeFile"]
alias = props["keyAlias"]
store_pass = props["storePassword"]
key_pass = props.get("keyPassword") or store_pass

out = subprocess.run(
    [
        "keytool",
        "-list",
        "-v",
        "-keystore",
        str(ks),
        "-alias",
        alias,
        "-storepass",
        store_pass,
        "-keypass",
        key_pass,
    ],
    capture_output=True,
    text=True,
    check=False,
)
if out.returncode != 0:
    print(out.stderr.strip() or "keytool failed", file=sys.stderr)
    sys.exit(out.returncode)

sha = None
for line in out.stdout.splitlines():
    if "SHA256:" in line:
        sha = line.split("SHA256:", 1)[1].strip()
        break
if not sha:
    print("SHA256 not found in keytool output", file=sys.stderr)
    sys.exit(1)
print(sha)
PY
