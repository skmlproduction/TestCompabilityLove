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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.lovetest.app.R

@Composable
fun ShareActionsPanel(
    shareText: String,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

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
                onClick = {
                    onShare()
                    onDismiss()
                },
            )
            ShareTarget(
                label = stringResource(R.string.share_target_whatsapp),
                color = Color(0xFF25D366),
                onClick = {
                    onShare()
                    onDismiss()
                },
            )
            ShareTarget(
                label = stringResource(R.string.share_target_copy),
                color = Color(0xFFF3EDF7),
                labelColor = Color(0xFF49454F),
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
                onClick = {
                    onShare()
                    onDismiss()
                },
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 28.dp)
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(44.dp))
                .background(Color(0xFF49454F))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.share_cancel),
                style = MaterialTheme.typography.titleMedium,
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
    onClick: () -> Unit,
    labelColor: Color = Color.White.copy(0.9f),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
