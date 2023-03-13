package com.gabriel4k2.fluidsynthdemo.ui.settings

import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.model.UIInstrument
import com.gabriel4k2.fluidsynthdemo.ui.time.AvailablePrecisions

sealed class SettingsChangeEvent {
    data class PrecisionChangeEvent(val precision: AvailablePrecisions) : SettingsChangeEvent()
    data class TimeChangeEvent(val time: String) : SettingsChangeEvent()
    data class InstrumentChangeEvent(val instrument: UIInstrument) : SettingsChangeEvent()

}