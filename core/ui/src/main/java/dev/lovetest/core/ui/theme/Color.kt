package dev.lovetest.core.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Love Tester — Velvet (dark romantic premium), default с 2026-09.
 * Легаси-имена сохранены и перепointed на Velvet-значения: экраны используют
 * старые токены, а рендерятся в тёмной теме. Светлая редакция удалена из prod.
 * Концепт: build/design-lt/concept.html
 */
val LovePrimary = Color(0xFFFF3D71)          // Velvet rose accent
val LoveOnPrimary = Color(0xFFFFFFFF)
val LovePrimaryContainer = Color(0x29FF3D71) // rose 16% glass chip
val LoveOnPrimaryContainer = Color(0xFFFFB4C8) // Velvet soft pink text
val LoveSecondary = Color(0xFFFF8A5C)        // Velvet coral
val LoveSurface = Color(0x0DFFFFFF)          // Velvet glass card (white 5%)
val LoveOnSurface = Color(0xFFFFF5F7)        // Velvet primary text
val LoveOnSurfaceVariant = Color(0xB8FFF5F7) // Velvet secondary text
val LoveOutline = Color(0x29FFFFFF)          // white 16%
val LoveOutlineVariant = Color(0x17FFFFFF)   // white 9%
val LoveErrorContainer = Color(0x33FF3D71)
val LoveOnErrorContainer = Color(0xFFFFF5F7)
val LoveBgGlowTop = Color(0xFF251021)
val LoveBgGlowBottom = Color(0xFF150810)
val LoveHeroEnd = Color(0xFFFF8A5C)

/** Love protocol test (#8) — deep teal editorial lane */
val LoveProtocolPrimary = Color(0xFF0F6B63)
val LoveProtocolPrimaryDark = Color(0xFF0A4A45)
val LoveProtocolSecondary = Color(0xFF2A9B90)
val LoveProtocolLight = Color(0xFFB8E0DB)
val LoveProtocolContainer = Color(0x1F2A9B90) // Velvet mint glass

val LoveProtocolHeroGradientColors = listOf(
    LoveProtocolPrimaryDark,
    LoveProtocolPrimary,
    LoveProtocolSecondary,
    LoveProtocolLight,
)

/** Muted protocol hero for low scores. */
val LoveProtocolMutedHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1E3330),
        Color(0xFF2E4A46),
        Color(0xFF3F635E),
        LoveProtocolContainer,
    ),
)

/** Desaturated hero for low compatibility scores. */
val LoveResultMutedHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF3A2A33),
        Color(0xFF5C4350),
        Color(0xFF7E5C6C),
    ),
)

/**
 * Zodiac test (#6) — cosmic ink→violet→rose editorial night lane.
 */
val LoveZodiacIndigo = Color(0xFF1A1630)
val LoveZodiacViolet = Color(0xFF4A2C6A)
val LoveZodiacAccentPink = Color(0xFF9F2A4A)
val LoveZodiacVioletSoft = Color(0xFFC9B4E4)   // readable label/back on dark
val LoveZodiacPinkSoft = Color(0xFFE8A4B8)     // readable accent label on dark
val LoveZodiacSlotUnselected = Color(0x14FFFFFF) // Velvet glass slot
val LoveZodiacShareBorder = Color(0xFFE0D4E8)

val LoveZodiacHeroGradientColors = listOf(
    LoveZodiacIndigo,
    LoveZodiacViolet,
    LoveZodiacAccentPink,
)

val LoveZodiacResultHeroGradientColors = listOf(
    LoveZodiacIndigo,
    LoveZodiacViolet,
    LoveZodiacAccentPink,
    LoveHeroEnd,
)

val LoveZodiacHeroBrush = Brush.linearGradient(colors = LoveZodiacHeroGradientColors)
val LoveZodiacResultHeroBrush = Brush.linearGradient(colors = LoveZodiacResultHeroGradientColors)

/** Wheel of fortune (#7) — rose hero. */
val LoveWheelHeroGradientColors = listOf(
    LovePrimary,
    LoveSecondary,
    LoveHeroEnd,
)

val LoveWheelHeroBrush = Brush.linearGradient(colors = LoveWheelHeroGradientColors)
val LoveWheelPointerGold = Color(0xFFE8C547)
val LoveWheelBadgeContainer = Color(0xFFF3D9E0)
val LoveWheelBadgeText = Color(0xFF5C1228)
val LoveWheelHintCardText = LoveWheelBadgeText

val LoveWheelSegmentColors = listOf(
    Color(0xFF9F2A4A),
    Color(0xFFC45A72),
    Color(0xFFE8A0B0),
    Color(0xFFF3D9E0),
    Color(0xFF9F2A4A),
    Color(0xFF7A1F38),
    Color(0xFFE8A0B0),
    Color(0xFFC45A72),
)

val LoveWheelSegmentTextColors = listOf(
    Color.White,
    Color.White,
    Color.White,
    LoveWheelBadgeText,
    LoveWheelBadgeText,
    Color.White,
    Color.White,
    Color.White,
)

// ─────────────────────────────────────────────────────────────
// Velvet — dark romantic premium (default theme since 2026-09)
// Концепт: build/design-lt/concept.html
// ─────────────────────────────────────────────────────────────
val VelvetBgTop = Color(0xFF251021)
val VelvetBgMid = Color(0xFF150810)
val VelvetBgBottom = Color(0xFF0E060B)

val VelvetAccentRose = Color(0xFFFF3D71)
val VelvetAccentCoral = Color(0xFFFF8A5C)
val VelvetPinkSoft = Color(0xFFFFB4C8)
val VelvetGold = Color(0xFFFFC94D)

val VelvetText = Color(0xFFFFF5F7)
val VelvetTextSecondary = Color(0xB8FFF5F7)   // 72%
val VelvetTextMuted = Color(0x7DFFF5F7)       // 49%
val VelvetTextFaint = Color(0x61FFF5F7)       // 38%

val VelvetCard = Color(0x0DFFFFFF)            // white 5%
val VelvetCardStrong = Color(0x14FFFFFF)      // white 8%
val VelvetCardBorder = Color(0x17FFFFFF)      // white 9%
val VelvetCardBorderStrong = Color(0x29FFFFFF) // white 16%

val VelvetAccentBrush = Brush.linearGradient(
    colors = listOf(VelvetAccentRose, VelvetAccentCoral),
)

val VelvetBgBrush = Brush.verticalGradient(
    colors = listOf(VelvetBgTop, VelvetBgMid, VelvetBgBottom),
)

val VelvetGoldBrush = Brush.linearGradient(
    colors = listOf(VelvetGold, VelvetAccentCoral),
)

/** Glow blobs for dark backgrounds (replace light LoveHubBackgroundBlobs palette). */
val VelvetGlowRose = Color(0x47FF3D71)   // 28%
val VelvetGlowCoral = Color(0x2EFF8A5C)  // 18%
