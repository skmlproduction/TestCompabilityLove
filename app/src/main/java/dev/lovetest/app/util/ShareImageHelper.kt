package dev.lovetest.app.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import dev.lovetest.app.R
import java.io.File
import java.io.FileOutputStream

object ShareTargetPackages {
    const val TELEGRAM = "org.telegram.messenger"
    const val WHATSAPP = "com.whatsapp"
}

fun Context.isShareTargetInstalled(packageName: String): Boolean =
    packageManager.getLaunchIntentForPackage(packageName) != null

fun Context.shareLoveShareCardImage(
    bitmap: Bitmap,
    shareText: String,
    chooserTitle: String = getString(R.string.share_chooser_title),
    targetPackage: String? = null,
): Boolean {
    val uri = saveShareBitmap(bitmap) ?: return false
    val resolvedPackage = targetPackage?.takeIf { isShareTargetInstalled(it) }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, shareText)
        clipData = ClipData.newUri(contentResolver, "image", uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        resolvedPackage?.let { setPackage(it) }
    }

    return runCatching {
        if (resolvedPackage != null) {
            grantUriPermission(
                resolvedPackage,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
            startActivity(intent)
        } else {
            // Chooser targets need an explicit grant; FLAG alone is not enough on many OEMs.
            packageManager.queryIntentActivities(intent, 0).forEach { resolve ->
                grantUriPermission(
                    resolve.activityInfo.packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            startActivity(Intent.createChooser(intent, chooserTitle))
        }
        true
    }.getOrDefault(false)
}

private fun Context.saveShareBitmap(bitmap: Bitmap): Uri? = runCatching {
    val dir = File(cacheDir, "share").apply { mkdirs() }
    val file = File(dir, "love_share_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    FileProvider.getUriForFile(this, "${packageName}.share.fileprovider", file)
}.getOrNull()
