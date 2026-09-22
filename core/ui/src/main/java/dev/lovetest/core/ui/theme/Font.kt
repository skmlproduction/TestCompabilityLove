package dev.lovetest.core.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.lovetest.core.ui.R

val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
)

/**
 * Velvet display face — Fredoka (rounded, playful premium).
 * Fredoka has no Cyrillic glyphs: Nunito ExtraBold/Bold chain catches fallback.
 */
val VelvetDisplayFamily = FontFamily(
    Font(R.font.fredoka_medium, FontWeight.Medium),
    Font(R.font.fredoka_semibold, FontWeight.SemiBold),
    Font(R.font.fredoka_bold, FontWeight.Bold),
    // Fallback chain for glyphs Fredoka misses (Cyrillic etc.)
    Font(R.font.nunito_bold, FontWeight.Medium),
    Font(R.font.nunito_extrabold, FontWeight.SemiBold),
    Font(R.font.nunito_extrabold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_extrabold, FontWeight.Black),
)

/** Velvet body face — Nunito (warm, readable). */
val VelvetBodyFamily = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
)
