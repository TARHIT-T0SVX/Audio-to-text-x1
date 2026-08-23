package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "transcribe_preferences")

enum class AppThemeMode {
    LIGHT, DARK, SYSTEM
}

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.LIGHT,
    val selectedLanguageCode: String = "en-UK",
    val selectedLanguageName: String = "English (UK)",
    val isBackgroundRecordingEnabled: Boolean = true,
    val isNoiseSuppressionEnabled: Boolean = true,
    val isBiometricLockEnabled: Boolean = false,
    val defaultExportFormat: String = "md"
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SELECTED_LANGUAGE_CODE = stringPreferencesKey("selected_language_code")
        val SELECTED_LANGUAGE_NAME = stringPreferencesKey("selected_language_name")
        val BACKGROUND_RECORDING = booleanPreferencesKey("background_recording")
        val NOISE_SUPPRESSION = booleanPreferencesKey("noise_suppression")
        val BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
        val DEFAULT_EXPORT_FORMAT = stringPreferencesKey("default_export_format")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val themeString = preferences[PreferencesKeys.THEME_MODE] ?: AppThemeMode.LIGHT.name
        val themeMode = try {
            AppThemeMode.valueOf(themeString)
        } catch (e: Exception) {
            AppThemeMode.LIGHT
        }

        UserPreferences(
            themeMode = themeMode,
            selectedLanguageCode = preferences[PreferencesKeys.SELECTED_LANGUAGE_CODE] ?: "en-UK",
            selectedLanguageName = preferences[PreferencesKeys.SELECTED_LANGUAGE_NAME] ?: "English (UK)",
            isBackgroundRecordingEnabled = preferences[PreferencesKeys.BACKGROUND_RECORDING] ?: true,
            isNoiseSuppressionEnabled = preferences[PreferencesKeys.NOISE_SUPPRESSION] ?: true,
            isBiometricLockEnabled = preferences[PreferencesKeys.BIOMETRIC_LOCK] ?: false,
            defaultExportFormat = preferences[PreferencesKeys.DEFAULT_EXPORT_FORMAT] ?: "md"
        )
    }

    suspend fun updateThemeMode(themeMode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun updateSelectedLanguage(code: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE_CODE] = code
            preferences[PreferencesKeys.SELECTED_LANGUAGE_NAME] = name
        }
    }

    suspend fun setBackgroundRecordingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKGROUND_RECORDING] = enabled
        }
    }

    suspend fun setNoiseSuppressionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOISE_SUPPRESSION] = enabled
        }
    }

    suspend fun setBiometricLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_LOCK] = enabled
        }
    }

    suspend fun setDefaultExportFormat(format: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_EXPORT_FORMAT] = format
        }
    }
}
