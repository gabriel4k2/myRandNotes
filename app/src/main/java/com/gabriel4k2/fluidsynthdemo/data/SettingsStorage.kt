package com.gabriel4k2.fluidsynthdemo.data

import android.content.SharedPreferences
import com.gabriel4k2.fluidsynthdemo.domain.model.NoteGenerationConfig
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import javax.inject.Inject


class SettingsStorage @Inject constructor(
    private val preferences: SharedPreferences,
    private val moshi: Moshi
) {

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