package com.gabriel4k2.fluidsynthdemo.ui.model

import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument

data class UINoteGenerationConfig(
    val precision: AvailablePrecisions,
    val timeInSeconds: TimeInSeconds,
    val instrument: Instrument
) {
    companion object {
        val UNKNOWN = UINoteGenerationConfig(
            precision = AvailablePrecisions.UNKNOWN,
            timeInSeconds = TimeInSeconds.UNKNOWN,
            instrument = Instrument.UNKNOWN
        )
    }

}
