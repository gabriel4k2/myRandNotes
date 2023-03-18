package com.gabriel4k2.fluidsynthdemo.domain.model

import android.util.Log
import com.gabriel4k2.fluidsynthdemo.ui.model.UINoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.time.AvailablePrecisions


data class NoteGenerationConfig(val instrument: Instrument, val timeIntervalMs: Long){
    fun toUINoteGenerationConfig() : UINoteGenerationConfig{
        val timeInSeconds = timeIntervalMs.div(1000)
        val precision = timeIntervalMs.mod(1000).let { ms -> AvailablePrecisions.values().find { it.value == ms.toString()  } } ?: AvailablePrecisions.ZERO
        return UINoteGenerationConfig(instrument = instrument, precision = precision, timeInSeconds = timeInSeconds.toString())
    }



    companion object {
        val INITIAL_CONFIG = NoteGenerationConfig(instrument = Instrument.INITIAL_INSTRUMENT, timeIntervalMs = 1000)
        fun fromUINoteGenerationConfig(uiNoteGenerationConfig: UINoteGenerationConfig): NoteGenerationConfig {
            val timeInMs =
                uiNoteGenerationConfig.precision.value.toInt() + uiNoteGenerationConfig.timeInSeconds.toInt().times(1000L)
            return NoteGenerationConfig(instrument = uiNoteGenerationConfig.instrument, timeIntervalMs = timeInMs)
        }
    }
}
