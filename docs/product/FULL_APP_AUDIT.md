# Полный аудит Love Tester — дизайн, функции, экраны vs референсы

**Дата:** 2026-07-18 (full quality pass + stylish redesign v2)  
**Эталоны:** `docs/design/v2/screen*_v2.svg` (+ legacy `screen*_m3.svg`)  
**Каталог:** `docs/product/screens_catalog.csv` (34 screen_id)  
**Код:** 24 `*Screen.kt` + share/overlays  

**Честный итог (2026-07-20 Full App Recheck):**
- **34/34 экрана реализованы**; redesign v2 + quality pass (input validation, a11y touch targets, share PNG love+wheel, restore outcomes, interstitial states).
- **Визуально:** light editorial romance (ink/paper/rose/teal); близко к v2 SVG по структуре/токенам, не pixel-perfect 1:1.
- **Store PNG:** RU+EN **34/34 @1080×1920** (эмулятор LoveTester_Capture).
- **Xiaomi QA:** `docs/screenshots/qa/xiaomi/ru` **34/34** (без смены system settings; разрешение устройства может быть 1080×2400).
- **Recheck P1:** critical **4/4 ✅** + QA **12/12 FRESH** на API34 (2026-07-20).
- **Gates:** `verifyLoveTestBeforeStore` ✅ ранее; **legal URLs HTTP 200 ×3** (`skmlproduction.github.io/TestCompabilityLove`, 2026-07-22).
- **Release:** Internal **GO**, Production **NO-GO** до Closed IAP smoke.

---

| Символ | Значение |
|--------|----------|
| 🟢 | Близко к SVG: структура, цвета, ключевые размеры, flow |
| 🟡 | Экран есть, заметные расхождения layout / типографика / токены |
| 🔴 | Функционально есть, но доверие/UX/Store-риск или сильный визуальный разрыв |
| ⚪ | Debug-only / не production flow |

---

## A. Executive summary

| Область | Оценка | Комментарий |
|---------|--------|-------------|
| Функции / навигация | 🟢 95% | 34 screen_id; session guards; share bitmap + text |
| Design tokens | 🟢 85% | `LoveLayout`, `LoveTypographyTokens`, `LoveFeatureTopBar` |
| Типографика vs SVG | 🟡 75% | Токены применены; percent/hero ≈ SVG, не 1:1 px |
| Edge-to-edge | 🟢 80% | Splash, Onboarding, Consent, Premium TY, feature flows |
| Low-% UX | 🟢 80% | Love+protocol эталон; остальные muted + low cards |
| Дисклеймеры | 🟢 85% | Onboarding, results, settings, key inputs |
| Монетизация v1 | 🟢 85% | Ads off; paywall «support dev»; SKU tip jar |
| A11y | 🟡 75% | CriticalFlowsA11yComposeTest; TalkBack manual pending |
| Store assets | 🟢 90% | RU+EN 34/34 @1080×1920; Xiaomi QA 34/34 |
| Legal HTTPS | 🟢 100% live | `check_legal_urls.sh` → **200×3** (skmlproduction Pages) |
| **Store readiness** | **95/100** | Internal GO; Production NO-GO (Closed IAP) |

**Сводка по 34 экранам (post-polish):** 🟢 18 · 🟡 13 · 🔴 0 · ⚪ 3

---

## B. Design system — что задумано vs что в коде

### Токены (`LoveLayout.kt`, `Color.kt`, `DESIGN_SYSTEM.md`)

| Token | SVG / док | Compose | Факт в UI |
|-------|-----------|---------|-----------|
| Horizontal padding | 72px ≈ 24dp | `ScreenHorizontalPadding` 24dp | ✅ широко через `loveScreenHorizontalPadding()` |
| Hero radius | rx 54 (input); rx 48 (result hero в screen10) | `HeroCornerRadius` 54dp; `HubHeroShape` 46dp | ⚠️ Result heroes: `48.dp` (совпадает с screen10); Hub 46dp ✅; input heroes через `LoveFeatureHero` — проверить radius |
| CTA height | 88px | `PrimaryCtaHeight` 56dp | ✅ `LovePrimaryButton` |
| Percent ring | 108px font, r=150 | `LoveTestResultRingSize` 200dp | 🟡 масштаб приблизительный |
| Share card | screen27 | `LoveShareCardHeight` 387dp | 🟡 radius 40dp не 54 |
| Protocol teal | `#00796B` | `LoveProtocolPrimary` | ✅ protocol lane |
| Muted low hero | screen11, screen34 | `LoveResultMutedHeroBrush`, `LoveProtocolMutedHeroBrush` | ✅ на % result; wheel N/A |

### Паттерны заголовков (3 несовместимых стиля)

1. **M3 TopAppBar** — love test, calculator, pair, letters, victory, protocol input (полупрозрачный `LoveSurface` 0.85)
2. **Custom top bar** — hub, onboarding, premium (close/skip)
3. **Edge-to-edge** — splash, consent, premium thank you (`loveEdgeToEdgeScreenPadding`)

**Рекомендация:** зафиксировать матрицу «какой паттерн на каком блоке» и привести onboarding + feature inputs к одному виду.

### Общие компоненты

| Компонент | Adoption | Gap |
|-----------|----------|-----|
| `LoveFeatureHero` | Input screens (love, protocol, pair, letters, victory, calculator, zodiac) | ❌ wheel, hub, results, premium |
| `LoveFeatureResultActions` | calculator, pair, victory, letters, zodiac, wheel | ❌ **LoveTestResultScreen**, **ProtocolResultScreen** |
| `LoveScreenBackButton` | inputs + settings + zodiac + wheel | ❌ results (by design — flow forward) |
| `loveInputFieldSemantics` | все text inputs | ✅ промпт 9 |

---

## C. Экран за экраном (34 screen_id)

### Блок 1 — Старт и legal

| # | screen_id | SVG | Kotlin | Match | Замечания / доработка |
|---|-----------|-----|--------|-------|------------------------|
| 1 | `splash_brand` | screen1, screen39 EN | SplashScreen | 🟡 | Edge-to-edge ✅; dual progress (linear+circular) лишний vs SVG; hero 240dp fixed; типографика headline меньше SVG `.headline` 58px |
| 2 | `onboarding_welcome` | screen2, screen40 EN | OnboardingScreen p.0 | 🟡 | Custom header без status strip SVG; mini-cards vs screen2 grid density; нет `loveEdgeToEdgeScreenPadding` |
| 3 | `onboarding_tests` | screen3, screen41 EN | OnboardingScreen p.1 | 🟡 | 2×3 grid 68dp ✅; page dots, section kicker, отступы vs screen3 |
| 4 | `onboarding_disclaimer` | screen4, screen36 | OnboardingScreen p.3 | 🟢 | Сильнейшая onboarding-страница; legal card + CTA; сверить screen36 variant |
| 30 | `onboarding_protocol` | screen35, screen56 EN | OnboardingScreen p.2 | 🟡 | Teal accent + disclaimer ✅; hero 160dp token; hub GO card (screen33) — отдельная сущность на hub |
| 5 | `consent_ads_gdpr` | screen5 | ConsentScreen | ⚪ | **Не в prod flow** (`ADS_ENABLED=false`); edge-to-edge ✅; ad-centric copy для v1.2 |

### Блок 2 — Hub

| # | screen_id | SVG | Kotlin | Match | Замечания |
|---|-----------|-----|--------|-------|-----------|
| 6 | `hub_main` | screen6, screen33, screen37 EN | HubScreen | 🟡 | Welcome hero 46dp = SVG ✅; featured love + 2×3 grid 68dp ✅; protocol strip (screen33) ✅; bottom nav only settings — SVG top settings icon отсутствует в app (решение OK); типографика welcome vs `.heroTitle` 40px |
| 7 | `hub_loading` | screen7 | HubScreen overlay | ⚪ | **Debug-only** — нет реальной async загрузки |
| 28 | `error_network` | screen28 | HubScreen overlay | ⚪ | **Debug-only** — нет network layer |

### Блок 3 — Love test (главный)

| # | screen_id | SVG | Kotlin | Match | Замечания |
|---|-----------|-----|--------|-------|-----------|
| 8 | `love_test_input` | screen8, screen43 EN | LoveTestInputScreen | 🟡 | `LoveFeatureHero` + fields 48dp ✅; inline tip disclaimer ✅; TopAppBar блокирует edge-to-edge |
| 9 | `love_test_calculating` | screen9, screen45 EN | LoveTestCalculatingScreen | 🟢 | Ring 134dp, step dots, blocked back ✅; progress card 48dp radius |
| 10 | `love_test_result` | screen10, screen38 EN | LoveTestResultScreen | 🟡 | Ring 200dp, disclaimer long, share sheet ✅; **custom CTAs** не `LoveFeatureResultActions`; share preview только high |
| 11 | `love_test_result_low` | screen11, screen48 EN | LoveTestResultScreen | 🟢 | **Эталон low%:** muted hero, warning/tip, broken-heart decor |
| 27 | `share_result_card` | screen27, screen47 EN | ShareResultPreview | 🟡 | Height token ✅; всегда pink gradient — нет muted для low; Telegram/WhatsApp = тот же system chooser |

### Блок 4 — Calculator & Pair

| # | screen_id | SVG | Kotlin | Match | Замечания |
|---|-----------|-----|--------|-------|-----------|
| 12 | `calculator_input` | screen12 | CalculatorInputScreen | 🟡 | LoveFeatureHero; local brush; нет disclaimer |
| 13 | `calculator_result` | screen13 | CalculatorResultScreen | 🟡 | Muted hero on low; **нет low-specific cards** как #11; `LoveFeatureResultActions` ✅ |
| 14 | `pair_input` | screen14, screen50 EN | PairInputScreen | 🟡 | `FeatureHeroTallMinHeight` 148dp для RU ✅; preview strip extra vs SVG |
| 15 | `pair_result` | screen15, screen51 EN | PairResultScreen | 🟡 | Metrics card одинаков для low/high; animated bars vs static SVG |

### Блок 5 — Victory & Letters

| # | screen_id | SVG | Kotlin | Match | Замечания |
|---|-----------|-----|--------|-------|-----------|
| 16 | `victory_input` | screen16 | VictoryInputScreen | 🟡 | Tall hero + trophy; нет disclaimer |
| 17 | `victory_result` | screen17 | VictoryResultScreen | 🟡 | YES/MAYBE paradigm; muted on «no»; нет warning band как love low |
| 18 | `letters_input` | screen18 | LettersInputScreen | 🟡 | Purple lane; stream preview; RU длинные строки — риск обрезки |
| 19 | `letters_result` | screen19 | LettersResultScreen | 🟡 | Muted low; tiles/stream layout density vs SVG |

### Блок 6 — Zodiac & Wheel

| # | screen_id | SVG | Kotlin | Match | Замечания |
|---|-----------|-----|--------|-------|-----------|
| 20 | `zodiac_pick` | screen20 | ZodiacPickScreen | 🟡 | Cosmic tokens ✅; **inline disclaimer** ✅; sign cells a11y ✅ |
| 21 | `zodiac_result` | screen21 | ZodiacResultScreen | 🟡 | Muted low; forecast card не меняется по score |
| 22 | `wheel_spin` | screen22 | WheelSpinScreen | 🟡 | Segment labels на диске ✅ (06-21); `WheelNotBetFooter` ✅; нет pink hero shell как SVG |
| 23 | `wheel_result` | screen23 | WheelResultScreen | 🟡 | Always pink hero; inline share card внизу vs overlay pattern |

### Блок 7 — Protocol

| # | screen_id | SVG | Kotlin | Match | Замечания |
|---|-----------|-----|--------|-------|-----------|
| 31 | `protocol_input` | screen30, screen52 EN | ProtocolInputScreen | 🟢 | Teal hero, steps, disclaimer, fields 48dp — лучший input после love test |
| 32 | `protocol_calculating` | screen32, screen53 EN | LoveTestCalculatingScreen | 🟢 | Reuse + protocol strings |
| 33 | `protocol_result` | screen31, screen54 EN | ProtocolResultScreen | 🟡 | Signals grid + verdict ✅; custom CTAs; share только high |
| 34 | `protocol_result_low` | screen34, screen55 EN | ProtocolResultScreen | 🟢 | Muted hero, warning/tip, empty state fallback ✅ |

### Блок 8 — Premium & Settings

| # | screen_id | SVG | Kotlin | Match | Замечания |
|---|-----------|-----|--------|-------|-----------|
| 24 | `premium_paywall` | screen24, screen44 EN | PremiumPaywallScreen | 🔴 | Copy ветвится по `ADS_ENABLED` ✅; но SKU `remove_ads` + benefit «Все тесты» при free content; hero vs screen24 gold trophy |
| 25 | `premium_thank_you` | screen25, screen49 EN | PremiumThankYouScreen | 🟡 | Edge-to-edge ✅; нет confetti/celebration из SVG |
| 26 | `settings_main` | screen26, screen46 EN | SettingsScreen | 🟢 | statusBarsPadding, disclaimer, groups, back a11y ✅; ads row hidden when off |

### Блок 9 — Ads placeholder

| # | screen_id | SVG | Kotlin | Match | Замечания |
|---|-----------|-----|--------|-------|-----------|
| 29 | `ad_interstitial_placeholder` | screen29 | AdInterstitialPlaceholder | ⚪ | Debug preview; v1 AdMob path when ads on |

---

## D. Функции vs ожидания «идеального» приложения

| Функция | Статус | Доработка |
|---------|--------|-----------|
| 8 тестов + protocol | ✅ | UI polish 1:1 |
| Love algorithm (local) | ✅ | Product decision |
| Share preview UI | ✅ | **Bitmap export** ❌ — только text `ACTION_SEND` |
| Share deep links (TG/WA) | 🟡 | Кнопки = system chooser |
| Onboarding 4 стр. + skip | ✅ | Insets + layout |
| Premium IAP | 🟡 | Closed testing; **переименовать SKU / value prop** |
| Restore purchases | ✅ | Device validate |
| Settings | ✅ | — |
| Ads + UMP | ⏸ | Code ready; flag off |
| Hub loading / network error | ❌ | Debug only |
| History / favorites | ❌ | V2 |
| Deep links | ❌ | V2 |
| Analytics / Crashlytics | ❌ | Post-launch |
| EN parity | 🟡 | 20 EN SVG; strings есть; visual не сверен |
| Store PNG актуальные | 🟡 | EN 34/34 @1080×1920; RU пересъёмка на эмуляторе |
| Legal Pages live | ✅ | `skmlproduction.github.io/TestCompabilityLove` |

---

## E. Store & release blockers (P0)

1. **Closed IAP smoke** — purchase + restore на license tester — `docs/store/CLOSED_IAP_SMOKE.md`
2. Закоммитить локальный P1 dirty tree (~240 файлов) и запушить
3. **Play Console manual** — app, Data safety, IARC, upload AAB (`build/store-upload/`)
4. ~~Legal URLs 404~~ — ✅ HTTP 200 ×3 на skmlproduction Pages (2026-07-22)
5. ~~IAP honesty~~ — ✅ paywall «support dev» при ADS off (2026-07-14)

---

## F. Cross-cutting backlog (приоритет)

### P0 — Store / trust
1. `git push` → Pages → `check_legal_urls.sh` 200
2. `recapture_store_screenshots.sh` на эмуляторе 1080×1920
3. IAP repositioning (SKU name / benefits / «support dev»)
4. Device smoke: все 8 тестов + settings privacy + premium continue free

### P1 — Design 1:1
5. Единая политика headers + edge-to-edge
6. `LoveFeatureResultActions` на love test + protocol results
7. Low-% parity: calculator/pair/letters/zodiac или явное «tier-1 only love+protocol»
8. Input disclaimers на всех test inputs
9. Typography scale tokens (percent, heroTitle, section kicker)
10. Share: export card as bitmap

### P2 — Quality
11. Hub loading/error — real или убрать из store claims
12. Side-by-side SVG overlay script
13. `connectedDebugAndroidTest` green on CI
14. TalkBack manual pass по `docs/a11y/WCAG_CRITICAL_FLOWS_CHECKLIST.md`

---

## G. Матрица инфраструктуры (F6)

`python3 scripts/audit_screens_matrix.py` — **34/34** Kotlin ✅, SVG ✅, PNG ok (размер/placeholder), Debug route ✅.  
**Важно:** «ok» PNG ≠ соответствие текущему UI (дата съёмки 2026-05-24).

---

## H. Честная оценка «идеальности»

| Критерий | Сейчас | Цель |
|----------|--------|------|
| Все экраны каталога | 34/34 implemented | 34/34 **pixel-close** |
| Design token discipline | Частичная | 100% через `LoveLayout` |
| EN store SVG set | 20 SVG + strings | Visual parity verified |
| Store listing honesty | Disclaimers in app | PNG = current UI |
| Premium honesty | SKU/value mismatch | Tip jar или real gate |
| Zero visual bugs | Много 🟡 | adb pass all flows |
| Production polish | Internal-ready | Closed-tested + fresh assets |

**Итог:** сильный **v1 scaffold + release pipeline**, но до «идеального» нужна **системная UI-полировка 1:1**, **честная монетизация**, **свежие скриншоты**, **live legal**.

---

## I. 10 промптов для следующей волны работ

Скопируйте по одному в чат агенту (порядок важен).

### Промпт 1/10 — Design foundation lock
«Design foundation pass: зафиксируй матрицу header patterns (TopAppBar / custom / edge-to-edge) в DESIGN_SYSTEM.md и примени на Splash, Onboarding×4, Consent. Замени hardcoded `48.dp` hero radius на `LoveLayout.HeroShape` где SVG rx=54; оставь 48 только где screen10/11. Добавь typography tokens для percent/heroTitle/section. `assembleDebug` + lint.»

### Промпт 2/10 — Hub + protocol strip
«Hub 1:1 vs screen6 + screen33: welcome hero типографика, featured love card, 2×3 grid spacing, protocol teal card (NOVOE, GO pill), bottom nav. Убери визуальные расхождения с SVG. `adb_screenshot_preview.sh hub_main ru` + сверка. assembleDebug.»

### Промпт 3/10 — Love test flow
«Love test input/calculating/result/share vs screen8–11, screen27: унифицируй CTAs через `LoveFeatureResultActions`; share preview на low если SVG требует; ring typography vs 108px; inline disclaimer на input. Compose tests love flow. assembleDebug.»

### Промпт 4/10 — Calculator, Pair, Letters, Victory
«4 feature tests input+result vs screen12–19: LoveFeatureHero везде; Pair metrics layout vs screen15; Letters stream vs screen19; Victory yes/maybe cards vs screen17. Low%: muted hero + опционально warning card как love test. Feature result actions. assembleDebug + unit tests.»

### Промпт 5/10 — Protocol flow
«Protocol input/result vs screen30–34, screen52–55 EN: hero min height vs SVG; signals grid icons; verdict bands; low warning+tip; ProtocolResult → LoveFeatureResultActions. ProtocolFlowComposeTest green. assembleDebug.»

### Промпт 6/10 — Zodiac + Wheel
«Zodiac pick/result vs screen20–21: cosmic lane tokens only; sign picker decor. Wheel spin vs screen22: pink hero shell если в SVG; segment labels; gold pointer; WheelNotBetFooter. Wheel result vs screen23. assembleDebug.»

### Промпт 7/10 — Premium + Settings + monetization honesty
«Premium paywall vs screen24: при ADS_ENABLED=false — benefits «Поддержать разработку», не «Все тесты»/«Без рекламы»; рассмотреть SKU rename в docs; dynamic price; thank you vs screen25. Settings vs screen26. Consent debug-only label. assembleDebug.»

### Промпт 8/10 — Share + viral
«Share: экспорт `LoveShareCard` в bitmap + `ACTION_SEND` image/*; TG/WA кнопки с target package если установлен; muted share card variant для low%. ShareResultPreviewComposeTest. assembleDebug.»

### Промпт 9/10 — Accessibility + inputs
«A11y: все input screens — semantics label+field; back buttons merge descendants; hub bottom nav; wheel CD. `connectedDebugAndroidTest` compose tests. WCAG checklist для onboarding, love result, settings.»

### Промпт 10/10 — Visual QA gate + Store assets
«На эмуляторе 1080×1920: `./scripts/capture_visual_qa_ru.sh` + full `./scripts/recapture_store_screenshots.sh` ru+en. Сверь 7 listing PNG с disclaimers. `./gradlew verifyLoveTestBeforeStore`. `./scripts/check_legal_urls.sh` после Pages. Итоговый отчёт: 34 экрана pass/fail vs SVG, готовность Store.»

---

*Автоматическая матрица PNG: `python3 scripts/audit_screens_matrix.py --write docs/product/AUDIT_REPORT.md`*
