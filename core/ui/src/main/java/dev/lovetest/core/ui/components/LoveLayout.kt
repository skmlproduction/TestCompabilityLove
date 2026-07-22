package dev.lovetest.core.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Design-system layout tokens — см. docs/design/DESIGN_SYSTEM.md */
object LoveLayout {
    /** Screen horizontal inset (SVG 72px ≈ 24dp). */
    val ScreenHorizontalPadding: Dp = 24.dp

    /** Hero / large card corner (SVG rx 54). */
    val HeroCornerRadius: Dp = 54.dp

    /** Primary pill CTA height (M3 comfortable touch; SVG 88px ≈ 56dp on phone). */
    val PrimaryCtaHeight: Dp = 56.dp

    /** Minimum hero block height on feature input screens (v2 editorial). */
    val FeatureHeroMinHeight: Dp = 132.dp

    /** Onboarding hero heights (SVG px ÷ 3). */
    val OnboardingHeroWelcomeHeight: Dp = 268.dp
    val OnboardingHeroTestsHeight: Dp = 174.dp
    /** Protocol onboarding hero — badge + title + 2-line body + page pill. */
    val OnboardingHeroProtocolHeight: Dp = 216.dp
    val OnboardingHeroDisclaimerHeight: Dp = 134.dp

    /** Onboarding tests grid row (screen3: 200px ÷ 3, horizontal icon+text). */
    val OnboardingTestsGridCellHeight: Dp = 72.dp

    /** Splash feature chips (screen1: 52px ÷ 3). */
    val SplashFeatureChipHeight: Dp = 52.dp

    /** Hub welcome hero (screen6: 280px ÷ 3). */
    val HubHeroHeight: Dp = 93.dp

    /** Hub hero chip pill (screen6: 48px ÷ 2). */
    val HubHeroChipHeight: Dp = 24.dp

    /** Hub grid + featured cards (screen6/33: 176–200px ÷ 3). */
    val HubGridRowSpacing: Dp = 8.dp
    val HubGridCellHeight: Dp = 59.dp
    val HubFeaturedCardHeight: Dp = 67.dp
    val HubProtocolFeaturedCardHeight: Dp = 80.dp
    /** Grid icon box — screen6: 56px ÷ 2. */
    val HubGridIconSize: Dp = 28.dp
    val HubGridIconCorner: Dp = 9.dp
    val HubGoPillHeight: Dp = 48.dp
    val HubGoPillMinWidth: Dp = 72.dp
    val HubGoPillCorner: Dp = 24.dp

    val HubFeaturedShape = RoundedCornerShape(38.dp)
    val HubGridShape = RoundedCornerShape(32.dp)

    /** Love test input field (screen8: 96px ÷ 3, min touch 48dp). */
    val LoveTestInputFieldHeight: Dp = 48.dp

    /** Preset name chips / ad close — WCAG minimum touch target. */
    val PresetChipMinHeight: Dp = 48.dp
    val MinTouchTarget: Dp = 48.dp

    /** Love test input hero — title + 2-line body must stay readable. */
    val LoveTestInputHeroMinHeight: Dp = 128.dp

    /** Onboarding welcome mini-cards (3-up) — room for title + 2-line caption. */
    val OnboardingMiniCardMinHeight: Dp = 152.dp

    /** Result hero percent ring (screen10/11: 300px ÷ 3). */
    val LoveTestResultRingSize: Dp = 200.dp

    /** Calculating progress ring (screen9: 400px ÷ 3). */
    val LoveTestCalculatingRingSize: Dp = 134.dp

    /** Share card bitmap preview (screen27: 1160px ÷ 3). */
    val LoveShareCardHeight: Dp = 387.dp

    /** Zodiac pick hero (screen20: 200px ÷ 3, rx 46). */
    val ZodiacPickHeroMinHeight: Dp = 67.dp

    /** Protocol input hero (screen30: 220px ÷ 3 ≈ 73dp; rx 46 → [HubHeroShape]). */
    val ProtocolInputHeroMinHeight: Dp = 73.dp

    /** Tall feature heroes for multi-line RU copy (screen14/16/18). */
    val FeatureHeroTallMinHeight: Dp = 148.dp

    val HeroShape = RoundedCornerShape(HeroCornerRadius)

    /** Hub welcome hero — screen6 SVG rx=46. */
    val HubHeroShape = RoundedCornerShape(46.dp)

    /** Result hero card — screen10/11 SVG rx=48 (not 54). */
    val ResultHeroCornerRadius: Dp = 48.dp
    val ResultHeroShape = RoundedCornerShape(ResultHeroCornerRadius)

    /** Consent illustration block — screen5 rx=48 (same as result hero). */
    val ConsentIllustrationShape = ResultHeroShape
}

/** Full-screen scroll content under transparent system bars. */
fun Modifier.loveEdgeToEdgeScreenPadding(
    includeNavigationBar: Boolean = true,
): Modifier {
    var m = statusBarsPadding()
    if (includeNavigationBar) {
        m = m.then(Modifier.navigationBarsPadding())
    }
    return m.then(Modifier.padding(horizontal = LoveLayout.ScreenHorizontalPadding))
}

fun Modifier.loveScreenHorizontalPadding(): Modifier =
    padding(horizontal = LoveLayout.ScreenHorizontalPadding)
