package com.gabriel4k2.myRandNotes.data

import android.content.Context
import android.content.SharedPreferences
import com.gabriel4k2.myRandNotes.domain.model.NoteGenerationConfig
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class SettingsStorage constructor(
    val context: Context,
    moshi: Moshi
) {
    private val preferences: SharedPreferences = context.getSharedPreferences("MyPref", 0)

    private val adapter: JsonAdapter<NoteGenerationConfig> =
        moshi.adapter(NoteGenerationConfig::class.java)

    private fun initialConfigLoad(): NoteGenerationConfig {
        val initialConfig = NoteGenerationConfig.INITIAL_CONFIG
        saveSettings(initialConfig)
        return initialConfig
    }

    fun saveSettings(settings: NoteGenerationConfig) {
        val settingsJSON = adapter.toJson(settings)

        preferences.edit().apply {
            this.putString(SETTINGS_KEY, settingsJSON)
            this.apply()
        }
    }

    fun getSettingsOrDefaultToInitial(): NoteGenerationConfig {
        val settingsJson = preferences.getString(SETTINGS_KEY, null)
        return if (settingsJson == null) {
            initialConfigLoad()
        } else {
            settingsJson.let { adapter.fromJson(it) }!!
        }
    }

    companion object {
        const val SETTINGS_KEY = "settings"
    }
}
