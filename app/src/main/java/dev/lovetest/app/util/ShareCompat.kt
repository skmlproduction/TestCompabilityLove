package dev.lovetest.app.util

import android.content.Context
import android.content.Intent
import dev.lovetest.app.R
import dev.lovetest.app.session.LoveTestSession

internal fun formatLoveShareText(
    appName: String,
    footer: String,
    percent: Int,
    name1: String,
    name2: String,
): String = buildString {
    append(appName)
    append(": ")
    append(percent)
    append("% — ")
    append(name1)
    append(" + ")
    append(name2)
    append('\n')
    append(footer)
}

internal fun formatWheelShareText(
    appName: String,
    wheelKicker: String,
    footer: String,
    prize: String,
): String = buildString {
    append(appName)
    append(" — ")
    append(wheelKicker)
    append('\n')
    append(prize)
    append('\n')
    append(footer)
}

fun Context.buildLoveShareText(
    percent: Int = LoveTestSession.percent,
    name1: String = LoveTestSession.name1,
    name2: String = LoveTestSession.name2,
): String = formatLoveShareText(
    appName = getString(R.string.app_name),
    footer = getString(R.string.share_card_footer),
    percent = percent,
    name1 = name1,
    name2 = name2,
)

internal fun formatProtocolShareText(
    appName: String,
    protocolTitle: String,
    footer: String,
    percent: Int,
    name1: String,
    name2: String,
): String = buildString {
    append(appName)
    append(" — ")
    append(protocolTitle)
    append(": ")
    append(percent)
    append("% — ")
    append(name1)
    append(" + ")
    append(name2)
    append('\n')
    append(footer)
}

fun Context.buildProtocolShareText(
    percent: Int = LoveTestSession.percent,
    name1: String = LoveTestSession.name1,
    name2: String = LoveTestSession.name2,
): String = formatProtocolShareText(
    appName = getString(R.string.app_name),
    protocolTitle = getString(R.string.protocol_title),
    footer = getString(R.string.share_card_footer),
    percent = percent,
    name1 = name1,
    name2 = name2,
)

fun Context.buildWheelShareText(
    prize: String = LoveTestSession.name1.ifBlank { "…" },
): String = formatWheelShareText(
    appName = getString(R.string.app_name),
    wheelKicker = getString(R.string.share_external_wheel_kicker),
    footer = getString(R.string.share_card_footer),
    prize = prize,
)

fun Context.shareLoveResult() {
    sharePlainText(buildLoveShareText())
}

fun Context.shareWheelIdea() {
    sharePlainText(buildWheelShareText())
}

fun Context.shareProtocolResult() {
    sharePlainText(buildProtocolShareText())
}

private fun Context.sharePlainText(text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, getString(R.string.share_chooser_title)))
}
