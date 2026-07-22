package dev.lovetest.core.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Love Tester v2 — light editorial romance.
 * См. docs/design/DESIGN_SYSTEM.md и docs/design/v2/.
 */
val LovePrimary = Color(0xFF9F2A4A)
val LoveOnPrimary = Color(0xFFFFFFFF)
val LovePrimaryContainer = Color(0xFFF3D9E0)
val LoveOnPrimaryContainer = Color(0xFF5C1228)
val LoveSecondary = Color(0xFFC45A72)
val LoveSurface = Color(0xFFFFF8F6)
val LoveOnSurface = Color(0xFF1A1218)
val LoveOnSurfaceVariant = Color(0xFF6B5E66)
val LoveOutline = Color(0xFFD4C4C9)
val LoveOutlineVariant = Color(0xFFEDE0E4)
val LoveErrorContainer = Color(0xFFF9DEDC)
val LoveOnErrorContainer = Color(0xFF410E0B)
val LoveBgGlowTop = Color(0xFFFFF8F6)
val LoveBgGlowBottom = Color(0xFFF3D9E0)
val LoveHeroEnd = Color(0xFFE8A0B0)

/** Love protocol test (#8) — deep teal editorial lane */
val LoveProtocolPrimary = Color(0xFF0F6B63)
val LoveProtocolPrimaryDark = Color(0xFF0A4A45)
val LoveProtocolSecondary = Color(0xFF2A9B90)
val LoveProtocolLight = Color(0xFFB8E0DB)
val LoveProtocolContainer = Color(0xFFE6F4F2)

val LoveProtocolHeroGradientColors = listOf(
    LoveProtocolPrimaryDark,
    LoveProtocolPrimary,
    LoveProtocolSecondary,
    LoveProtocolLight,
)

/** Muted protocol hero for low scores. */
val LoveProtocolMutedHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF6B7A7A),
        Color(0xFF9AA8A8),
        Color(0xFFD0D8D8),
        LoveProtocolContainer,
    ),
)

/** Desaturated hero for low compatibility scores. */
val LoveResultMutedHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF6B5E66),
        Color(0xFF9A8A90),
        Color(0xFFEDE0E4),
    ),
)

/**
 * Zodiac test (#6) — cosmic ink→violet→rose editorial night lane.
 */
val LoveZodiacIndigo = Color(0xFF1A1630)
val LoveZodiacViolet = Color(0xFF4A2C6A)
val LoveZodiacAccentPink = Color(0xFF9F2A4A)
val LoveZodiacSlotUnselected = Color(0xFFF5EEF1)
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
