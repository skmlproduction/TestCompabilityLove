# WCAG 2.2 — критичные flows (Love Tester)

Ручная проверка TalkBack + spot-check контраста. Автоматизация: `CriticalFlowsA11yComposeTest` (`connectedDebugAndroidTest`).

Обновлено: **2026-07-18** (CriticalFlowsA11y **9/9** на AVD; hub Koin = `navHostTestModule`; Pair/Wheel/missing-session E2E ✅).

## Onboarding (screen2–4, 36)

| Критерий | Проверка | Код | Compose test |
|----------|----------|-----|--------------|
| 1.3.1 Info & Relationships | Hero headings; page dots decorative | ✅ | `onboarding_showsWelcomeHeadlineForScreenReaders` |
| 2.4.4 Link Purpose | CTA «Далее» / «Начать» — visible text | ✅ | `OnboardingScreenComposeTest` |
| 2.5.8 Target Size | Primary CTA ≥48dp (`PrimaryCtaHeight` 56dp) | ✅ | — |
| 4.1.2 Name, Role, Value | Buttons announce label | ✅ | 🔲 TalkBack device |

## Love test input + result (screen8–11)

| Критерий | Проверка | Код | Compose test |
|----------|----------|-----|--------------|
| 1.3.1 | Fields: `loveInputFieldSemantics` + `loveInputLabelForAccessibility` | ✅ | `loveTestInput_backAndNameFields_haveAccessibilityNames` |
| 2.4.6 Headings | TopAppBar title + hero title | ✅ | `LoveTestInputScreenComposeTest` |
| 1.1.1 Non-text | Percent ring `love_test_percent_cd` | ✅ | `loveTestResult_percentRing_hasContentDescription` |
| 2.4.4 | Share / Retry / Home — text CTAs | ✅ | `LoveTestResultScreenComposeTest` |
| 4.1.2 | Back: `LoveScreenBackButton` mergeDescendants | ✅ | `loveTestInput_backAndNameFields_*` |

**Input screens с label+field semantics:** Love test, Calculator, Pair, Letters, Victory, Protocol. Zodiac — sign grid (`contentDescription` + `selected`).

## Settings (screen26)

| Критерий | Проверка | Код | Compose test |
|----------|----------|-----|--------------|
| 2.4.6 | Group labels PREMIUM / APP / LEGAL | ✅ | — |
| 2.5.8 | Rows ≥48dp touch height | ✅ | — |
| 4.1.2 | Back mergeDescendants; rows title+subtitle merged | ✅ | `settings_backAndPremiumRow_haveMergedSemantics` |
| 2.4.4 | Privacy / Data collection subtitles in row CD | ✅ | — |

## Hub bottom nav

| Критерий | Проверка | Код | Compose test |
|----------|----------|-----|--------------|
| 4.1.2 | Nav items mergeDescendants; selected «…, выбрано» | ✅ | `hubBottomNav_selectedTestsAnnounced` |
| 1.1.1 | Icons decorative; label in merged node | ✅ | `HubScreenComposeTest` |
| 4.1.2 | Premium strip / featured / grid: title+subtitle merged | ✅ | — |

## Share sheet (screen27)

| Критерий | Проверка | Код | Compose test |
|----------|----------|-----|--------------|
| 4.1.2 | TG/WA/Copy/More `Role.Button` + icon; Cancel ≥48dp | ✅ | share exporter instrumented |
| 1.1.1 | Love + Wheel PNG export via FileProvider | ✅ | `ShareCardImageExporterInstrumentedTest` |

## Wheel spin (screen22)

| Критерий | Проверка | Код | Compose test |
|----------|----------|-----|--------------|
| 1.1.1 | `wheel_segments_cd` on disc; `wheel_spinning_cd` while spinning | ✅ | `wheelSpin_discHasSegmentsContentDescription` |
| 4.1.2 | Back mergeDescendants | ✅ | — |
| 2.4.4 | Footer «не является ставкой» visible | ✅ | `WheelSpinScreenComposeTest` |

## Test run

```bash
./gradlew :app:assembleDebug
./gradlew :app:connectedDebugAndroidTest
# только a11y smoke:
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=dev.lovetest.app.a11y.CriticalFlowsA11yComposeTest
```

Compose tests = semantics smoke (contentDescription / merge). Полный TalkBack pass — эмулятор или устройство.
