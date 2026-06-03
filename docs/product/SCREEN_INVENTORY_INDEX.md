# Индекс: экраны, состояния, навигация (Love Tester)

| Артефакт | Назначение |
|----------|------------|
| [SCREEN_INVENTORY_AND_NAVIGATION.md](./SCREEN_INVENTORY_AND_NAVIGATION.md) | Манифест (план), Routes, состояния, Intent. |
| [screens_catalog.csv](./screens_catalog.csv) | **29** screen_id → скриншоты, SVG, Kotlin-файлы (план). |
| [nav_matrix.csv](./nav_matrix.csv) | Переходы hub → тесты → результат. |
| [screens_catalog_DRAFT.md](./screens_catalog_DRAFT.md) | Исторический черновик (26 id). |
| `scripts/verify_ui_inventory.py` | CSV + (после F3) Routes ↔ NavHost, strings, PNG. |

## Референс (не копировать ассеты)

| Тип | Путь |
|-----|------|
| Store-тексты | `info/description.rtf` |
| Декомпил манифест/SDK | `Love Test - Compatibility Test_3.0.5_..._Decompiler.com/` |
| Скриншоты UX | `reference/screenshots/{play,device}/` + [INDEX.md](../../reference/screenshots/INDEX.md) |

## Команды (после F3)

```bash
python3 scripts/verify_ui_inventory.py --inventory-only
./gradlew verifyUiInventory
./gradlew verifyLoveTest
```

При новом маршруте: обновить этот инвентарь → CSV → `nav_matrix.csv` → SVG в `docs/design/`.
