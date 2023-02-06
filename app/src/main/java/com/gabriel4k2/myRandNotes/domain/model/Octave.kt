package com.gabriel4k2.myRandNotes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Octave(val value: String) : Parcelable {
    FIRST("1"),
    SECOND("2"),
    THIRD("3"),
    FOURTH("4"),
    FIFTH("5"),
    SIXTH("6");
    companion object {
        fun orderedOctaves(): List<Octave> {
            return Octave.values().sortedBy { it.value }
        }
    }
}
