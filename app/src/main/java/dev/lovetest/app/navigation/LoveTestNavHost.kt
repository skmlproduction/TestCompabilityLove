package dev.lovetest.app.navigation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.R
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.app.session.LoveTestSession
import dev.lovetest.app.ui.consent.ConsentScreen
import dev.lovetest.app.ui.features.CalculatorInputScreen
import dev.lovetest.app.ui.features.CalculatorResultScreen
import dev.lovetest.app.ui.features.PairInputScreen
import dev.lovetest.app.ui.features.PairResultScreen
import dev.lovetest.app.ui.features.ProtocolInputScreen
import dev.lovetest.app.ui.features.ProtocolResultScreen
import dev.lovetest.app.ui.features.LettersInputScreen
import dev.lovetest.app.ui.features.LettersResultScreen
import dev.lovetest.app.ui.features.VictoryInputScreen
import dev.lovetest.app.ui.features.VictoryResultScreen
import dev.lovetest.app.ui.features.WheelResultScreen
import dev.lovetest.app.ui.features.WheelSpinScreen
import dev.lovetest.app.ui.features.ZodiacPickScreen
import dev.lovetest.app.ui.features.ZodiacResultScreen
import dev.lovetest.app.ui.hub.HubScreen
import dev.lovetest.app.ui.love.LoveTestCalculatingScreen
import dev.lovetest.app.ui.love.TestCalculatingFlavor
import dev.lovetest.app.ui.love.LoveTestFlowViewModel
import dev.lovetest.app.ui.love.LoveTestInputScreen
import dev.lovetest.app.ui.love.LoveTestResultScreen
import dev.lovetest.app.ui.onboarding.OnboardingScreen
import dev.lovetest.app.ui.premium.PremiumPaywallScreen
import dev.lovetest.app.ui.premium.PremiumThankYouScreen
import dev.lovetest.app.ui.settings.SettingsScreen
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.legal.LegalDocuments.openDataCollectionSummary
import dev.lovetest.app.legal.LegalDocuments.openPrivacyPolicy
import dev.lovetest.app.monetization.AdMobInterstitialManager
import dev.lovetest.app.monetization.AdsConsentManager
import dev.lovetest.app.monetization.AdsInterstitialController
import dev.lovetest.app.monetization.bootstrapAdsIfAllowed
import dev.lovetest.app.monetization.PremiumBillingManager
import dev.lovetest.app.monetization.PremiumPurchaseOutcome
import dev.lovetest.app.monetization.restorePremiumAccess
import dev.lovetest.app.util.findActivity
import dev.lovetest.app.ui.session.RequireLoveResult
import dev.lovetest.app.ui.session.RequirePairResult
import dev.lovetest.app.ui.session.RequireProtocolResult
import dev.lovetest.app.ui.session.RequireWheelResult
import dev.lovetest.app.ui.splash.SplashScreen
import androidx.compose.runtime.LaunchedEffect
import dev.lovetest.app.util.shareLoveResult
import dev.lovetest.app.util.shareProtocolResult
import dev.lovetest.app.util.shareWheelIdea
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LoveTestNavHost(
    navController: NavHostController,
    appScope: CoroutineScope,
    startDestination: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val preferences: AppPreferences = koinInject()
    val billingManager: PremiumBillingManager = koinInject()
    val adsConsentManager: AdsConsentManager = koinInject()
    val adMobManager: AdMobInterstitialManager = koinInject()
    val flowViewModel: LoveTestFlowViewModel = koinViewModel()

    fun navigateHomeAfterTest() {
        appScope.launch {
            if (AdsInterstitialController.shouldShow(preferences, adsConsentManager)) {
                AdsInterstitialController.requestShowOnHub()
            }
            if (!navController.popBackStack(Routes.Hub, false)) {
                navController.navigate(Routes.Hub) {
                    popUpTo(Routes.Hub) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    fun redirectWhenSessionLost() {
        Toast.makeText(
            context,
            context.getString(R.string.result_session_expired),
            Toast.LENGTH_SHORT,
        ).show()
        navController.navigate(Routes.Hub) {
            popUpTo(Routes.Hub) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { loveTestEnterTransition() },
        exitTransition = { loveTestExitTransition() },
        popEnterTransition = { loveTestPopEnterTransition() },
        popExitTransition = { loveTestPopExitTransition() },
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.Splash) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Routes.Onboarding) {
            OnboardingScreen(
                onComplete = {
                    appScope.launch {
                        preferences.markOnboardingCompleted()
                        navController.navigate(routeAfterOnboarding(preferences)) {
                            popUpTo(Routes.Onboarding) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onSkip = {
                    appScope.launch {
                        preferences.markOnboardingCompleted()
                        navController.navigate(routeAfterOnboarding(preferences)) {
                            popUpTo(Routes.Onboarding) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
            )
        }
        composable(Routes.Consent) {
            ConsentScreen(
                onOpenPrivacy = { context.openPrivacyPolicy() },
                onAccept = {
                    val activity = context.findActivity() ?: return@ConsentScreen
                    adsConsentManager.gatherConsent(activity) { canRequestAds ->
                        appScope.launch {
                            preferences.markConsentCompleted()
                            if (canRequestAds) {
                                bootstrapAdsIfAllowed(
                                    context,
                                    preferences,
                                    adsConsentManager,
                                    adMobManager,
                                )
                            }
                            navController.navigate(Routes.Hub) {
                                popUpTo(Routes.Consent) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                onManage = {
                    val activity = context.findActivity() ?: return@ConsentScreen
                    if (adsConsentManager.isPrivacyOptionsRequired()) {
                        adsConsentManager.showPrivacyOptions(activity) {}
                    } else {
                        adsConsentManager.gatherConsent(activity) {}
                    }
                },
            )
        }
        composable(Routes.Hub) {
            HubScreen(
                onOpenLoveTest = { navController.navigateSingleTop(Routes.LoveTestInput) },
                onOpenCalculator = { navController.navigateSingleTop(Routes.CalculatorInput) },
                onOpenPair = { navController.navigateSingleTop(Routes.PairInput) },
                onOpenVictory = { navController.navigateSingleTop(Routes.VictoryInput) },
                onOpenLetters = { navController.navigateSingleTop(Routes.LettersInput) },
                onOpenZodiac = { navController.navigateSingleTop(Routes.ZodiacPick) },
                onOpenWheel = { navController.navigateSingleTop(Routes.WheelSpin) },
                onOpenProtocol = { navController.navigateSingleTop(Routes.ProtocolInput) },
                onOpenPremium = { navController.navigateSingleTop(Routes.PremiumPaywall) },
                onOpenSettings = { navController.navigateSingleTop(Routes.Settings) },
            )
        }
        composable(Routes.LoveTestInput) {
            LoveTestInputScreen(
                onBack = { navController.navigateUp() },
                onCalculate = { n1, n2 ->
                    if (flowViewModel.runLoveTest(n1, n2) {
                            navController.navigate(Routes.LoveTestResult) {
                                popUpTo(Routes.LoveTestCalculating) { inclusive = true }
                                launchSingleTop = true
                            }
                    }) {
                        navController.navigateSingleTop(Routes.LoveTestCalculating)
                    }
                },
            )
        }
        composable(Routes.LoveTestCalculating) {
            if (DebugUiPreview.matches("love_test_calculating")) {
                LaunchedEffect(Unit) {
                    flowViewModel.runPreviewCalculating()
                }
            }
            LoveTestCalculatingScreen(viewModel = flowViewModel)
        }
        composable(Routes.LoveTestResult) {
            RequireLoveResult(onMissing = { redirectWhenSessionLost() }) {
                LoveTestResultScreen(
                    onShare = { context.shareLoveResult() },
                    onTryAgain = {
                        navController.navigate(Routes.LoveTestInput) {
                            popUpTo(Routes.Hub) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onHome = { navigateHomeAfterTest() },
                )
            }
        }
        composable(Routes.CalculatorInput) {
            CalculatorInputScreen(
                onBack = { navController.navigateUp() },
                onSubmit = { n1, n2 ->
                    if (flowViewModel.runLoveTest(n1, n2) {
                            navController.navigate(Routes.CalculatorResult) {
                                popUpTo(Routes.LoveTestCalculating) { inclusive = true }
                            }
                    }) {
                        navController.navigateSingleTop(Routes.LoveTestCalculating)
                    }
                },
            )
        }
        composable(Routes.CalculatorResult) {
            RequireLoveResult(onMissing = { redirectWhenSessionLost() }) {
                CalculatorResultScreen(
                    onShare = { context.shareLoveResult() },
                    onTryAnother = {
                        navController.navigate(Routes.CalculatorInput) {
                            popUpTo(Routes.Hub) { inclusive = false }
                        }
                    },
                    onHome = { navigateHomeAfterTest() },
                )
            }
        }
        composable(Routes.PairInput) {
            PairInputScreen(
                onBack = { navController.navigateUp() },
                onSubmit = { n1, n2 ->
                    if (flowViewModel.runPairTest(n1, n2) {
                            navController.navigate(Routes.PairResult) {
                                popUpTo(Routes.LoveTestCalculating) { inclusive = true }
                            }
                    }) {
                        navController.navigateSingleTop(Routes.LoveTestCalculating)
                    }
                },
            )
        }
        composable(Routes.PairResult) {
            RequirePairResult(onMissing = { redirectWhenSessionLost() }) {
                PairResultScreen(
                    onShare = { context.shareLoveResult() },
                    onTryAnother = {
                        navController.navigate(Routes.PairInput) {
                            popUpTo(Routes.Hub) { inclusive = false }
                        }
                    },
                    onHome = { navigateHomeAfterTest() },
                )
            }
        }
        composable(Routes.VictoryInput) {
            VictoryInputScreen(
                onBack = { navController.navigateUp() },
                onSubmit = { name ->
                    if (flowViewModel.runSingleNameTest(name) {
                            navController.navigate(Routes.VictoryResult) {
                                popUpTo(Routes.LoveTestCalculating) { inclusive = true }
                            }
                    }) {
                        navController.navigateSingleTop(Routes.LoveTestCalculating)
                    }
                },
            )
        }
        composable(Routes.VictoryResult) {
            RequireLoveResult(onMissing = { redirectWhenSessionLost() }) {
                VictoryResultScreen(
                    onShare = { context.shareLoveResult() },
                    onTryAnother = {
                        navController.navigate(Routes.VictoryInput) {
                            popUpTo(Routes.Hub) { inclusive = false }
                        }
                    },
                    onHome = { navigateHomeAfterTest() },
                )
            }
        }
        composable(Routes.LettersInput) {
            LettersInputScreen(
                onBack = { navController.navigateUp() },
                onSubmit = { w1, w2 ->
                    if (flowViewModel.runLoveTest(w1, w2) {
                            navController.navigate(Routes.LettersResult) {
                                popUpTo(Routes.LoveTestCalculating) { inclusive = true }
                            }
                    }) {
                        navController.navigateSingleTop(Routes.LoveTestCalculating)
                    }
                },
            )
        }
        composable(Routes.LettersResult) {
            RequireLoveResult(onMissing = { redirectWhenSessionLost() }) {
                LettersResultScreen(
                    onShare = { context.shareLoveResult() },
                    onTryAnother = {
                        navController.navigate(Routes.LettersInput) {
                            popUpTo(Routes.Hub) { inclusive = false }
                        }
                    },
                    onHome = { navigateHomeAfterTest() },
                )
            }
        }
        composable(Routes.ZodiacPick) {
            ZodiacPickScreen(
                onBack = { navController.navigateUp() },
                onSubmit = { s1, s2 ->
                    if (flowViewModel.runLoveTest(s1, s2) {
                            navController.navigate(Routes.ZodiacResult) {
                                popUpTo(Routes.LoveTestCalculating) { inclusive = true }
                            }
                    }) {
                        navController.navigateSingleTop(Routes.LoveTestCalculating)
                    }
                },
            )
        }
        composable(Routes.ZodiacResult) {
            RequireLoveResult(onMissing = { redirectWhenSessionLost() }) {
                ZodiacResultScreen(
                    onShare = { context.shareLoveResult() },
                    onTryAnother = {
                        navController.navigate(Routes.ZodiacPick) {
                            popUpTo(Routes.Hub) { inclusive = false }
                        }
                    },
                    onHome = { navigateHomeAfterTest() },
                )
            }
        }
        composable(Routes.WheelSpin) {
            WheelSpinScreen(
                onBack = { navController.navigateUp() },
                onSpinComplete = { index, prize ->
                    if (flowViewModel.runWheelSpin(prize, index) {
                            navController.navigate(Routes.WheelResult) {
                                popUpTo(Routes.LoveTestCalculating) { inclusive = true }
                            }
                    }) {
                        navController.navigateSingleTop(Routes.LoveTestCalculating)
                    }
                },
            )
        }
        composable(Routes.WheelResult) {
            RequireWheelResult(onMissing = { redirectWhenSessionLost() }) {
                WheelResultScreen(
                    onShare = { context.shareWheelIdea() },
                    onSpinAgain = {
                        navController.navigate(Routes.WheelSpin) {
                            popUpTo(Routes.Hub) { inclusive = false }
                        }
                    },
                    onHome = { navigateHomeAfterTest() },
                )
            }
        }
        composable(Routes.ProtocolInput) {
            ProtocolInputScreen(
                onBack = { navController.navigateUp() },
                onSubmit = { n1, n2 ->
                    if (flowViewModel.runProtocolTest(n1, n2) {
                            navController.navigate(Routes.ProtocolResult) {
                                popUpTo(Routes.ProtocolCalculating) { inclusive = true }
                            }
                    }) {
                        navController.navigateSingleTop(Routes.ProtocolCalculating)
                    }
                },
            )
        }
        composable(Routes.ProtocolCalculating) {
            if (DebugUiPreview.matches("protocol_calculating")) {
                LaunchedEffect(Unit) {
                    flowViewModel.runPreviewCalculating()
                }
            }
            LoveTestCalculatingScreen(
                flavor = TestCalculatingFlavor.Protocol,
                viewModel = flowViewModel,
            )
        }
        composable(Routes.ProtocolResult) {
            RequireProtocolResult(onMissing = { redirectWhenSessionLost() }) {
                ProtocolResultScreen(
                    onShare = { context.shareProtocolResult() },
                    onTryAnother = {
                        navController.navigate(Routes.ProtocolInput) {
                            popUpTo(Routes.Hub) { inclusive = false }
                        }
                    },
                    onHome = { navigateHomeAfterTest() },
                )
            }
        }
        composable(Routes.PremiumPaywall) {
            PremiumPaywallScreen(
                onClose = { navController.navigateUp() },
                onPurchase = {
                    appScope.launch {
                        val outcome = if (billingManager.isConfigured()) {
                            val activity = context.findActivity()
                            if (activity != null) {
                                billingManager.purchase(activity)
                            } else {
                                PremiumPurchaseOutcome.Error("no_activity")
                            }
                        } else {
                            PremiumPurchaseOutcome.NotConfigured
                        }
                        when (outcome) {
                            PremiumPurchaseOutcome.Success -> {
                                preferences.setPremium(true)
                                AdsInterstitialController.consume()
                                navController.navigateSingleTop(Routes.PremiumThankYou)
                            }
                            PremiumPurchaseOutcome.NotConfigured -> {
                                if (BuildConfig.DEBUG) {
                                    preferences.setPremium(true)
                                    AdsInterstitialController.consume()
                                    navController.navigateSingleTop(Routes.PremiumThankYou)
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.premium_billing_not_configured),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                            PremiumPurchaseOutcome.Cancelled -> Unit
                            is PremiumPurchaseOutcome.Error -> {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.premium_billing_error),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    }
                },
                onRestore = {
                    appScope.launch {
                        val restored = restorePremiumAccess(preferences, billingManager)
                        if (restored) {
                            preferences.setPremium(true)
                            AdsInterstitialController.consume()
                            Toast.makeText(
                                context,
                                context.getString(R.string.premium_restore_success),
                                Toast.LENGTH_SHORT,
                            ).show()
                            navController.navigateUp()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.premium_restore_not_found),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                },
                onContinueFree = { navController.navigateUp() },
            )
        }
        composable(Routes.PremiumThankYou) {
            PremiumThankYouScreen(
                onHome = {
                    navController.navigate(Routes.Hub) {
                        popUpTo(Routes.Hub) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLoveTest = { navController.navigateSingleTop(Routes.LoveTestInput) },
            )
        }
        composable(Routes.Settings) {
            val prefs: AppPreferences = koinInject()
            SettingsScreen(
                onBack = { navController.navigateUp() },
                onPremium = { navController.navigateSingleTop(Routes.PremiumPaywall) },
                onRestorePurchases = {
                    appScope.launch {
                        val restored = restorePremiumAccess(preferences, billingManager)
                        if (restored) {
                            preferences.setPremium(true)
                            AdsInterstitialController.consume()
                            Toast.makeText(
                                context,
                                context.getString(R.string.premium_restore_success),
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.premium_restore_not_found),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                },
                onReplayOnboarding = {
                    appScope.launch {
                        prefs.resetOnboarding()
                        if (BuildConfig.ADS_ENABLED) {
                            prefs.resetConsent()
                        }
                        navController.navigate(Routes.Onboarding) {
                            popUpTo(Routes.Hub) { inclusive = true }
                        }
                    }
                },
                onLanguage = {
                    try {
                        context.startActivity(
                            Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                                data = Uri.parse("package:${context.packageName}")
                            },
                        )
                    } catch (_: Exception) {
                        context.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    }
                },
                onManageAdsConsent = {
                    val activity = context.findActivity()
                    if (activity != null) {
                        if (adsConsentManager.isPrivacyOptionsRequired()) {
                            adsConsentManager.showPrivacyOptions(activity) {
                                appScope.launch {
                                    bootstrapAdsIfAllowed(
                                        context,
                                        prefs,
                                        adsConsentManager,
                                        adMobManager,
                                    )
                                }
                            }
                        } else {
                            adsConsentManager.gatherConsent(activity) { _ ->
                                appScope.launch {
                                    bootstrapAdsIfAllowed(
                                        context,
                                        prefs,
                                        adsConsentManager,
                                        adMobManager,
                                    )
                                }
                            }
                        }
                    } else {
                        context.openPrivacyPolicy()
                    }
                },
                onPrivacy = { context.openPrivacyPolicy() },
                onDataCollection = { context.openDataCollectionSummary() },
                onClearSavedNames = {
                    appScope.launch {
                        prefs.clearLastNames()
                        LoveTestSession.clear()
                        Toast.makeText(
                            context,
                            context.getString(R.string.settings_clear_saved_names_done),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
            )
        }
    }
}
