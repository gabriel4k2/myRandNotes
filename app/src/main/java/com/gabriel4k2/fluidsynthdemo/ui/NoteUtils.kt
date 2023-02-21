package com.gabriel4k2.fluidsynthdemo.ui


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


    fun generateMidiNumberToNoteNameMap(): Map<Int, String> {
        val midiToNoteMap = HashMap<Int, String>()

        for (i in 0..83) {

            val note = when (i % 12) {
                0 -> "C"
                1 -> "C#"
                2 -> "D"
                3 -> "D#"
                4 -> "E"
                5 -> "F"
                6 -> "F#"
                7 -> "G"
                8 -> "G#"
                9 -> "A"
                10 -> "A#"
                11 -> "B"
                else -> "C"


            }

            val octave = (i/12).toString()

            midiToNoteMap[i] = note+octave

        }

        return midiToNoteMap
    }

}