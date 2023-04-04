package com.gabriel4k2.fluidsynthdemo.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


// Effective java tip, set an order value such that the "values" returns
// a deteministic order
@Parcelize
enum class Chord(val value : String,  val order : Int) : Parcelable {
    C("C", 1),
    CSHARP("C#", 2),
    D("D", 3),
    DSHARP("D#", 4),
    E("E", 5),
    F("F", 6),
    FSHARP("F#",7),
    GSHARP("G#", 8),
    A("A",9),
    B("B", 10);
    companion object {
        fun orderedChords(): List<Chord> {
            return Chord.values().sortedBy { it.order }
        }
    }
}
