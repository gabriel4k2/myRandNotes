package com.gabriel4k2.myRandNotes.domain.model

import com.gabriel4k2.myRandNotes.ui.model.AvailablePrecisions
import com.gabriel4k2.myRandNotes.ui.model.TimeInSeconds
import com.gabriel4k2.myRandNotes.ui.model.UINoteGenerationConfig
import com.gabriel4k2.myRandNotes.utils.NoteUtils

data class NoteGenerationConfig(val instrument: Instrument, val timeIntervalMs: Long, val notes: List<Note>) {
    fun toUINoteGenerationConfig(): UINoteGenerationConfig {
        val timeInSeconds = timeIntervalMs.div(1000)
        val precision = timeIntervalMs.mod(1000)
            .let { ms -> AvailablePrecisions.values().find { it.value == ms.toString() } } ?: AvailablePrecisions.ZERO
        return UINoteGenerationConfig(
            instrument = instrument,
            precision = precision,
            timeInSeconds = TimeInSeconds(timeInSeconds.toString()),
            notes = notes
        )
    }

    companion object {
        val INITIAL_CONFIG = NoteGenerationConfig(
            instrument = Instrument.INITIAL_INSTRUMENT,
            timeIntervalMs = 1000,
            notes = NoteUtils.generateInitialNoteList()
        )
        fun fromUINoteGenerationConfig(uiNoteGenerationConfig: UINoteGenerationConfig): NoteGenerationConfig {
            val timeInMs =
                uiNoteGenerationConfig.precision.value.toInt() +
                    uiNoteGenerationConfig.timeInSeconds.value.toInt().times(1000L)
            return NoteGenerationConfig(
                instrument = uiNoteGenerationConfig.instrument,
                timeIntervalMs = timeInMs,
                notes = uiNoteGenerationConfig.notes
            )
        }
    }
}
