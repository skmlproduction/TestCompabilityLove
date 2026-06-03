package dev.lovetest.app.navigation

/**
 * Маршруты из [docs/product/screens_catalog.csv] (`route_path`).
 */
object Routes {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Consent = "consent"
    const val Hub = "hub"
    const val LoveTestInput = "love_test/input"
    const val LoveTestCalculating = "love_test/calculating"
    const val LoveTestResult = "love_test/result"
    const val CalculatorInput = "calculator/input"
    const val CalculatorResult = "calculator/result"
    const val PairInput = "pair/input"
    const val PairResult = "pair/result"
    const val VictoryInput = "victory/input"
    const val VictoryResult = "victory/result"
    const val LettersInput = "letters/input"
    const val LettersResult = "letters/result"
    const val ZodiacPick = "zodiac/pick"
    const val ZodiacResult = "zodiac/result"
    const val WheelSpin = "wheel/spin"
    const val WheelResult = "wheel/result"
    const val ProtocolInput = "protocol/input"
    const val ProtocolCalculating = "protocol/calculating"
    const val ProtocolResult = "protocol/result"
    const val PremiumPaywall = "premium/paywall"
    const val PremiumThankYou = "premium/thank_you"
    const val Settings = "settings"

    fun allDestinations(): List<String> = listOf(
        Splash,
        Onboarding,
        Consent,
        Hub,
        LoveTestInput,
        LoveTestCalculating,
        LoveTestResult,
        CalculatorInput,
        CalculatorResult,
        PairInput,
        PairResult,
        VictoryInput,
        VictoryResult,
        LettersInput,
        LettersResult,
        ZodiacPick,
        ZodiacResult,
        WheelSpin,
        WheelResult,
        ProtocolInput,
        ProtocolCalculating,
        ProtocolResult,
        PremiumPaywall,
        PremiumThankYou,
        Settings,
    )
}
