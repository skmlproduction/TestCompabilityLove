package dev.lovetest.app.debug

import android.content.Intent
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.navigation.NavIntents
import dev.lovetest.app.navigation.Routes

object DebugUiPreview {
    @Volatile
    private var screenId: String? = null

    fun applyFromIntent(intent: Intent?) {
        if (!BuildConfig.DEBUG) return
        screenId = intent
            ?.getStringExtra(NavIntents.EXTRA_DEBUG_UI_PREVIEW)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    fun current(): String? = if (BuildConfig.DEBUG) screenId else null

    fun matches(id: String): Boolean = BuildConfig.DEBUG && screenId == id

    fun routeFor(screenId: String): String? = when (screenId) {
        "splash_brand" -> Routes.Splash
        "onboarding_welcome", "onboarding_tests", "onboarding_protocol", "onboarding_disclaimer" -> Routes.Onboarding
        "consent_ads_gdpr" -> Routes.Consent
        "hub_main" -> Routes.Hub
        "hub_loading", "error_network" -> Routes.Hub
        "love_test_input" -> Routes.LoveTestInput
        "love_test_calculating" -> Routes.LoveTestCalculating
        "love_test_result", "love_test_result_low" -> Routes.LoveTestResult
        "calculator_input" -> Routes.CalculatorInput
        "calculator_result", "calculator_result_low" -> Routes.CalculatorResult
        "pair_input" -> Routes.PairInput
        "pair_result", "pair_result_low" -> Routes.PairResult
        "victory_input" -> Routes.VictoryInput
        "victory_result", "victory_result_low" -> Routes.VictoryResult
        "letters_input" -> Routes.LettersInput
        "letters_result", "letters_result_low" -> Routes.LettersResult
        "zodiac_pick" -> Routes.ZodiacPick
        "zodiac_result" -> Routes.ZodiacResult
        "wheel_spin" -> Routes.WheelSpin
        "wheel_result" -> Routes.WheelResult
        "protocol_input" -> Routes.ProtocolInput
        "protocol_calculating" -> Routes.ProtocolCalculating
        "protocol_result", "protocol_result_low" -> Routes.ProtocolResult
        "premium_paywall" -> Routes.PremiumPaywall
        "premium_thank_you" -> Routes.PremiumThankYou
        "settings_main" -> Routes.Settings
        "share_result_card" -> Routes.LoveTestResult
        "ad_interstitial_placeholder" -> Routes.Hub
        else -> null
    }

    fun startRoute(): String? = screenId?.let { routeFor(it) }

    fun onboardingInitialPage(): Int = when (screenId) {
        "onboarding_tests" -> 1
        "onboarding_protocol" -> 2
        "onboarding_disclaimer" -> 3
        else -> 0
    }

    fun seedLoveResultForPreview() {
        if (!BuildConfig.DEBUG) return
        when (screenId) {
            "love_test_result_low" -> {
                dev.lovetest.app.session.LoveTestSession.storeLoveResult("Anna", "Max", 23)
            }
            "love_test_result", "share_result_card" -> {
                dev.lovetest.app.session.LoveTestSession.storeLoveResult("Anna", "Max", 87)
            }
            "pair_result" -> {
                val calc = dev.lovetest.core.domain.DefaultLoveScoreCalculator()
                dev.lovetest.app.session.LoveTestSession.storePairResult(
                    "Sophia",
                    "Dmitry",
                    74,
                    calc.pairMetrics("Sophia", "Dmitry"),
                )
            }
            "pair_result_low" -> {
                val calc = dev.lovetest.core.domain.DefaultLoveScoreCalculator()
                dev.lovetest.app.session.LoveTestSession.storePairResult(
                    "Анна",
                    "Макс",
                    34,
                    calc.pairMetrics("Анна", "Макс"),
                )
            }
            "calculator_result" -> {
                dev.lovetest.app.session.LoveTestSession.storeLoveResult("Мария", "Иван", 68)
            }
            "calculator_result_low" -> {
                dev.lovetest.app.session.LoveTestSession.storeLoveResult("Анна", "Макс", 38)
            }
            "victory_result" -> {
                dev.lovetest.app.session.LoveTestSession.storeLoveResult("Ольга", "Ольга", 72)
            }
            "victory_result_low" -> {
                dev.lovetest.app.session.LoveTestSession.storeLoveResult("Анна", "Макс", 41)
            }
            "letters_result" -> {
                dev.lovetest.app.session.LoveTestSession.storeLoveResult("ЛЮБОВЬ", "СЧАСТЬ", 65)
            }
            "letters_result_low" -> {
                dev.lovetest.app.session.LoveTestSession.storeLoveResult("АННА", "МАКС", 29)
            }
            "zodiac_result" -> {
                dev.lovetest.app.session.LoveTestSession.storeLoveResult("Лев", "Рыбы", 78)
            }
            "wheel_result" -> {
                dev.lovetest.app.session.LoveTestSession.storeWheelResult(2, "Свидание")
            }
            "protocol_result_low" -> {
                val calc = dev.lovetest.core.domain.DefaultLoveScoreCalculator()
                dev.lovetest.app.session.LoveTestSession.storeProtocolResult(
                    "Anna",
                    "Max",
                    28,
                    calc.protocolSignals("Anna", "Max"),
                )
            }
            "protocol_result" -> {
                val calc = dev.lovetest.core.domain.DefaultLoveScoreCalculator()
                dev.lovetest.app.session.LoveTestSession.storeProtocolResult(
                    "Sophia",
                    "Dmitry",
                    82,
                    calc.protocolSignals("Sophia", "Dmitry"),
                )
            }
        }
    }
}
