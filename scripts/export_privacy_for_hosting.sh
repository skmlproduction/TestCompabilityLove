#!/usr/bin/env bash
# Экспорт legal HTML для static hosting (GitHub Pages, Netlify и т.д.).
# Usage: ./scripts/export_privacy_for_hosting.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="${ROOT}/build/legal-host"
SRC="${ROOT}/app/src/main/assets/legal"

mkdir -p "${OUT}"
python3 - <<'PY'
import re
from pathlib import Path

root = Path(".")
src = root / "app/src/main/assets/legal/privacy_policy.html"
out = root / "build/legal-host/index.html"
lines = src.read_text(encoding="utf-8").splitlines()
filtered = [
    line
    for line in lines
    if not re.search(r"gradle\.properties|lovetest\.privacy\.policy\.url", line, re.I)
]
out.parent.mkdir(parents=True, exist_ok=True)
out.write_text("\n".join(filtered) + "\n", encoding="utf-8")
print(f"  cleaned {out.relative_to(root)}")
PY
cp "${SRC}/data_collection.html" "${OUT}/data-collection.html"
touch "${OUT}/.nojekyll"

# Canonical path /privacy → index.html (GitHub Pages)
cat > "${OUT}/privacy.html" <<'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <meta http-equiv="refresh" content="0; url=./"/>
  <link rel="canonical" href="./"/>
  <title>Love Tester — Privacy Policy</title>
</head>
<body>
  <p><a href="./">Love Tester — Privacy Policy</a></p>
</body>
</html>
EOF

cat > "${OUT}/README.txt" <<'EOF'
Upload this folder to static hosting.

GitHub Pages (recommended):
  1. Settings → Pages → Source: GitHub Actions
  2. Run workflow «Privacy GitHub Pages» (or push to main)
  3. URL: https://YOUR_USER.github.io/YOUR_REPO/
  4. ./scripts/set_privacy_url.sh https://YOUR_USER.github.io/YOUR_REPO/

See docs/store/PRIVACY_HOSTING.md
EOF

echo "export_privacy_for_hosting: wrote ${OUT}/"
echo "  index.html, privacy.html, .nojekyll"
echo "  data-collection.html → optional Data safety link"
echo "  suggest URL: ./scripts/suggest_privacy_url.sh USER REPO"
ls -la "${OUT}"
