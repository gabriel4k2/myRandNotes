package com.gabriel4k2.myRandNotes.ui.providers

import com.gabriel4k2.myRandNotes.domain.model.Instrument

interface JNIInterface {
    fun startPlayingNotesHandle(
        intervalInMs: Long,
        instrument: Instrument,
        playableNotesMidiNumbers: IntArray
    )

    fun pauseSynthHandle()
}
