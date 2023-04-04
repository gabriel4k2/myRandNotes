package com.gabriel4k2.fluidsynthdemo.utils

import com.gabriel4k2.fluidsynthdemo.domain.model.Chord
import com.gabriel4k2.fluidsynthdemo.domain.model.Note
import com.gabriel4k2.fluidsynthdemo.domain.model.Octave


typealias MidiToNoteMap = HashMap<Int, Note>

fun MidiToNoteMap.naturalMusicOrder() : List<Note>{
    return this.values.sortedWith( compareBy<Note> { it.chord  }.thenBy { it.octave })
}

object NoteUtils {

    /* Simple pattern to get the fully qualified note name (name and octave) from the midi
       note's numbers (up to the sixth octave):
        C0 -> 0
        C1 -> 12
        C2 -> 24
        ...
        C#0 -> 1
        C#1 -> 13
        ...

        B0 -> 11
        ...
        B6 -> 83
     */


    fun generateMidiNumberToNoteNameMap(): MidiToNoteMap {
        val chord = Chord.orderedChords()

        val octaves = Octave.orderedOctaves()
        val midiToNoteMap = MidiToNoteMap()
        chord.forEachIndexed { chordIndex, chord ->
            octaves.forEachIndexed { octaveIndex, octave ->
                val index = 12 * octaveIndex + chordIndex
                midiToNoteMap[index] = Note(chord, octave)

            }
        }
        return midiToNoteMap
    }

}