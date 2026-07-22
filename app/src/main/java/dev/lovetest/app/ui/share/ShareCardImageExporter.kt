package dev.lovetest.app.ui.share

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dev.lovetest.core.ui.components.LoveLayout
import dev.lovetest.core.ui.theme.LoveTestTheme

internal object ShareCardDimensions {
    /** screen27 share card at 3× (360dp × 387dp). */
    const val WIDTH_PX = 1080
    const val HEIGHT_PX = 1161
}

internal object ShareCardImageExporter {

    private const val TAG = "ShareCardCapture"

    fun captureLoveShareCard(
        activity: Activity,
        content: ShareCardContent,
    ): Bitmap? = capture(activity) {
        LoveShareCard(
            percent = content.percent,
            name1 = content.name1,
            name2 = content.name2,
            harmonyTag = content.harmonyTag,
            high = content.high,
            modifier = Modifier
                .width(360.dp)
                .height(LoveLayout.LoveShareCardHeight),
        )
    }

    fun captureWheelShareCard(
        activity: Activity,
        prize: String,
    ): Bitmap? = capture(activity) {
        WheelShareCard(
            prize = prize,
            contentDescription = "",
            modifier = Modifier
                .width(360.dp)
                .height(LoveLayout.LoveShareCardHeight),
        )
    }

    private fun capture(
        activity: Activity,
        content: @Composable () -> Unit,
    ): Bitmap? = runCatching {
        val lifecycleOwner = activity as? ComponentActivity
            ?: error("Share card capture requires ComponentActivity")
        check(!activity.isDestroyed) { "Activity destroyed before share capture" }

        val host = FrameLayout(activity).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        }
        val composeView = ComposeView(activity).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                LoveTestTheme { content() }
            }
        }
        host.addView(
            composeView,
            ViewGroup.LayoutParams(
                ShareCardDimensions.WIDTH_PX,
                ShareCardDimensions.HEIGHT_PX,
            ),
        )

        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            ShareCardDimensions.WIDTH_PX,
            View.MeasureSpec.EXACTLY,
        )
        val heightSpec = View.MeasureSpec.makeMeasureSpec(
            ShareCardDimensions.HEIGHT_PX,
            View.MeasureSpec.EXACTLY,
        )

        // Prefer attaching to decor when the activity window is still live; otherwise
        // measure/draw off-window (sibling apps on shared AVDs may steal focus).
        val decor = activity.window?.decorView as? ViewGroup
        val attached = decor != null && !activity.isFinishing && !activity.isDestroyed
        if (attached) {
            decor!!.addView(host, 0, 0)
        }
        try {
            host.measure(widthSpec, heightSpec)
            host.layout(0, 0, ShareCardDimensions.WIDTH_PX, ShareCardDimensions.HEIGHT_PX)
            composeView.measure(widthSpec, heightSpec)
            composeView.layout(0, 0, ShareCardDimensions.WIDTH_PX, ShareCardDimensions.HEIGHT_PX)
            // Flush pending layout/draw without posting (callers are often on main).
            composeView.invalidate()
            host.invalidate()
            val bitmap = Bitmap.createBitmap(
                ShareCardDimensions.WIDTH_PX,
                ShareCardDimensions.HEIGHT_PX,
                Bitmap.Config.ARGB_8888,
            )
            composeView.draw(Canvas(bitmap))
            // Second pass after Compose has applied first measure.
            composeView.measure(widthSpec, heightSpec)
            composeView.layout(0, 0, ShareCardDimensions.WIDTH_PX, ShareCardDimensions.HEIGHT_PX)
            composeView.draw(Canvas(bitmap))
            if (isMostlyBlank(bitmap)) {
                error("share card bitmap is blank")
            }
            bitmap
        } finally {
            if (attached) {
                runCatching { decor!!.removeView(host) }
            }
        }
    }.onFailure { err ->
        Log.e(TAG, "capture failed", err)
    }.getOrNull()

    /** Sample corners + center; reject near-empty captures so callers can fall back. */
    private fun isMostlyBlank(bitmap: Bitmap): Boolean {
        val points = listOf(
            0 to 0,
            bitmap.width - 1 to 0,
            0 to bitmap.height - 1,
            bitmap.width - 1 to bitmap.height - 1,
            bitmap.width / 2 to bitmap.height / 2,
            bitmap.width / 4 to bitmap.height / 4,
            bitmap.width * 3 / 4 to bitmap.height * 3 / 4,
        )
        var opaque = 0
        for ((x, y) in points) {
            val c = bitmap.getPixel(x, y)
            val a = (c ushr 24) and 0xff
            val r = (c ushr 16) and 0xff
            val g = (c ushr 8) and 0xff
            val b = c and 0xff
            if (a > 8 && (r + g + b) > 24) opaque++
        }
        return opaque < 2
    }
}
