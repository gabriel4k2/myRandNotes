package com.gabriel4k2.myRandNotes.ui.model

import com.gabriel4k2.myRandNotes.domain.model.Instrument
import com.gabriel4k2.myRandNotes.domain.model.Note

sealed class ConfigChangeEvent {
    data class PrecisionChangeEvent(val precision: AvailablePrecisions) : ConfigChangeEvent()
    data class TimeChangeEvent(val time: String) : ConfigChangeEvent()
    data class InstrumentChangeEvent(val instrument: Instrument) : ConfigChangeEvent()
    data class NoteRangeChangeEvent(val notes: List<Note>) : ConfigChangeEvent()
}
