package dev.lovetest.app.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "love_test_prefs")

class AppPreferences(
    private val context: Context,
) {
    private val onboardingDone = booleanPreferencesKey("onboarding_completed")
    private val consentDone = booleanPreferencesKey("consent_completed")
    private val premiumActiveKey = booleanPreferencesKey("premium_active")
    private val lastName1Key = stringPreferencesKey("last_name1")
    private val lastName2Key = stringPreferencesKey("last_name2")
    private val sessionSnapshotKey = stringPreferencesKey("session_snapshot_v1")

    val onboardingCompleted: Flow<Boolean> =
        context.dataStore.data.map { it[onboardingDone] == true }

    val consentCompleted: Flow<Boolean> =
        context.dataStore.data.map { it[consentDone] == true }

    val isPremiumFlow: Flow<Boolean> =
        context.dataStore.data.map { it[premiumActiveKey] == true }

    suspend fun isOnboardingCompleted(): Boolean =
        context.dataStore.data.map { it[onboardingDone] == true }.first()

    suspend fun isConsentCompleted(): Boolean =
        context.dataStore.data.map { it[consentDone] == true }.first()

    suspend fun isPremium(): Boolean =
        context.dataStore.data.map { it[premiumActiveKey] == true }.first()

    suspend fun markOnboardingCompleted() {
        context.dataStore.edit { it[onboardingDone] = true }
    }

    suspend fun markConsentCompleted() {
        context.dataStore.edit { it[consentDone] = true }
    }

    suspend fun resetConsent() {
        context.dataStore.edit { it.remove(consentDone) }
    }

    suspend fun setPremium(active: Boolean) {
        context.dataStore.edit {
            if (active) {
                it[premiumActiveKey] = true
            } else {
                it.remove(premiumActiveKey)
            }
        }
    }

    suspend fun resetOnboarding() {
        context.dataStore.edit { it.remove(onboardingDone) }
    }

    suspend fun getLastNames(): Pair<String, String> {
        val prefs = context.dataStore.data.first()
        return Pair(
            prefs[lastName1Key].orEmpty(),
            prefs[lastName2Key].orEmpty(),
        )
    }

    suspend fun saveLastNames(name1: String, name2: String) {
        val n1 = name1.trim()
        val n2 = name2.trim()
        context.dataStore.edit { prefs ->
            if (n1.isNotEmpty()) prefs[lastName1Key] = n1
            if (n2.isNotEmpty()) prefs[lastName2Key] = n2
        }
    }

    suspend fun clearLastNames() {
        context.dataStore.edit { prefs ->
            prefs.remove(lastName1Key)
            prefs.remove(lastName2Key)
            prefs.remove(sessionSnapshotKey)
        }
    }

    suspend fun saveSessionSnapshot(encoded: String?) {
        context.dataStore.edit { prefs ->
            if (encoded.isNullOrBlank()) {
                prefs.remove(sessionSnapshotKey)
            } else {
                prefs[sessionSnapshotKey] = encoded
            }
        }
    }

    suspend fun loadSessionSnapshot(): String? =
        context.dataStore.data.first()[sessionSnapshotKey]
}
