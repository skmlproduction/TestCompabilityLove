package dev.lovetest.app.debug

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import dev.lovetest.app.MainActivity
import dev.lovetest.app.navigation.NavIntents

/**
 * Debug entry for screenshot capture: forwards preview extras to [MainActivity].
 */
class DebugUiPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val target = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.getStringExtra(NavIntents.EXTRA_DEBUG_UI_PREVIEW)?.let {
                putExtra(NavIntents.EXTRA_DEBUG_UI_PREVIEW, it)
            }
            intent.getStringExtra(NavIntents.EXTRA_DEBUG_START_ROUTE)?.let {
                putExtra(NavIntents.EXTRA_DEBUG_START_ROUTE, it)
            }
        }
        startActivity(target)
        finish()
    }
}
