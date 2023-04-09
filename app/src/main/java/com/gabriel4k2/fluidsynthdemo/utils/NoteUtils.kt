package com.gabriel4k2.fluidsynthdemo.utils

import com.gabriel4k2.fluidsynthdemo.domain.model.Chord
import com.gabriel4k2.fluidsynthdemo.domain.model.Note
import com.gabriel4k2.fluidsynthdemo.domain.model.Octave


typealias MidiToNoteMap = HashMap<Int, Note>



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
    fun generateInitialNoteList(): List<Note> {
        val chord = Chord.orderedChords()
        val octaves = Octave.orderedOctaves()
        val noteList : MutableList<Note> = emptyList<Note>().toMutableList()
        chord.forEachIndexed { chordIndex, chord ->
            octaves.forEachIndexed { octaveIndex, octave ->
                val midiNumber = 12 * octaveIndex + chordIndex
                noteList.add(Note(chord = chord, octave= octave, midiNumber =  midiNumber, selected = true))

            }
        }
        return noteList
    }

    fun List<Note>.naturalMusicOrder() : List<Note> {
        return this.sortedWith( compareBy<Note> { it.chord  }.thenBy { it.octave })
    }
}