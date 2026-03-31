package com.aria.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aria_settings")

class SettingsRepository(private val context: Context) {

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { pref ->
        AppSettings(
            openAiApiKey = pref[KEY_OPENAI].orEmpty(),
            claudeApiKey = pref[KEY_CLAUDE].orEmpty(),
            elevenLabsApiKey = pref[KEY_ELEVENLABS].orEmpty(),
            elevenLabsVoiceId = pref[KEY_VOICE_ID].orEmpty(),
            activeModel = pref[KEY_MODEL] ?: "gpt-4o-mini",
            personalityMode = pref[KEY_PERSONALITY] ?: "Companion",
            preferredLanguage = pref[KEY_LANGUAGE] ?: "Bangla/English Mix"
        )
    }

    suspend fun save(settings: AppSettings) {
        context.dataStore.edit { pref ->
            pref[KEY_OPENAI] = settings.openAiApiKey
            pref[KEY_CLAUDE] = settings.claudeApiKey
            pref[KEY_ELEVENLABS] = settings.elevenLabsApiKey
            pref[KEY_VOICE_ID] = settings.elevenLabsVoiceId
            pref[KEY_MODEL] = settings.activeModel
            pref[KEY_PERSONALITY] = settings.personalityMode
            pref[KEY_LANGUAGE] = settings.preferredLanguage
        }
    }

    private companion object {
        val KEY_OPENAI = stringPreferencesKey("openai")
        val KEY_CLAUDE = stringPreferencesKey("claude")
        val KEY_ELEVENLABS = stringPreferencesKey("elevenlabs")
        val KEY_VOICE_ID = stringPreferencesKey("voice_id")
        val KEY_MODEL = stringPreferencesKey("model")
        val KEY_PERSONALITY = stringPreferencesKey("personality")
        val KEY_LANGUAGE = stringPreferencesKey("language")
    }
}
