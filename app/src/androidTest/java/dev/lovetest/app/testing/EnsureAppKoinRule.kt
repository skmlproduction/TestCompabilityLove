package dev.lovetest.app.testing

import androidx.test.platform.app.InstrumentationRegistry
import dev.lovetest.app.di.appModule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/**
 * Ensures Application Koin is available before rules that launch [dev.lovetest.app.MainActivity].
 *
 * Soft-ensure: only (re)starts when GlobalContext is missing — avoids stop/start thrash that
 * races MainActivity compose under low-RAM instrumented runs.
 */
class EnsureAppKoinRule : TestWatcher() {
    override fun starting(description: Description) {
        if (GlobalContext.getOrNull() != null) return
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        runCatching {
            startKoin {
                androidContext(app)
                modules(appModule)
            }
        }.onFailure {
            runCatching { stopKoin() }
            startKoin {
                androidContext(app)
                modules(appModule)
            }
        }
    }
}
