package com.eltafouk.masareefi.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "masareefi_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val KEY_GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val KEY_SELECTED_MODEL = stringPreferencesKey("selected_model")
        val KEY_FORCE_OFFLINE = booleanPreferencesKey("force_offline")
    }

    val geminiApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_GEMINI_API_KEY] ?: ""
    }

    val selectedModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_MODEL] ?: "gemini-2.5-flash"
    }

    val forceOffline: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_FORCE_OFFLINE] ?: false
    }

    suspend fun setGeminiApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_GEMINI_API_KEY] = key.trim()
        }
    }

    suspend fun setSelectedModel(model: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_MODEL] = model
        }
    }

    suspend fun setForceOffline(force: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FORCE_OFFLINE] = force
        }
    }
}
