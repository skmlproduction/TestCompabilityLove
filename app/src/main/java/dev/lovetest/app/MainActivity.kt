package dev.lovetest.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dev.lovetest.app.debug.DebugUiPreview
import dev.lovetest.app.navigation.LoveTestNavHost
import dev.lovetest.app.navigation.NavIntents
import dev.lovetest.app.navigation.Routes
import dev.lovetest.app.monetization.AdMobInterstitialManager
import dev.lovetest.app.monetization.AdsConsentManager
import dev.lovetest.app.monetization.bootstrapAdsIfAllowed
import dev.lovetest.app.prefs.AppPreferences
import dev.lovetest.core.ui.theme.LoveTestTheme
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    private val adsConsentManager: AdsConsentManager by inject()
    private val adMobManager: AdMobInterstitialManager by inject()
    private val preferences: AppPreferences by inject()

    private companion object {
        val DEBUG_ALLOWED_ROUTES: Set<String> = Routes.allDestinations().toSet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashReady = AtomicBoolean(false)
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { !splashReady.get() }
        super.onCreate(savedInstanceState)
        if (BuildConfig.ADS_ENABLED) {
            adsConsentManager.requestConsentInfoUpdate(this) {
                lifecycleScope.launch {
                    bootstrapAdsIfAllowed(
                        applicationContext,
                        preferences,
                        adsConsentManager,
                        adMobManager,
                    )
                }
            }
        }
        if (BuildConfig.DEBUG) {
            DebugUiPreview.applyFromIntent(intent)
            DebugUiPreview.seedLoveResultForPreview()
        }
        enableEdgeToEdge()
        setContent {
            LoveTestTheme {
                SideEffect { splashReady.set(true) }
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()
                    val startDestination = remember {
                        resolveStartDestination(intent)
                    }
                    if (BuildConfig.DEBUG) {
                        intent.removeExtra(NavIntents.EXTRA_DEBUG_START_ROUTE)
                        intent.removeExtra(NavIntents.EXTRA_DEBUG_UI_PREVIEW)
                    }
                    LoveTestNavHost(
                        navController = navController,
                        appScope = scope,
                        startDestination = startDestination,
                    )
                }
            }
        }
    }

    private fun resolveStartDestination(intent: Intent): String {
        if (BuildConfig.DEBUG) {
            DebugUiPreview.startRoute()?.let { return it }
            val raw = intent.getStringExtra(NavIntents.EXTRA_DEBUG_START_ROUTE)?.trim().orEmpty()
            if (raw.isNotEmpty() && raw in DEBUG_ALLOWED_ROUTES) return raw
        }
        return Routes.Splash
    }
}
