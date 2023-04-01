package com.gabriel4k2.fluidsynthdemo.ui.providers

import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument




interface JNIInterface{
    abstract fun startPlayingNotesHandle(intervalInMs: Long, instrument: Instrument)
    abstract fun pauseSynthHandle()
}

