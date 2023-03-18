package com.gabriel4k2.fluidsynthdemo.ui.model

import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.time.AvailablePrecisions

data class UINoteGenerationConfig(val precision: AvailablePrecisions, val timeInSeconds : String, val instrument: Instrument)
