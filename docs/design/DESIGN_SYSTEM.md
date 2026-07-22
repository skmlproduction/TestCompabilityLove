# Design System — Love Tester v2 (light editorial romance)

Эталонные макеты v2: `docs/design/v2/screen*_v2.svg` · viewBox `0 0 1080 2400`.  
Legacy SVG: `docs/design/screen*_m3.svg` (архив до миграции).

## Направление

Премиальный светлый UI: paper + ink + rosewood accent. Один hero на экран, сильный brand signal, минимум визуального шума. **Не** purple-glow, **не** clay/неоморфизм, **не** dark-by-default.

## Цвета

| Token | Hex | Использование |
|-------|-----|----------------|
| `primary` / rose | `#9F2A4A` | CTA, акценты, progress |
| `primaryContainer` / blush | `#F3D9E0` | chips, soft fills |
| `onPrimary` | `#FFFFFF` | текст на CTA / hero |
| `onPrimaryContainer` | `#5C1228` | текст на blush |
| `secondary` | `#C45A72` | градиент hero |
| `surface` / paper | `#FFF8F6` | фон экрана |
| `onSurface` / ink | `#1A1218` | заголовки |
| `onSurfaceVariant` / muted | `#6B5E66` | body |
| `outline` | `#D4C4C9` | обводки |
| `outlineVariant` | `#EDE0E4` | треки |
| `protocolPrimary` | `#0F6B63` | протокол любви |
| `protocolContainer` | `#E6F4F2` | chips протокола |
| hero end | `#E8A0B0` | конец градиента |

### Градиенты

- `bgGlow` — paper → blush
- `heroGradient` — rose → secondary → hero end
- `protocolGradient` — teal dark → teal → light

## Типографика (Compose)

Масштаб: SVG px ÷ 2 ≈ sp. Шрифт: **Inter**.

| Token | Compose | Назначение |
|-------|---------|------------|
| `AppTitle` | 20sp ExtraBold | Hub / onboarding brand |
| `FeatureScreenTitle` | 18sp ExtraBold | center title feature flows |
| `HeroTitle` | 22sp Black | hero on cards |
| `HeroTitleOnGradient` | 28sp Black | splash / onboarding hero |
| `ScreenHeadline` | 28sp ExtraBold | headlines |
| `HeroBody` | 15sp / 22lh | подзаголовок hero |
| `PercentDisplay` | scale via `percentForRing` | кольцо результата |
| `FieldLabel` | 13sp SemiBold | labels полей |
| `SectionKicker` | 11sp + letterSpacing | small caps |

## Отступы и радиусы

| Token | Value |
|-------|--------|
| horizontal padding | 24dp |
| CTA height | 56dp |
| Hero radius (input/splash) | 54dp |
| Hub welcome hero | 46dp |
| Result / consent hero | 48dp |
| Standard card | 38dp |
| Button pill | 44dp |
| Text field | 28dp |

## Header patterns

| Pattern | Когда |
|---------|--------|
| Edge-to-edge | Splash, Onboarding×4, Consent, Premium thank-you |
| Custom top bar | Hub, Premium close |
| Feature top bar | Love / calculator / pair / … / protocol / settings |

## Compose files

- `core/ui/.../theme/Color.kt`
- `core/ui/.../theme/Type.kt`
- `core/ui/.../components/LoveLayout.kt`
- Референсы: `docs/design/v2/README.md`
