package com.gabriel4k2.fluidsynthdemo.ui.model

import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.domain.model.Note

data class UINoteGenerationConfig(
    val precision: AvailablePrecisions,
    val timeInSeconds: TimeInSeconds,
    val instrument: Instrument,
    val notes: List<Note>
) {
    companion object {
        val UNKNOWN = UINoteGenerationConfig(
            precision = AvailablePrecisions.UNKNOWN,
            timeInSeconds = TimeInSeconds.UNKNOWN,
            instrument = Instrument.UNKNOWN,
            notes = emptyList()
        )
    }

}
