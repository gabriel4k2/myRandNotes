package com.gabriel4k2.myRandNotes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Chord(val value: String, val order: Int) : Parcelable {
    C("C", 0),
    CSHARP("C#", 1),
    D("D", 2),
    DSHARP("D#", 3),
    E("E", 4),
    F("F", 5),
    FSHARP("F#", 6),
    G("G", 7),
    GSHARP("G#", 8),
    A("A", 9),
    ASHARP("A#", 10),
    B("B", 11);

    companion object {
        fun orderedChords(): List<Chord> {
            return Chord.values().sortedBy { it.order }
        }
    }
}
