package dev.lovetest.app.ui.share

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R
import dev.lovetest.app.util.ShareTargetPackages
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.app.util.findActivity
import dev.lovetest.app.util.shareLoveShareCardImage
import dev.lovetest.app.util.shareLoveResult
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveTypographyTokens

@Composable
fun ShareActionsPanel(
    shareText: String,
    cardContent: ShareCardContent?,
    onDismiss: () -> Unit,
    onShareFallback: () -> Unit = {},
    modifier: Modifier = Modifier,
    wheelPrize: String? = null,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    fun shareImage(targetPackage: String?) {
        val activity = context.findActivity()
        val bitmap = when {
            activity == null -> null
            cardContent != null -> ShareCardImageExporter.captureLoveShareCard(activity, cardContent)
            wheelPrize != null -> ShareCardImageExporter.captureWheelShareCard(activity, wheelPrize)
            else -> null
        }
        if (bitmap == null) {
            onShareFallback()
            context.shareLoveResult()
            return
        }
        val sent = context.shareLoveShareCardImage(
            bitmap = bitmap,
            shareText = shareText,
            targetPackage = targetPackage,
        )
        if (!sent) {
            onShareFallback()
            context.shareLoveResult()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
            .background(Color(0xFF2B2930))
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ShareTarget(
                label = stringResource(R.string.share_target_telegram),
                color = Color(0xFF29B6F6),
                icon = Icons.AutoMirrored.Filled.Send,
                onClick = {
                    shareImage(ShareTargetPackages.TELEGRAM)
                    onDismiss()
                },
            )
            ShareTarget(
                label = stringResource(R.string.share_target_whatsapp),
                color = Color(0xFF25D366),
                icon = Icons.Filled.Share,
                onClick = {
                    shareImage(ShareTargetPackages.WHATSAPP)
                    onDismiss()
                },
            )
            ShareTarget(
                label = stringResource(R.string.share_target_copy),
                color = LovePrimaryContainer,
                icon = Icons.Filled.ContentCopy,
                labelColor = LoveOnSurfaceVariant,
                iconTint = LoveOnSurfaceVariant,
                onClick = {
                    clipboard.setText(AnnotatedString(shareText))
                    Toast.makeText(
                        context,
                        context.getString(R.string.share_copied_toast),
                        Toast.LENGTH_SHORT,
                    ).show()
                    onDismiss()
                },
            )
            ShareTarget(
                label = stringResource(R.string.share_target_more),
                color = Color(0xFF49454F),
                icon = Icons.Filled.MoreHoriz,
                onClick = {
                    shareImage(targetPackage = null)
                    onDismiss()
                },
            )
        }
        val cancelLabel = stringResource(R.string.share_cancel)
        Box(
            modifier = Modifier
                .padding(top = 28.dp)
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(44.dp))
                .background(Color(0xFF49454F))
                .semantics {
                    role = Role.Button
                    contentDescription = cancelLabel
                }
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = cancelLabel,
                style = LoveTypographyTokens.CardTitle,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun ShareTarget(
    label: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    labelColor: Color = Color.White.copy(0.9f),
    iconTint: Color = Color.White,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = label
            }
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(32.dp)
                    .decorativeForAccessibility(),
            )
        }
        Text(
            text = label,
            style = LoveTypographyTokens.CardCaption,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
