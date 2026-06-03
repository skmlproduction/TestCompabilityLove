#!/usr/bin/env bash
# Локальная проверка перед PR (как CI).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${ROOT}"

for script in \
  scripts/count_tests.sh \
  scripts/verify_test_inventory.sh \
  scripts/post_push.sh \
  scripts/first_push.sh \
  scripts/list_git_staging.sh \
  scripts/validate_git_staging.sh \
  scripts/validate_git_staging_cached.sh \
  scripts/suggest_first_commit.sh \
  scripts/project_health.sh; do
  bash -n "${script}"
done

python3 -m py_compile scripts/audit_screens_matrix.py scripts/verify_ui_inventory.py scripts/apply_emulator_screenshot_artifact.py
python3 scripts/audit_screens_matrix.py --write docs/product/AUDIT_REPORT.md
bash scripts/validate_git_staging_cached.sh
echo "Tests: $(bash scripts/count_tests.sh | grep '^summary=' | cut -d= -f2-)"
./gradlew verifyLoveTest --no-daemon
