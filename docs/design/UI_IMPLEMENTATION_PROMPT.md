# UI implementation prompt (Love Tester)

Один `screen_id` за итерацию. Эталон: `docs/design/screen{N}_love_test_*_m3.svg`.

## Шаблон

```
screen_id: <из screens_catalog.csv>
Эталон SVG: docs/design/screen{N}_...
Задача: привести Composable к 1:1 с SVG (M3 light, primary #C2185B).
Только UI — не трогать billing/ads SDK.
После: ./gradlew :app:compileDebugKotlin && ./gradlew verifyLoveTest
```

## Debug-съёмка

```bash
adb shell am start -n dev.lovetest.app/.MainActivity \
  --es lovetest.intent.extra.DEBUG_UI_PREVIEW hub_main
```

См. `dev.lovetest.app.debug.DebugUiPreview`.
