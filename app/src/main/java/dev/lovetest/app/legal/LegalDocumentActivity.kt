package dev.lovetest.app.legal

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import dev.lovetest.app.R
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveSurface
import dev.lovetest.core.ui.theme.LoveTestTheme

class LegalDocumentActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val assetPath = intent.getStringExtra(EXTRA_ASSET).orEmpty()
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val assetUrl = "file:///android_asset/$assetPath"

        setContent {
            LoveTestTheme {
                Scaffold(
                    containerColor = LoveSurface,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = title,
                                    color = LoveOnSurface,
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = onBackPressedDispatcher::onBackPressed) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.nav_back),
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = LoveSurface,
                            ),
                        )
                    },
                ) { padding ->
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = false
                                settings.allowFileAccess = false
                                settings.allowContentAccess = false
                                settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    settings.safeBrowsingEnabled = true
                                }
                                loadUrl(assetUrl)
                            }
                        },
                        update = { webView ->
                            if (webView.url != assetUrl) {
                                webView.loadUrl(assetUrl)
                            }
                        },
                    )
                }
            }
        }
    }

    companion object {
        private const val EXTRA_ASSET = "legal_asset"
        private const val EXTRA_TITLE = "legal_title"

        fun intent(context: Context, assetPath: String, title: String): Intent =
            Intent(context, LegalDocumentActivity::class.java).apply {
                putExtra(EXTRA_ASSET, assetPath)
                putExtra(EXTRA_TITLE, title)
            }
    }
}
