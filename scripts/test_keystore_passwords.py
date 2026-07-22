#!/usr/bin/env python3
"""Probe keystore passwords without printing secrets. Exit 0 if a combo works for PrivateKeyEntry."""
from __future__ import annotations

import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
props: dict[str, str] = {}
for line in (ROOT / "keystore.properties").read_text(encoding="utf-8").splitlines():
    line = line.strip()
    if not line or line.startswith("#") or "=" not in line:
        continue
    k, v = line.split("=", 1)
    props[k.strip()] = v.strip()

ks = ROOT / props["storeFile"]
alias = props["keyAlias"]
creds = {}
for line in (ROOT / "build/keystore/CREDENTIALS.local.txt").read_text(encoding="utf-8").splitlines():
    line = line.strip()
    if "=" in line and not line.startswith("#"):
        k, v = line.split("=", 1)
        creds[k.strip()] = v.strip()

combos = [
    ("props", props["storePassword"], props["keyPassword"]),
    ("store=store,key=store", creds["storePassword"], creds["storePassword"]),
    ("creds", creds["storePassword"], creds["keyPassword"]),
    ("store=creds,key=android", creds["storePassword"], "android"),
    ("android", "android", "android"),
]

for name, store_pass, key_pass in combos:
    r = subprocess.run(
        [
            "keytool",
            "-importkeystore",
            "-noprompt",
            "-srckeystore",
            str(ks),
            "-destkeystore",
            "/dev/null",
            "-srcstoretype",
            "JKS",
            "-deststoretype",
            "PKCS12",
            "-srcstorepass",
            store_pass,
            "-deststorepass",
            "x",
            "-srcalias",
            alias,
            "-destalias",
            alias,
            "-srckeypass",
            key_pass,
        ],
        capture_output=True,
        text=True,
    )
    ok = r.returncode == 0
    print(f"{name}: {'OK' if ok else 'FAIL'}")
    if not ok and r.stderr:
        err = r.stderr.strip().splitlines()[-1][:120]
        print(f"  {err}")
