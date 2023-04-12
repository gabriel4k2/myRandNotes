package com.gabriel4k2.fluidsynthdemo.ui.model

import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.domain.model.Note

sealed class ConfigChangeEvent {
    data class PrecisionChangeEvent(val precision: AvailablePrecisions) : ConfigChangeEvent()
    data class TimeChangeEvent(val time: String) : ConfigChangeEvent()
    data class InstrumentChangeEvent(val instrument: Instrument) : ConfigChangeEvent()
    data class NoteRangeChangeEvent(val notes: List<Note>) : ConfigChangeEvent()
}