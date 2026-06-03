package dev.lovetest.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lovetest.app.BuildConfig
import dev.lovetest.app.R
import dev.lovetest.app.legal.LegalDocuments
import dev.lovetest.app.util.decorativeForAccessibility
import dev.lovetest.app.prefs.AppPreferences
import org.koin.compose.koinInject
import dev.lovetest.core.ui.components.LoveCardShadowElevation
import dev.lovetest.core.ui.components.LoveShadowCard
import dev.lovetest.core.ui.components.LoveGradientBackground
import dev.lovetest.core.ui.components.LoveHubBackgroundBlobs
import dev.lovetest.core.ui.theme.LoveOnPrimaryContainer
import dev.lovetest.core.ui.theme.LoveOnSurface
import dev.lovetest.core.ui.theme.LoveOnSurfaceVariant
import dev.lovetest.core.ui.theme.LoveOutlineVariant
import dev.lovetest.core.ui.theme.LovePrimary
import dev.lovetest.core.ui.theme.LovePrimaryContainer
import dev.lovetest.core.ui.theme.LoveSurface
import java.util.Locale

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onPremium: () -> Unit,
    onRestorePurchases: () -> Unit,
    onReplayOnboarding: () -> Unit,
    onLanguage: () -> Unit,
    onPrivacy: () -> Unit,
    onDataCollection: () -> Unit,
    onClearSavedNames: () -> Unit,
    onManageAdsConsent: () -> Unit,
    preferences: AppPreferences = koinInject(),
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    val isPremium by preferences.isPremiumFlow.collectAsStateWithLifecycle(initialValue = false)
    val localeCode = Locale.getDefault().language
    val languageBadge = if (localeCode == "ru") "RU" else "EN"
    val languageValue = stringResource(
        if (localeCode == "ru") R.string.settings_language_value_ru
        else R.string.settings_language_value_en,
    )
    val privacySubtitle = stringResource(
        if (LegalDocuments.hasExternalPrivacyPolicy()) {
            R.string.settings_privacy_sub
        } else {
            R.string.settings_privacy_unavailable
        },
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LoveGradientBackground(Modifier.fillMaxSize())
        LoveHubBackgroundBlobs(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = LovePrimary,
                    modifier = Modifier.decorativeForAccessibility(),
                )
                Text(
                    text = stringResource(R.string.nav_back),
                    color = LovePrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = LoveOnSurface,
                modifier = Modifier.padding(top = 8.dp),
            )

            SettingsGroupLabel(
                text = stringResource(R.string.settings_group_premium),
                modifier = Modifier.padding(top = 28.dp),
            )
            SettingsItemsCard(modifier = Modifier.padding(top = 8.dp)) {
                if (BuildConfig.ADS_ENABLED) {
                    SettingsRow(
                        iconBackground = Color(0xFFE3F2FD),
                        iconTint = Color(0xFF1565C0),
                        icon = Icons.Filled.Campaign,
                        title = stringResource(R.string.settings_ads_preferences),
                        subtitle = stringResource(R.string.settings_ads_preferences_sub),
                        showChevron = true,
                        onClick = onManageAdsConsent,
                    )
                    SettingsDivider()
                }
                SettingsRow(
                    iconBackground = Color(0xFFFFF8E1),
                    iconTint = Color(0xFFFF8F00),
                    icon = Icons.Filled.Star,
                    title = stringResource(R.string.settings_premium_title),
                    subtitle = stringResource(
                        if (isPremium) R.string.settings_premium_active
                        else R.string.settings_premium_sub,
                    ),
                    showChevron = true,
                    onClick = onPremium,
                )
                SettingsDivider()
                SettingsRow(
                    iconBackground = Color(0xFFF3EDF7),
                    iconTint = Color(0xFF6750A4),
                    icon = Icons.Filled.Refresh,
                    title = stringResource(R.string.settings_restore_purchases),
                    showChevron = true,
                    onClick = onRestorePurchases,
                )
            }

            SettingsGroupLabel(
                text = stringResource(R.string.settings_group_app),
                modifier = Modifier.padding(top = 24.dp),
            )
            SettingsItemsCard(modifier = Modifier.padding(top = 8.dp)) {
                SettingsRow(
                    iconBackground = LovePrimaryContainer,
                    iconTint = LovePrimary,
                    icon = Icons.Filled.Replay,
                    title = stringResource(R.string.settings_replay_onboarding),
                    subtitle = stringResource(R.string.settings_onboarding_sub),
                    showChevron = true,
                    onClick = onReplayOnboarding,
                )
                SettingsDivider()
                SettingsRow(
                    iconBackground = LovePrimaryContainer,
                    iconTint = LovePrimary,
                    iconBadge = languageBadge,
                    title = stringResource(R.string.settings_language_title),
                    trailingValue = languageValue,
                    showChevron = true,
                    onClick = onLanguage,
                )
                SettingsDivider()
                SettingsRow(
                    iconBackground = Color(0xFFF3EDF7),
                    iconTint = LoveOnSurfaceVariant,
                    icon = Icons.Filled.Info,
                    title = stringResource(R.string.settings_about_title),
                    subtitle = stringResource(R.string.settings_about_sub, BuildConfig.VERSION_NAME),
                    showChevron = true,
                    onClick = { showAboutDialog = true },
                )
            }

            SettingsGroupLabel(
                text = stringResource(R.string.settings_group_legal),
                modifier = Modifier.padding(top = 24.dp),
            )
            SettingsItemsCard(modifier = Modifier.padding(top = 8.dp)) {
                SettingsRow(
                    iconBackground = Color(0xFFE8EAF6),
                    iconTint = Color(0xFF512DA8),
                    icon = Icons.Filled.Policy,
                    title = stringResource(R.string.settings_privacy_open),
                    subtitle = privacySubtitle,
                    showChevron = true,
                    onClick = onPrivacy,
                )
                SettingsDivider()
                SettingsRow(
                    iconBackground = Color(0xFFE8EAF6),
                    iconTint = Color(0xFF512DA8),
                    icon = Icons.Outlined.Description,
                    title = stringResource(R.string.settings_data_collection),
                    subtitle = stringResource(R.string.settings_data_collection_sub),
                    showChevron = true,
                    onClick = onDataCollection,
                )
                SettingsDivider()
                SettingsRow(
                    iconBackground = Color(0xFFFFEBEE),
                    iconTint = Color(0xFFC2185B),
                    icon = Icons.Outlined.DeleteOutline,
                    title = stringResource(R.string.settings_clear_saved_names),
                    subtitle = stringResource(R.string.settings_clear_saved_names_sub),
                    showChevron = false,
                    onClick = onClearSavedNames,
                )
            }

            LoveShadowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                shape = RoundedCornerShape(28.dp),
                shadowElevation = LoveCardShadowElevation.Subtle,
                colors = CardDefaults.cardColors(containerColor = LovePrimaryContainer),
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Text(
                        text = stringResource(R.string.settings_disclaimer_line1),
                        style = MaterialTheme.typography.bodyLarge,
                        color = LoveOnPrimaryContainer,
                    )
                    Text(
                        text = stringResource(R.string.settings_disclaimer_line2),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LoveOnPrimaryContainer.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }

            Text(
                text = stringResource(R.string.settings_build_footer),
                style = MaterialTheme.typography.bodySmall,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            )
            Text(
                text = stringResource(R.string.settings_support_footer),
                style = MaterialTheme.typography.bodySmall,
                color = LoveOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 32.dp),
            )
        }

        if (showAboutDialog) {
            AboutDialog(onDismiss = { showAboutDialog = false })
        }
    }
}

@Composable
private fun SettingsGroupLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = LovePrimary,
        letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing,
        modifier = modifier,
    )
}

@Composable
private fun SettingsItemsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    LoveShadowCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = LoveCardShadowElevation.Card,
        colors = CardDefaults.cardColors(containerColor = LoveSurface),
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = LoveOutlineVariant,
        thickness = 1.dp,
    )
}

@Composable
private fun SettingsRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconBackground: Color = LovePrimaryContainer,
    iconTint: Color = LovePrimary,
    icon: ImageVector? = null,
    iconBadge: String? = null,
    subtitle: String? = null,
    trailingValue: String? = null,
    showChevron: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            when {
                iconBadge != null -> {
                    Text(
                        text = iconBadge,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = iconTint,
                    )
                }
                icon != null -> {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier
                            .decorativeForAccessibility()
                            .size(28.dp),
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = LoveOnSurface,
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoveOnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        trailingValue?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = LoveOnSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp),
            )
        }
        if (showChevron) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = LoveOnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.decorativeForAccessibility(),
            )
        }
    }
}
