package com.gabriel4k2.fluidsynthdemo.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Note(
    val chord : Chord,
    val octave: Octave,
    val selected: Boolean,
    val midiNumber: Int
) : Parcelable {
    override fun toString(): String {
        return chord.value+octave.value
    }

}
